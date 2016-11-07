package com.lwouis.f9;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.inject.Inject;

import org.boris.pecoff4j.PE;
import org.boris.pecoff4j.ResourceDirectory;
import org.boris.pecoff4j.ResourceEntry;
import org.boris.pecoff4j.constant.ResourceType;
import org.boris.pecoff4j.io.PEParser;
import org.boris.pecoff4j.io.ResourceParser;
import org.boris.pecoff4j.resources.StringPair;
import org.boris.pecoff4j.resources.StringTable;
import org.boris.pecoff4j.resources.VersionInfo;
import org.boris.pecoff4j.util.ResourceHelper;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lwouis.f9.injection.InjectLogger;
import com.lwouis.f9.models.Item;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import sun.awt.shell.ShellFolder;

@Component
public class WindowsFileAnalyzer {

  @InjectLogger
  private Logger logger;

  private final AppState appState;

  @Inject
  public WindowsFileAnalyzer(AppState appState) {
    this.appState = appState;
  }

  public List<Item> itemsFromBackgroundThreadInspections(Collection<File> files) {
    ExecutorService threadPool = threadPool();
    CountDownLatch countDownLatch = new CountDownLatch(files.size() * 2);
    ArrayList<Item> itemList = new ArrayList<>();
    for (File file : files) {
      Item defaultItem = defaultItem(file);
      updateItemPropertiesOnBackgroundThreads(threadPool, defaultItem, file, countDownLatch);
      itemList.add(defaultItem);
    }
    updateUi(itemList);
    waitForCountDownOnBackgroundThread(threadPool, countDownLatch, itemList);
    return itemList;
  }

  private void updateUi(ArrayList<Item> itemList) {
    Platform.runLater(() -> appState.getObservableItemList().addAll(itemList));
  }

  private Item defaultItem(File file) {
    return new Item(file.getName(), file.getAbsolutePath(), "", null);
  }

  private ExecutorService threadPool() {
    String threadName = Environment.APP_NAME + " FileInspectionTask %d";
    ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat(threadName)
        .setUncaughtExceptionHandler((t, e) -> logger.error(threadName + " failed to finish.", e)).build();
    return Executors.newFixedThreadPool(10, threadFactory);
  }

  private void updateItemPropertiesOnBackgroundThreads(ExecutorService threadPool, Item item, File file,
      CountDownLatch countDownLatch) {
    updateItemPropertyOnBackgroundThread(threadPool, item.nameProperty(), () -> getProductName(file), countDownLatch);
    updateItemPropertyOnBackgroundThread(threadPool, item.iconProperty(), () -> getFileIcon(file), countDownLatch);
  }

  private void updateItemPropertyOnBackgroundThread(ExecutorService threadPool, final Property property,
      Callable<Object> function, final CountDownLatch countDownLatch) {
    threadPool.submit(new Task() {
      @Override
      protected Void call() throws Exception {
        try {
          Object name = function.call();
          property.setValue(name);
          countDownLatch.countDown();
        }
        catch (Throwable t) {
          logger.error("Failed to update item.", t);
        }
        return null;
      }
    });
  }

  public String getProductName(File file) {
    try {
      PE pe = PEParser.parse(file);
      ResourceDirectory rd = pe.getImageData().getResourceTable();
      ResourceEntry[] entries = ResourceHelper.findResources(rd, ResourceType.VERSION_INFO);
      if (entries.length == 0) {
        logger.info("Failed to retrieve pretty file name for file: {}.", file.getAbsolutePath());
        return file.getName();
      }
      VersionInfo versionInfo = ResourceParser.readVersionInfo(entries[0].getData());
      StringTable properties = versionInfo.getStringFileInfo().getTable(0);
      for (int i = 0; i < properties.getCount(); i++) {
        StringPair property = properties.getString(i);
        if (property.getKey().equals("ProductName")) {
          return property.getValue();
        }
      }
      logger.info("Failed to retrieve pretty file name for file: {}.", file.getAbsolutePath());
      return file.getName();
    }
    catch (Throwable t) {
      logger.info("Failed to retrieve pretty file name for file: {}.", file.getAbsolutePath(), t);
      return file.getName();
    }
  }

  private Image getFileIcon(File file) {
    try {
      java.awt.Image icon = ShellFolder.getShellFolder(file).getIcon(true); // true is 32x32, false if 16x16
      BufferedImage bufferedImage = new BufferedImage(icon.getWidth(null), icon.getHeight(null),
          BufferedImage.TYPE_INT_ARGB);
      Graphics2D bGr = bufferedImage.createGraphics();
      bGr.drawImage(icon, 0, 0, null);
      bGr.dispose();
      WritableImage writableImage = SwingFXUtils.toFXImage(bufferedImage, null);
      return writableImage;
    }
    catch (Throwable t) {
      logger.info("Failed to retrieve icon for file: {}.", file.getAbsolutePath());
      return null;
    }
  }

  private void waitForCountDownOnBackgroundThread(ExecutorService threadPool, CountDownLatch countDownLatch,
      ArrayList<Item> itemList) {
    String threadName = Environment.APP_NAME + " PersistOnceAllItemsAreUpdated";
    Thread thread = new Thread(() -> {
      try {
        countDownLatch.await();
        threadPool.shutdown();
        appState.addItems(itemList);
      }
      catch (Throwable t) {
        logger.error("Failed to wait for all FileInspectionTask to finish.", t);
      }
    }, threadName);
    thread.setDaemon(true);
    thread.start();
  }
}

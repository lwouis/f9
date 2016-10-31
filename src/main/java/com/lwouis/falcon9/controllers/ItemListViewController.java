package com.lwouis.falcon9.controllers;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.Collator;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.stackoverflowusers.file.WindowsShortcut;

import com.lwouis.falcon9.AppState;
import com.lwouis.falcon9.Keyboard;
import com.lwouis.falcon9.StageManager;
import com.lwouis.falcon9.injection.InjectLogger;
import com.lwouis.falcon9.models.Item;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import sun.awt.shell.ShellFolder;

@Component
public class ItemListViewController implements Initializable, ApplicationContextAware, InitializingBean {

  private final String networkFilePrefix = "\\\\";

  private final List<String> buggyShortcutResolutions = Arrays.asList(networkFilePrefix, ".", "i");

  private double opacity = 1;

  private FilteredList<Item> filteredItemList;

  @FXML
  private ListView<Item> itemListView;

  private final AppState appState;

  private SearchTextViewController searchTextViewController;

  private final StageManager stageManager;

  @InjectLogger
  private Logger logger;

  private ApplicationContext applicationContext;

  @Inject
  public ItemListViewController(AppState appState, StageManager stageManager) {
    this.appState = appState;
    this.stageManager = stageManager;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    filteredItemList = new FilteredList<>(appState.getObservableItemList());
    SortedList<Item> itemSortedList = new SortedList<>(filteredItemList);
    itemSortedList.setComparator((o1, o2) -> {
      String text = searchTextViewController.getSearchTextField().getText();
      Collator coll = Collator.getInstance();
      coll.setStrength(Collator.PRIMARY);
      boolean o1StartsWithText = coll.compare(o1.getName().substring(0, text.length()), text) == 0;
      boolean o2StartsWithText = coll.compare(o2.getName().substring(0, text.length()), text) == 0;
      if ((o1StartsWithText && o2StartsWithText) || (!o1StartsWithText && !o2StartsWithText)) {
        return coll.compare(o1.getName(), o2.getName());
      }
      else if (o1StartsWithText) {
        return -1;
      }
      else {
        return 1;
      }
    });
    itemListView.setItems(itemSortedList);
    itemListView.setCellFactory(lv -> new ItemListCell());
    MultipleSelectionModel<Item> selectionModel = itemListView.getSelectionModel();
    selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
    selectionModel.selectFirst();
  }

  @FXML
  public void removeSelected() {
    appState.getObservableItemList().removeAll(itemListView.getSelectionModel().getSelectedItems());
  }

  @FXML
  public void onMouseEvent(MouseEvent mouseEvent) {
    if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
      launchSelectedInternal();
    }
    mouseEvent.consume();
  }

  @FXML
  public void onKeyPressedOnItemListView(KeyEvent keyEvent) {
    if (Keyboard.ONLY_TAB.match(keyEvent)) {
      keyEvent.consume();
      searchTextViewController.getSearchTextField().requestFocus();
      searchTextViewController.getSearchTextField().end();
    }
  }

  @FXML
  public void onKeyTypedOnItemListView(KeyEvent keyEvent) {
    String typedChar = keyEvent.getCharacter();
    if (Keyboard.ONLY_ENTER.getCharacter().equals(typedChar)) {
      launchSelectedInternal();
    }
    else if (Keyboard.ONLY_DELETE.getCharacter().equals(typedChar)) {
      removeSelected();
    }
    keyEvent.consume();
  }

  public void launchSelectedInternal() {
    for (Item item : itemListView.getSelectionModel().getSelectedItems()) {
      File file = new File(item.getAbsolutePath());
      try {
        if (Desktop.isDesktopSupported()) {
          Desktop.getDesktop().open(file);
        }
        else {
          new ProcessBuilder(item.getAbsolutePath()).start();
        }
      }
      catch (Throwable t) {
        logger.error("Failed to open the selected item with user's prefered app.", t);
      }
      stageManager.hideStage();
    }
  }

  public void addFiles(List<File> files) {
    List<Item> itemList = new ArrayList<>();
    for (File file : files) {
      File actualFile = resolveWindowsShortcut(file);
      itemList.add(new Item(getProductName(actualFile), actualFile.getAbsolutePath(), getFileIcon(actualFile)));
    }
    appState.getObservableItemList().addAll(itemList);
  }

  private File resolveWindowsShortcut(File file) {
    try {
      if (WindowsShortcut.isPotentialValidLink(file)) {
        String realFilename = new WindowsShortcut(file).getRealFilename();
        if (buggyShortcutResolutions.contains(realFilename)) { // buggy .lnk shortcut
          return file;
        }
        return new File(realFilename);
      }
      else {
        return file;
      }
    }
    catch (IOException | ParseException e) {
      e.printStackTrace();
      return file;
    }
  }

  private String getProductName(File file) {
    try {
      if (file.getAbsolutePath().startsWith(networkFilePrefix)) { // network files are too long to resolve
        return file.getName();
      }
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
      if (file.getAbsolutePath().startsWith(networkFilePrefix)) { // network files are too long to resolve
        return null;
      }
      java.awt.Image icon = ShellFolder.getShellFolder(file).getIcon(true); // true is 32x32, false if 16x16
      BufferedImage bufferedImage = new BufferedImage(icon.getWidth(null), icon.getHeight(null),
          BufferedImage.TYPE_INT_ARGB);
      Graphics2D bGr = bufferedImage.createGraphics();
      bGr.drawImage(icon, 0, 0, null);
      bGr.dispose();
      return SwingFXUtils.toFXImage(bufferedImage, null);
    }
    catch (Throwable t) {
      logger.info("Failed to retrieve icon for file: {}.", file.getAbsolutePath());
      return null;
    }
  }

  public void toggleDragOverFeedback() {
    opacity = opacity == 1 ? 0.5 : 1;
    itemListView.setOpacity(opacity);
  }

  public ListView<Item> getItemListView() {
    return itemListView;
  }

  public FilteredList<Item> getFilteredItemList() {
    return filteredItemList;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    // doing DI this way prevents Spring circular dependency exception
    searchTextViewController = applicationContext.getBean(SearchTextViewController.class);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}

package com.lwouis.falcon9.components.menu_bar;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.filechooser.FileSystemView;

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

import com.lwouis.falcon9.AppState;
import com.lwouis.falcon9.DiskPersistanceManager;
import com.lwouis.falcon9.components.item_list.ItemListController;
import com.lwouis.falcon9.models.Launchable;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import sun.awt.shell.ShellFolder;

public class MenuBarController {
  @FXML
  private MenuBar menuBar;

  private ItemListController itemListController;

  private FileSystemView fileSystemView = FileSystemView.getFileSystemView();

  public void setItemListController(ItemListController itemListController) {
    this.itemListController = itemListController;
  }

  @FXML
  private void chooseFile() {
    FileChooser fileChooser = new FileChooser();
    List<File> files = fileChooser.showOpenMultipleDialog(menuBar.getScene().getWindow());
    if (files == null) {
      return;
    }
    List<Launchable> launchableList = new ArrayList<>();
    for (File file : files) {
      launchableList.add(new Launchable(getProductName(file), file.getAbsolutePath(), getFileIcon(file)));
    }
    AppState.getLaunchableObservableList().addAll(launchableList);
  }

  private String getProductName(File file) {
    try {
      PE pe = PEParser.parse(file);
      ResourceDirectory rd = pe.getImageData().getResourceTable();
      ResourceEntry[] entries = ResourceHelper.findResources(rd, ResourceType.VERSION_INFO);
      if (entries.length == 0) {
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
      return file.getName();
    }
    catch (Throwable t) {
      t.printStackTrace();
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
      return SwingFXUtils.toFXImage(bufferedImage, null);
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  @FXML
  public void removeSelected() {
    itemListController.removeSelected();
  }

  @FXML
  public void fillWithDummy() {
    String pathToJsonFile = ClassLoader.getSystemResource("dummyItems.json").getPath();
    DiskPersistanceManager.loadFromDisk(new File(pathToJsonFile));
  }
}

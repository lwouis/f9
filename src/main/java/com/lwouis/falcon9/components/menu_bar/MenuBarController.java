package com.lwouis.falcon9.components.menu_bar;

import java.io.File;
import java.util.List;
import javax.swing.filechooser.FileSystemView;

import com.lwouis.falcon9.DiskPersistanceManager;
import com.lwouis.falcon9.components.item_list.ItemListController;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;

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
    itemListController.addFiles(files);
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

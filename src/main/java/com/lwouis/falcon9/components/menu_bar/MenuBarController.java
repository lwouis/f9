package com.lwouis.falcon9.components.menu_bar;

import java.io.File;
import java.util.List;

import com.lwouis.falcon9.AppState;
import com.lwouis.falcon9.DiskPersistanceManager;
import com.lwouis.falcon9.components.item_list.ItemListController;
import com.lwouis.falcon9.models.Launchable;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;

public class MenuBarController {
  @FXML
  private MenuBar menuBar;

  private ItemListController itemListController;

  public void setItemListController(ItemListController itemListController) {
    this.itemListController = itemListController;
  }

  @FXML
  private void chooseFile() {
    FileChooser fileChooser = new FileChooser();
    List<File> files = fileChooser.showOpenMultipleDialog(menuBar.getScene().getWindow());
    for (File file : files) {
      AppState.getItemList().add(new Launchable(file.getName(), file.getAbsolutePath()));
    }
  }

  @FXML
  public void removeSelected() {
    itemListController.removeSelected();
  }

  @FXML
  public void fillWithDummy() {
    String pathToJsonFile = ClassLoader.getSystemClassLoader().getResource("dummyItems.json").getPath();
    DiskPersistanceManager.loadFromDisk(new File(pathToJsonFile));
  }
}

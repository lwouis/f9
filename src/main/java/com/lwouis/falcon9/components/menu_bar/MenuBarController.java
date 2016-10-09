package com.lwouis.falcon9.components.menu_bar;

import java.io.File;

import com.lwouis.falcon9.components.item_list.ItemListController;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;

public class MenuBarController {
  @FXML
  private MenuBar menuBar;

  //@Inject
  private ItemListController itemListController;

  @FXML
  private void chooseFile() {
    FileChooser fileChooser = new FileChooser();
    File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
    ItemListController.addItem(file.getName());
  }

  @FXML
  public void removeSelected() {
    itemListController.removeSelected();
  }
}

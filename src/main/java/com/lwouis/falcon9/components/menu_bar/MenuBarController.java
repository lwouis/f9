package com.lwouis.falcon9.components.menu_bar;

import java.io.File;

import com.lwouis.falcon9.AppState;
import com.lwouis.falcon9.components.item_list.ItemListController;
import com.lwouis.falcon9.models.Launchable;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;

public class MenuBarController {
  @FXML
  private MenuBar menuBar;

  private ItemListController itemListController;

  @FXML
  private void chooseFile() {
    FileChooser fileChooser = new FileChooser();
    File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
    AppState.getItemList().add(new Launchable(file.getName(), file.getAbsolutePath()));
  }

  @FXML
  public void removeSelected() {
    itemListController.removeSelected();
  }

  public void setItemListController(ItemListController itemListController) {
    this.itemListController = itemListController;
  }
}

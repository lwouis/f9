package com.lwouis.falcon9.components.main_window;

import java.net.URL;
import java.util.ResourceBundle;

import com.lwouis.falcon9.components.item_list.ItemListController;
import com.lwouis.falcon9.components.menu_bar.MenuBarController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class MainWindowController implements Initializable {

  @FXML
  private ItemListController itemListController;

  @FXML
  private MenuBarController menuBarController;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    menuBarController.setItemListController(itemListController);
  }
}

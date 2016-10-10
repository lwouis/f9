package com.lwouis.falcon9.components.item_list;

import java.net.URL;
import java.util.ResourceBundle;

import com.lwouis.falcon9.AppState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

public class ItemListController implements Initializable {
  @FXML
  private ListView<String> itemList;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    itemList.setItems(AppState.getItemList());
  }

  public void removeSelected() {
    AppState.getItemList().removeAll(itemList.getSelectionModel().getSelectedItems());
  }
}

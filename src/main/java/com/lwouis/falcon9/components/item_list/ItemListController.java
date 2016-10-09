package com.lwouis.falcon9.components.item_list;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

public class ItemListController implements Initializable {
  @FXML
  private ListView<String> itemListView;

  private static ObservableList<String> itemList = FXCollections.observableArrayList();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    itemListView.setItems(itemList);
  }

  public static void addItem(String item) {
    itemList.add(item);
  }

  public void removeSelected() {
    itemList.removeAll(itemListView.getSelectionModel().getSelectedItems());
  }
}

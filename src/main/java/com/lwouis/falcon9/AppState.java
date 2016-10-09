package com.lwouis.falcon9;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AppState {
  private static ObservableList<String> itemList = FXCollections.observableArrayList();

  public static ObservableList<String> getItemList() {
    return itemList;
  }
}

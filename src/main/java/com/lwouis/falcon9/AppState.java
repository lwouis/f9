package com.lwouis.falcon9;

import com.lwouis.falcon9.models.Launchable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AppState {
  private static final ObservableList<Launchable> itemList = FXCollections.observableArrayList();

  public static ObservableList<Launchable> getItemList() {
    return itemList;
  }
}

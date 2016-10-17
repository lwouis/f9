package com.lwouis.falcon9;

import com.lwouis.falcon9.models.Launchable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class AppState {
  private static final ObservableList<Launchable> launchableObservableList = FXCollections.observableArrayList();

  private static FilteredList<Launchable> launchableFilteredList = new FilteredList<>(launchableObservableList);

  private static SortedList<Launchable> launchableSortedList = new SortedList<>(launchableFilteredList);

  public static ObservableList<Launchable> getLaunchableObservableList() {
    return launchableObservableList;
  }

  public static FilteredList<Launchable> getLaunchableFilteredList() {
    return launchableFilteredList;
  }

  public static SortedList<Launchable> getLaunchableSortedList() {
    return launchableSortedList;
  }
}

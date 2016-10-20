package com.lwouis.falcon9;

import javax.inject.Singleton;

import com.lwouis.falcon9.models.Launchable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

@Singleton
public class AppState {
  private static final ObservableList<Launchable> launchableObservableList = FXCollections.observableArrayList();

  private static final FilteredList<Launchable> launchableFilteredList = new FilteredList<>(launchableObservableList);

  private static final SortedList<Launchable> launchableSortedList = new SortedList<>(launchableFilteredList);

  public ObservableList<Launchable> getLaunchableObservableList() {
    return launchableObservableList;
  }

  public FilteredList<Launchable> getLaunchableFilteredList() {
    return launchableFilteredList;
  }

  public SortedList<Launchable> getLaunchableSortedList() {
    return launchableSortedList;
  }
}

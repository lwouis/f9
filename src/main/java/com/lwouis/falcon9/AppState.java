package com.lwouis.falcon9;

import javax.inject.Singleton;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.lwouis.falcon9.models.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

@Singleton
@Entity(name = "AppState")
public class AppState {

  @Id
  @GeneratedValue
  private Integer id;

  private static final ObservableList<Item> itemObservableList = FXCollections.observableArrayList();

  private static final FilteredList<Item> itemFilteredList = new FilteredList<>(itemObservableList);

  private static final SortedList<Item> itemSortedList = new SortedList<>(itemFilteredList);

  public ObservableList<Item> getItemObservableList() {
    return itemObservableList;
  }

  public FilteredList<Item> getItemFilteredList() {
    return itemFilteredList;
  }

  public SortedList<Item> getItemSortedList() {
    return itemSortedList;
  }
}

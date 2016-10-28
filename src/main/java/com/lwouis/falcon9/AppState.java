package com.lwouis.falcon9;

import javax.inject.Singleton;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.lwouis.falcon9.models.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Singleton
@Entity(name = "AppState")
public class AppState {

  @Id
  @GeneratedValue
  private Integer id;

  private static final ObservableList<Item> itemObservableList = FXCollections.observableArrayList();

  public ObservableList<Item> getItemObservableList() {
    return itemObservableList;
  }
}

package com.lwouis.f9.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.lwouis.f9.FontAwesomeManager;
import com.lwouis.f9.Keyboard;
import com.lwouis.f9.injection.InjectLogger;
import com.lwouis.f9.models.Item;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.input.KeyEvent;

@Component
public class SearchTextViewController implements Initializable, ApplicationContextAware, InitializingBean {

  @FXML
  private CustomTextField searchTextField;

  @InjectLogger
  private Logger logger;

  private final FontAwesomeManager fontAwesomeManager;

  private ItemListViewController itemListViewController;

  private ApplicationContext applicationContext;

  @Inject
  public SearchTextViewController(FontAwesomeManager fontAwesomeManager) {
    this.fontAwesomeManager = fontAwesomeManager;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    addSearchIcon();
    addListenerToFilterList();
  }

  private void addListenerToFilterList() {
    searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      String searchText = searchTextField.getText();
      FilteredList<Item> filteredItemList = itemListViewController.getFilteredItemList();
      if (searchText == null || searchText.length() == 0) {
        filteredItemList.setPredicate(s -> true);
      }
      else {
        String searchTextTrimmed = searchText.trim(); // ignore extra spaces on the sides
        filteredItemList.setPredicate(s -> StringUtils.containsIgnoreCase(s.nameProperty().get(), searchTextTrimmed));
      }
      itemListViewController.getListView().getSelectionModel().selectFirst();
    });
  }

  private void addSearchIcon() {
    Glyph searchIcon = fontAwesomeManager.getGlyph(FontAwesome.Glyph.SEARCH);
    searchIcon.setTranslateX(35);
    searchIcon.setTranslateY(-1);
    searchTextField.setLeft(searchIcon);
  }

  @FXML
  public void onKeyPressed(KeyEvent keyEvent) {
    if (Keyboard.ONLY_ENTER.match(keyEvent)) {
      keyEvent.consume();
      launchSelectedItem(keyEvent);
    }
    else if (Keyboard.ONLY_UP.match(keyEvent) || Keyboard.ONLY_DOWN.match(keyEvent)) {
      keyEvent.consume();
      selectAdjacentItem(keyEvent);
    }
  }

  private void selectAdjacentItem(KeyEvent keyEvent) {
    ListView<Item> itemListView = itemListViewController.getListView();
    MultipleSelectionModel<Item> selectionModel = itemListView.getSelectionModel();
    int itemListSize = itemListView.getItems().size();
    if (Keyboard.ONLY_UP.match(keyEvent)) {
      selectPreviousItem(selectionModel, itemListSize);
    }
    else if (Keyboard.ONLY_DOWN.match(keyEvent)) {
      selectNextItem(selectionModel, itemListSize);
    }
  }

  private void selectNextItem(MultipleSelectionModel<Item> selectionModel, int itemListSize) {
    int newIndex = selectionModel.getSelectedIndex() + 1;
    if (newIndex > itemListSize - 1) {
      newIndex = 0;
    }
    selectionModel.clearAndSelect(newIndex);
  }

  private void selectPreviousItem(MultipleSelectionModel<Item> selectionModel, int itemListSize) {
    int newIndex = selectionModel.getSelectedIndex() - 1;
    if (newIndex < 0) {
      newIndex = itemListSize - 1;
    }
    selectionModel.clearAndSelect(newIndex);
  }

  private void launchSelectedItem(KeyEvent keyEvent) {
    itemListViewController.launchSelected();
  }

  public CustomTextField getSearchTextField() {
    return searchTextField;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    // doing DI this way prevents Spring circular dependency exception
    itemListViewController = applicationContext.getBean(ItemListViewController.class);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}

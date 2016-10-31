package com.lwouis.falcon9.controllers;

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

import com.lwouis.falcon9.FontAwesomeManager;
import com.lwouis.falcon9.Keyboard;
import com.lwouis.falcon9.injection.InjectLogger;
import com.lwouis.falcon9.models.Item;
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
    Glyph searchIcon = fontAwesomeManager.getGlyph(FontAwesome.Glyph.SEARCH);
    searchIcon.setTranslateX(35);
    searchIcon.setTranslateY(-1);
    searchTextField.setLeft(searchIcon);
    searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      String searchText = searchTextField.getText();
      MultipleSelectionModel<Item> selectionModel = itemListViewController.getItemListView().getSelectionModel();
      FilteredList<Item> filteredItemList = itemListViewController.getFilteredItemList();
      if (searchText == null || searchText.length() == 0) {
        filteredItemList.setPredicate(s -> true);
      }
      else {
        String searchTextTrimmed = searchText.trim(); // ignore extra spaces on the sides
        filteredItemList.setPredicate(s -> StringUtils.containsIgnoreCase(s.getName(), searchTextTrimmed));
      }
      selectionModel.selectFirst();
    });
  }

  @FXML
  public void onKeyPressed(KeyEvent keyEvent) {
    if (Keyboard.ONLY_ENTER.match(keyEvent)) {
      itemListViewController.launchSelected();
      keyEvent.consume();
    }
    else if (Keyboard.ONLY_UP.match(keyEvent) || Keyboard.ONLY_DOWN.match(keyEvent)) {
      ListView<Item> itemListView = itemListViewController.getItemListView();
      MultipleSelectionModel<Item> selectionModel = itemListView.getSelectionModel();
      int itemListSize = itemListView.getItems().size();
      if (Keyboard.ONLY_UP.match(keyEvent)) {
        int newIndex = selectionModel.getSelectedIndex() - 1;
        if (newIndex < 0) {
          newIndex = itemListSize - 1;
        }
        selectionModel.clearAndSelect(newIndex);
      }
      else if (Keyboard.ONLY_DOWN.match(keyEvent)) {
        int newIndex = selectionModel.getSelectedIndex() + 1;
        if (newIndex > itemListSize - 1) {
          newIndex = 0;
        }
        selectionModel.clearAndSelect(newIndex);
      }
      keyEvent.consume();
    }
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

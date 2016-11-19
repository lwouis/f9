package com.lwouis.f9.controllers;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.text.Collator;
import java.util.List;
import java.util.ResourceBundle;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.jfoenix.controls.JFXListView;
import com.lwouis.f9.Keyboard;
import com.lwouis.f9.PersistenceManager;
import com.lwouis.f9.StageManager;
import com.lwouis.f9.WindowsFileAnalyzer;
import com.lwouis.f9.injection.InjectLogger;
import com.lwouis.f9.models.Item;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

@Component
public class ItemListViewController implements Initializable, ApplicationContextAware, InitializingBean {

  private ObservableList<Item> observableItemList;

  private FilteredList<Item> filteredItemList;

  @FXML
  private JFXListView<Item> listView;

  private SearchTextFieldController searchTextFieldController;

  private final PersistenceManager persistenceManager;

  private final WindowsFileAnalyzer windowsFileAnalyzer;

  private final StageManager stageManager;

  @InjectLogger
  private Logger logger;

  private ApplicationContext applicationContext;

  private final PopOverController popOverController;

  @Inject
  public ItemListViewController(PersistenceManager persistenceManager, StageManager stageManager, WindowsFileAnalyzer windowsFileAnalyzer,
      PopOverController popOverController) {
    this.persistenceManager = persistenceManager;
    this.stageManager = stageManager;
    this.windowsFileAnalyzer = windowsFileAnalyzer;
    this.popOverController = popOverController;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    VBox.setVgrow(listView, Priority.ALWAYS);
    listView.setItems(loadItemListThenFilterAndSort());
    setListViewCellFactory();
    MultipleSelectionModel<Item> selectionModel = listView.getSelectionModel();
    selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
    selectionModel.selectFirst();
  }

  private SortedList<Item> loadItemListThenFilterAndSort() {
    observableItemList = FXCollections.observableList(persistenceManager.loadListFromDiskOrCreateOne());
    filteredItemList = new FilteredList<>(observableItemList);
    SortedList<Item> itemSortedList = new SortedList<>(filteredItemList);
    setSortedListComparator(itemSortedList);
    return itemSortedList;
  }

  private void setListViewCellFactory() {
    listView.setCellFactory(lv -> {
      StringProperty textToHighlight = searchTextFieldController.getSearchTextField().textProperty();
      return new ItemListCellController(this, textToHighlight);
    });
  }

  private void setSortedListComparator(SortedList<Item> itemSortedList) {
    String text = searchTextFieldController.getSearchTextField().getText();
    itemSortedList.setComparator((o1, o2) -> {
      Collator coll = Collator.getInstance();
      coll.setStrength(Collator.PRIMARY);
      boolean o1StartsWithText = coll.compare(o1.nameProperty().get().substring(0, text.length()), text) == 0;
      boolean o2StartsWithText = coll.compare(o2.nameProperty().get().substring(0, text.length()), text) == 0;
      if ((o1StartsWithText && o2StartsWithText) || (!o1StartsWithText && !o2StartsWithText)) {
        return coll.compare(o1.nameProperty().get(), o2.nameProperty().get());
      }
      else if (o1StartsWithText) {
        return -1;
      }
      else {
        return 1;
      }
    });
  }

  public void removeSelected() {
    ObservableList<Item> selectedItems = listView.getSelectionModel().getSelectedItems();
    persistenceManager.removeItems(selectedItems);
    observableItemList.removeAll(selectedItems);
  }

  @FXML
  public void onKeyPressed(KeyEvent keyEvent) {
    if (Keyboard.ONLY_TAB.match(keyEvent)) {
      keyEvent.consume();
      searchTextFieldController.getSearchTextField().requestFocus();
      searchTextFieldController.getSearchTextField().end();
    }
  }

  @FXML
  public void onKeyTyped(KeyEvent keyEvent) {
    String typedChar = keyEvent.getCharacter();
    if (Keyboard.ONLY_ENTER.getCharacter().equals(typedChar)) {
      keyEvent.consume();
      launchSelected();
    }
    else if (Keyboard.ONLY_DELETE.getCharacter().equals(typedChar)) {
      keyEvent.consume();
      removeSelected();
    }
  }

  public void launchSelected() {
    for (Item item : listView.getSelectionModel().getSelectedItems()) {
      File file = new File(item.pathProperty().get());
      try {
        String args = item.argumentsProperty().get();
        if (Desktop.isDesktopSupported() && args.isEmpty()) {
          Desktop.getDesktop().open(file);
        }
        else {
          new ProcessBuilder(item.pathProperty().get(), args).start();
        }
      }
      catch (Throwable t) {
        logger.error("Failed to open the selected item with user's prefered app.", t);
      }
      stageManager.hideStage();
    }
  }

  public void addFiles(List<File> files) {
    windowsFileAnalyzer.itemsFromBackgroundThreadInspections(files, observableItemList);
  }

  public JFXListView<Item> getListView() {
    return listView;
  }

  public FilteredList<Item> getFilteredItemList() {
    return filteredItemList;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    // doing DI this way prevents Spring circular dependency exception
    searchTextFieldController = applicationContext.getBean(SearchTextFieldController.class);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  public void showPopOver(ItemListCellController itemListCellController, Item item) {
    popOverController.show(itemListCellController, item);
  }

  public void hidePopOver() {
    popOverController.hide();
  }
}

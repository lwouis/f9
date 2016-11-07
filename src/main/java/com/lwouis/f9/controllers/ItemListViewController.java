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

import com.lwouis.f9.AppState;
import com.lwouis.f9.Keyboard;
import com.lwouis.f9.StageManager;
import com.lwouis.f9.WindowsFileAnalyzer;
import com.lwouis.f9.injection.InjectLogger;
import com.lwouis.f9.models.Item;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

@Component
public class ItemListViewController implements Initializable, ApplicationContextAware, InitializingBean {

  private FilteredList<Item> filteredItemList;

  @FXML
  private ListView<Item> listView;

  private SearchTextViewController searchTextViewController;

  private final AppState appState;

  private final WindowsFileAnalyzer windowsFileAnalyzer;

  private final StageManager stageManager;

  @InjectLogger
  private Logger logger;

  private ApplicationContext applicationContext;

  @Inject
  public ItemListViewController(AppState appState, StageManager stageManager, WindowsFileAnalyzer windowsFileAnalyzer) {
    this.appState = appState;
    this.stageManager = stageManager;
    this.windowsFileAnalyzer = windowsFileAnalyzer;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    VBox.setVgrow(listView, Priority.ALWAYS);
    filteredItemList = new FilteredList<>(appState.getObservableItemList());
    SortedList<Item> itemSortedList = new SortedList<>(filteredItemList);
    String text = searchTextViewController.getSearchTextField().getText();
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
    listView.setItems(itemSortedList);
    listView.setCellFactory(lv -> new ItemListCellController(searchTextViewController.getSearchTextField().textProperty()));
    MultipleSelectionModel<Item> selectionModel = listView.getSelectionModel();
    selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
    selectionModel.selectFirst();
  }

  public void removeSelected() {
    ObservableList<Item> selectedItems = listView.getSelectionModel().getSelectedItems();
    appState.removeItems(selectedItems);
    appState.getObservableItemList().removeAll(selectedItems);
  }

  @FXML
  public void onMouseEvent(MouseEvent mouseEvent) {
    if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
      launchSelected();
    }
    mouseEvent.consume();
  }

  @FXML
  public void onKeyPressed(KeyEvent keyEvent) {
    if (Keyboard.ONLY_TAB.match(keyEvent)) {
      keyEvent.consume();
      searchTextViewController.getSearchTextField().requestFocus();
      searchTextViewController.getSearchTextField().end();
    }
  }

  @FXML
  public void onKeyTyped(KeyEvent keyEvent) {
    String typedChar = keyEvent.getCharacter();
    if (Keyboard.ONLY_ENTER.getCharacter().equals(typedChar)) {
      launchSelected();
    }
    else if (Keyboard.ONLY_DELETE.getCharacter().equals(typedChar)) {
      removeSelected();
    }
    keyEvent.consume();
  }

  public void launchSelected() {
    for (Item item : listView.getSelectionModel().getSelectedItems()) {
      File file = new File(item.pathProperty().get());
      try {
        if (Desktop.isDesktopSupported()) {
          Desktop.getDesktop().open(file);
        }
        else {
          new ProcessBuilder(item.pathProperty().get()).start();
        }
      }
      catch (Throwable t) {
        logger.error("Failed to open the selected item with user's prefered app.", t);
      }
      stageManager.hideStage();
    }
  }

  public void addFiles(List<File> files) {
    windowsFileAnalyzer.itemsFromBackgroundThreadInspections(files);
  }

  public ListView<Item> getListView() {
    return listView;
  }

  public FilteredList<Item> getFilteredItemList() {
    return filteredItemList;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    // doing DI this way prevents Spring circular dependency exception
    searchTextViewController = applicationContext.getBean(SearchTextViewController.class);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}

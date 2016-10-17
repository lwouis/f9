package com.lwouis.falcon9.components.item_list;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.Collator;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import com.lwouis.falcon9.AppState;
import com.lwouis.falcon9.components.launchable_cell.LaunchableCell;
import com.lwouis.falcon9.models.Launchable;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class ItemListController implements Initializable {
  @FXML
  public Label launchableLabel;

  @FXML
  private ListView<Launchable> launchableListView;

  @FXML
  private TextField filterTextField;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeFilterTextField();
    initializeLaunchableListView();
  }

  private void initializeFilterTextField() {
    filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      String filterText = filterTextField.getText();
      FilteredList<Launchable> launchableFilteredList = AppState.getLaunchableFilteredList();
      if (filterText == null || filterText.length() == 0) {
        launchableFilteredList.setPredicate(s -> true);
      }
      else {
        String filterTextTrimmed = filterText.trim(); // ignore extra spaces on the sides
        launchableFilteredList.setPredicate(s -> StringUtils.containsIgnoreCase(s.getName(), filterTextTrimmed));
      }
    });
  }

  private void initializeLaunchableListView() {
    AppState.getLaunchableSortedList().setComparator((o1, o2) -> {
      Collator coll = Collator.getInstance();
      coll.setStrength(Collator.PRIMARY);
      return coll.compare(o1.getName(), o2.getName());
    });
    launchableListView.setItems(AppState.getLaunchableSortedList());
    launchableListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    launchableListView.setCellFactory(lv -> new LaunchableCell());
  }

  @FXML
  public void removeSelected() {
    AppState.getLaunchableObservableList().removeAll(launchableListView.getSelectionModel().getSelectedItems());
  }

  @FXML
  public void onMouseEvent(MouseEvent mouseEvent) throws IOException {
    if (mouseEvent.getClickCount() == 2) {
      launchSelectedInternal();
    }
  }

  @FXML
  public void onKeyEvent(KeyEvent keyEvent) throws IOException {
    if (keyEvent.getCharacter().equals("\r")) {
      launchSelectedInternal();
    }
  }

  private void launchSelectedInternal() {
    for (Launchable launchable : launchableListView.getSelectionModel().getSelectedItems()) {
      File file = new File(launchable.getAbsolutePath());
      try {
        if (Desktop.isDesktopSupported()) {
          Desktop.getDesktop().open(file);
        }
        else {
          new ProcessBuilder(launchable.getAbsolutePath()).start();
        }
      }
      catch (IOException e) {
        e.printStackTrace();
        // TODO: show the user that the file doesn't exist
      }
    }
  }
}
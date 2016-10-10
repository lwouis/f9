package com.lwouis.falcon9.components.item_list;

import java.net.URL;
import java.util.ResourceBundle;

import com.lwouis.falcon9.AppState;
import com.lwouis.falcon9.models.Launchable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.util.Callback;

public class ItemListController implements Initializable {
  @FXML
  private ListView<Launchable> itemList;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    itemList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    itemList.setItems(AppState.getItemList());
    itemList.setCellFactory(new Callback<ListView<Launchable>, ListCell<Launchable>>() {
      @Override
      public ListCell<Launchable> call(ListView<Launchable> p) {
        return new ListCell<Launchable>() {
          @Override
          protected void updateItem(Launchable launchable, boolean bln) {
            super.updateItem(launchable, bln);
            if (launchable != null) {
              setText(launchable.getName() + " (" + launchable.getAbsolutePath() + ")");
            }
            else {
              setText(null);
              setGraphic(null);
            }
          }

        };
      }
    });
  }

  public void removeSelected() {
    AppState.getItemList().removeAll(itemList.getSelectionModel().getSelectedItems());
  }
}

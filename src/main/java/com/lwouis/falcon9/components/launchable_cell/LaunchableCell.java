package com.lwouis.falcon9.components.launchable_cell;

import java.io.IOException;

import com.lwouis.falcon9.models.Launchable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

public class LaunchableCell extends ListCell<Launchable> {

  @FXML
  public Label launchableLabel;

  @Override
  protected void updateItem(Launchable launchable, boolean empty) {
    super.updateItem(launchable, empty);
    if (empty || launchable == null) {
      setText(null);
      setGraphic(null);
      return;
    }
    try {
      FXMLLoader loader = new FXMLLoader(
              ClassLoader.getSystemResource("com/lwouis/falcon9/components/launchable_cell/launchableCell.fxml"));
      loader.setController(this);
      Parent parent = loader.load();
      launchableLabel.setText(launchable.getName());
      setText(null);
      setGraphic(parent);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
}

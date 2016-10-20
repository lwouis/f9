package com.lwouis.falcon9.controllers;

import java.io.IOException;

import com.lwouis.falcon9.models.Launchable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

public class LaunchableCell extends ListCell<Launchable> {

  @FXML
  private Label launchableNameLabel;

  @FXML
  private Label launchablePathLabel;

  @FXML
  private ImageView launchableImageView;

  private Node content;

  public LaunchableCell() {
    super();
    try {
      FXMLLoader loader = new FXMLLoader(
              ClassLoader.getSystemResource("fxml/launchableCell.fxml"));
      loader.setController(this);
      content = loader.load();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void updateItem(Launchable launchable, boolean empty) {
    super.updateItem(launchable, empty);
    if (empty || launchable == null) {
      setGraphic(null);
      return;
    }
    launchableImageView.setImage(launchable.getImage());
    launchableNameLabel.setText(launchable.getName());
    launchablePathLabel.setText(launchable.getAbsolutePath());
    setGraphic(content);
  }
}

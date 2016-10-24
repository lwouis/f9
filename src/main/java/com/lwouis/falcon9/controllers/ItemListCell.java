package com.lwouis.falcon9.controllers;

import java.io.IOException;

import com.lwouis.falcon9.models.Item;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

public class ItemListCell extends ListCell<Item> {

  @FXML
  private Label itemNameLabel;

  @FXML
  private Label itemPathLabel;

  @FXML
  private ImageView itemImageView;

  private Node content;

  public ItemListCell() {
    super();
    try {
      FXMLLoader loader = new FXMLLoader(
              ClassLoader.getSystemResource("fxml/ItemListCell.fxml"));
      loader.setController(this);
      content = loader.load();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void updateItem(Item item, boolean empty) {
    super.updateItem(item, empty);
    if (empty || item == null) {
      setGraphic(null);
      return;
    }
    itemImageView.setImage(item.getImage());
    itemNameLabel.setText(item.getName());
    itemPathLabel.setText(item.getAbsolutePath());
    setGraphic(content);
  }
}

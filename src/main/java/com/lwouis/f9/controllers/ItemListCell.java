package com.lwouis.f9.controllers;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.lwouis.f9.models.Item;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

@Component
public class ItemListCell extends ListCell<Item> {

  @FXML
  private Label nameLabel;

  @FXML
  private Label pathLabel;

  @FXML
  private ImageView imageView;

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
    imageView.setImage(item.getImage());
    nameLabel.setText(item.getName());
    pathLabel.setText(item.getAbsolutePath());
    setGraphic(content);
  }
}

package com.lwouis.f9.controllers;

import java.io.IOException;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.lwouis.f9.injection.InjectLogger;
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

  @InjectLogger
  private Logger logger;

  public ItemListCell() {
    super();
    String fxmlFile = "fxml/ItemListCell.fxml";
    try {
      FXMLLoader loader = new FXMLLoader(
              ClassLoader.getSystemResource(fxmlFile));
      loader.setController(this);
      content = loader.load();
    }
    catch (IOException e) {
      logger.error("Failed to load " + fxmlFile, e);
    }
  }

  @Override
  protected void updateItem(Item item, boolean empty) {
    super.updateItem(item, empty);
    if (empty || item == null) {
      setGraphic(null);
      return;
    }
    imageView.imageProperty().bind(item.iconProperty());
    nameLabel.textProperty().bind(item.nameProperty());
    pathLabel.textProperty().bind(item.absolutePathProperty());
    setGraphic(content);
  }
}

package com.lwouis.f9.controllers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXListCell;
import com.lwouis.f9.models.Item;
import com.lwouis.f9.nodes.SearchableTextFlow;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.text.TextFlow;

public class ItemListCellController extends JFXListCell<Item> {

  private static final Logger logger = LoggerFactory.getLogger(ItemListCellController.class);

  private final ItemListViewController itemListViewController;

  @FXML
  private TextFlow highlightedName;

  @FXML
  private Label pathLabel;

  @FXML
  private ImageView imageView;

  @FXML
  private SearchableTextFlow searchableTextFlow;

  private Node content;

  public ItemListCellController(ItemListViewController itemListViewController, StringProperty textToHighlight) {
    super();
    this.itemListViewController = itemListViewController;
    String fxmlFile = "fxml/ItemListCell.fxml";
    try {
      FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(fxmlFile));
      loader.setController(this);
      content = loader.load();
    }
    catch (IOException e) {
      logger.error("Failed to load " + fxmlFile, e);
    }
    searchableTextFlow.textToHighlightProperty().bind(textToHighlight);
    handleMouseEvents();
  }

  @Override
  public void updateItem(Item item, boolean empty) {
    super.updateItem(item, empty);
    searchableTextFlow.textProperty().unbind();
    pathLabel.textProperty().unbind();
    imageView.imageProperty().unbind();
    if (empty || item == null) {
      setGraphic(null);
      return;
    }
    searchableTextFlow.textProperty().bind(item.nameProperty());
    pathLabel.textProperty().bind(Bindings.concat(item.pathProperty(), " ", item.argumentsProperty()));
    imageView.imageProperty().bind(item.iconProperty());
    setGraphic(content);
  }

  private void handleMouseEvents() {
    setOnMouseClicked(mouseEvent -> {
      mouseEvent.consume();
      if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
        itemListViewController.hidePopOver();
        if (mouseEvent.getClickCount() == 2) {
          itemListViewController.launchSelected();
        }
      }
      else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
        itemListViewController.showPopOver(this, getItem());
      }
    });
  }
}

package com.lwouis.f9.nodes;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lwouis.f9.models.Item;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextFlow;

public class ItemListCell extends ListCell<Item> {

  private static final Logger logger = LoggerFactory.getLogger(ItemListCell.class);

  @FXML
  private TextFlow highlightedName;

  @FXML
  private Label pathLabel;

  @FXML
  private ImageView imageView;

  @FXML
  private SearchableTextFlow searchableTextFlow;

  private Node content;

  public ItemListCell(StringProperty textToHighlight) {
    super();
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
  }

  @Override
  protected void updateItem(Item item, boolean empty) {
    super.updateItem(item, empty);
    if (empty || item == null) {
      setGraphic(null);
      searchableTextFlow.textProperty().unbind();
      return;
    }
    searchableTextFlow.textProperty().bind(item.nameProperty());
    pathLabel.textProperty().bind(item.absolutePathProperty());
    imageView.imageProperty().bind(item.iconProperty());
    setGraphic(content);
  }
}

package com.lwouis.f9.controllers;

import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lwouis.f9.models.Item;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public class PopOverController {

  private static final Logger logger = LoggerFactory.getLogger(PopOverController.class);

  @FXML
  private TextField nameTextField;

  @FXML
  private TextField pathTextField;

  @FXML
  private TextField argumentsTextField;

  private PopOver popOver;

  private Item item;

  public PopOverController() {
    String fxmlFile = "fxml/ItemListPopOver.fxml";
    try {
      FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(fxmlFile));
      loader.setController(this);
      popOver = new PopOver(loader.load());
      popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
      popOver.setDetachable(false);
    }
    catch (Throwable t) {
      logger.error("Failed contruct PopOverController.", t);
    }
  }

  public void show(Node owner, Item item) {
    if (this.item != null && this.item.equals(item)) {
      return;
    }
    this.item = item;
    bindPopOverControllerToItem();
    popOver.show(owner);
  }

  private void bindPopOverControllerToItem() {
    nameTextField.textProperty().unbind();
    pathTextField.textProperty().unbind();
    argumentsTextField.textProperty().unbind();
    nameTextField.textProperty().bind(item.nameProperty());
    pathTextField.textProperty().bind(item.pathProperty());
    argumentsTextField.textProperty().bind(item.argumentsProperty());
  }
}

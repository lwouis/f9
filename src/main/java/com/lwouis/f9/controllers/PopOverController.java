package com.lwouis.f9.controllers;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.inject.Inject;

import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jfoenix.controls.JFXTextField;
import com.lwouis.f9.AppState;
import com.lwouis.f9.models.Item;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

@Component
public class PopOverController {

  private static final Logger logger = LoggerFactory.getLogger(PopOverController.class);

  @FXML
  private JFXTextField nameTextField;

  @FXML
  private JFXTextField pathTextField;

  @FXML
  private JFXTextField argumentsTextField;

  private PopOver popOver;

  private Item item;

  private final AppState appState;

  @Inject
  public PopOverController(AppState appState) {
    String fxmlFile = "fxml/ItemListPopOver.fxml";
    try {
      FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(fxmlFile));
      loader.setResources(ResourceBundle.getBundle("i18n.strings", Locale.getDefault()));
      loader.setController(this);
      popOver = new PopOver(loader.load());
      popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
      popOver.setDetachable(false);
      //popOver.setAutoHide(false);
    }
    catch (Throwable t) {
      logger.error("Failed contruct PopOverController.", t);
    }
    this.appState = appState;
  }

  public void show(Node owner, Item item) {
    if (this.item != null && this.item.equals(item) && popOver.isShowing()) {
      return;
    }
    this.item = item;
    bindPopOverControllerToItem();
    popOver.show(owner);
  }

  public void hide() {
    if (popOver.isShowing()) {
      popOver.hide();
      appState.persist();
    }
  }

  private void bindPopOverControllerToItem() {
    item.nameProperty().unbind();
    item.pathProperty().unbind();
    item.argumentsProperty().unbind();
    nameTextField.textProperty().set(item.nameProperty().get());
    pathTextField.textProperty().set(item.pathProperty().get());
    argumentsTextField.textProperty().set(item.argumentsProperty().get());
    item.nameProperty().bind(nameTextField.textProperty());
    item.pathProperty().bind(pathTextField.textProperty());
    item.argumentsProperty().bind(argumentsTextField.textProperty());
  }
}

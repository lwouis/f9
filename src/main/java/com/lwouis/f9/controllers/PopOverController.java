package com.lwouis.f9.controllers;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.inject.Inject;

import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jfoenix.controls.JFXTextField;
import com.lwouis.f9.PersistenceManager;
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

  @Inject
  public PopOverController(PersistenceManager persistenceManager) {
    String fxmlFile = "fxml/ItemListPopOver.fxml";
    try {
      FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(fxmlFile));
      loader.setResources(ResourceBundle.getBundle("i18n.strings", Locale.getDefault()));
      loader.setController(this);
      popOver = new PopOver(loader.load());
      popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
      popOver.setDetachable(false);
      popOver.setConsumeAutoHidingEvents(true);
      bindPersistOnHide(persistenceManager);
    }
    catch (Throwable t) {
      logger.error("Failed contruct PopOverController.", t);
    }
  }

  private void bindPersistOnHide(PersistenceManager persistenceManager) {
    popOver.showingProperty().addListener((obs, old, val) -> {
      if (!val) {
        persistenceManager.persist();
      }
    });
  }

  public void show(Node owner, Item item) {
    if (this.item != null && this.item.equals(item) && popOver.isShowing()) {
      return;
    }
    unbindPreviousItem();
    this.item = item;
    bindNewItem();
    popOver.show(owner);
  }

  private void unbindPreviousItem() {
    if (item != null) {
      item.nameProperty().unbind();
      item.pathProperty().unbind();
      item.argumentsProperty().unbind();
    }
  }

  public void hide() {
    if (popOver.isShowing()) {
      popOver.hide();
    }
  }

  private void bindNewItem() {
    nameTextField.textProperty().set(item.nameProperty().get());
    pathTextField.textProperty().set(item.pathProperty().get());
    argumentsTextField.textProperty().set(item.argumentsProperty().get());
    item.nameProperty().bind(nameTextField.textProperty());
    item.pathProperty().bind(pathTextField.textProperty());
    item.argumentsProperty().bind(argumentsTextField.textProperty());
  }
}

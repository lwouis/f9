package com.lwouis.falcon9.controllers;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

@Component
public class MainWindowController {
  @FXML
  private ItemListController itemListController;

  @FXML
  public void onDragDropped(DragEvent dragEvent) {
    Dragboard db = dragEvent.getDragboard();
    boolean success = false;
    if (db.hasFiles()) {
      success = true;
      itemListController.addFiles(db.getFiles());
    }
    dragEvent.setDropCompleted(success);
    dragEvent.consume();
  }

  @FXML
  public void onDragExited(DragEvent dragEvent) {
    itemListController.toggleDragOverFeedback();
    dragEvent.consume();
  }

  @FXML
  public void onDragEntered(DragEvent dragEvent) {
    itemListController.toggleDragOverFeedback();
    dragEvent.consume();
  }

  @FXML
  public void onDragOver(DragEvent dragEvent) {
    dragEvent.acceptTransferModes(TransferMode.ANY);
    dragEvent.consume();
  }
}

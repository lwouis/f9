package com.lwouis.f9.controllers;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

@Component
public class MainWindowController {
  @FXML
  private ItemListViewController itemListViewController;

  @FXML
  public void onDragDropped(DragEvent dragEvent) {
    Dragboard db = dragEvent.getDragboard();
    boolean success = false;
    if (db.hasFiles()) {
      success = true;
      itemListViewController.addFiles(db.getFiles());
    }
    dragEvent.setDropCompleted(success);
    dragEvent.consume();
  }

  @FXML
  public void onDragExited(DragEvent dragEvent) {
    itemListViewController.setDragOverFeedback(false);
    dragEvent.consume();
  }

  @FXML
  public void onDragEntered(DragEvent dragEvent) {
    itemListViewController.setDragOverFeedback(true);
    dragEvent.consume();
  }

  @FXML
  public void onDragOver(DragEvent dragEvent) {
    dragEvent.acceptTransferModes(TransferMode.ANY);
    dragEvent.consume();
  }
}

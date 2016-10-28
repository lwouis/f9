package com.lwouis.falcon9.controllers;

import java.io.File;
import java.util.List;
import javax.inject.Inject;

import com.lwouis.falcon9.DiskPersistenceManager;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;

public class MenuBarController {
  @FXML
  private MenuBar menuBar;

  private final ItemListController itemListController;

  @Inject
  public MenuBarController(ItemListController itemListController, DiskPersistenceManager diskPersistenceManager) {
    this.itemListController = itemListController;
  }

  @FXML
  private void chooseFile() {
    FileChooser fileChooser = new FileChooser();
    List<File> files = fileChooser.showOpenMultipleDialog(menuBar.getScene().getWindow());
    if (files == null) {
      return;
    }
    itemListController.addFiles(files);
  }

  @FXML
  public void removeSelected() {
    itemListController.removeSelected();
  }
}

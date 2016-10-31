package com.lwouis.falcon9.controllers;

import java.io.File;
import java.util.List;
import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.lwouis.falcon9.AppState;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;

@Component
public class MenuBarController {
  @FXML
  private MenuBar menuBar;

  private final ItemListViewController itemListViewController;

  @Inject
  public MenuBarController(ItemListViewController itemListViewController, AppState appState) {
    this.itemListViewController = itemListViewController;
  }

  @FXML
  private void chooseFile() {
    FileChooser fileChooser = new FileChooser();
    List<File> files = fileChooser.showOpenMultipleDialog(menuBar.getScene().getWindow());
    if (files == null) {
      return;
    }
    itemListViewController.addFiles(files);
  }

  @FXML
  public void removeSelected() {
    itemListViewController.removeSelected();
  }
}

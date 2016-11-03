package com.lwouis.f9.controllers;

import java.io.File;
import java.util.List;
import javax.inject.Inject;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;

@Component
public class MenuBarController {
  @FXML
  private MenuBar menuBar;

  @FXML
  private Menu menu1;

  private final ItemListViewController itemListViewController;

  @Inject
  public MenuBarController(ItemListViewController itemListViewController) {
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

  public Menu getMenu1() {
    return menu1;
  }
}

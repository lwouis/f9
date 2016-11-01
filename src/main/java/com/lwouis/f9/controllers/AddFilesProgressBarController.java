package com.lwouis.f9.controllers;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.lwouis.f9.AppState;
import com.lwouis.f9.WindowsFileAnalyzer;
import com.lwouis.f9.models.Item;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

@Component
public class AddFilesProgressBarController implements Initializable, ApplicationContextAware, InitializingBean {
  private final WindowsFileAnalyzer windowsFileAnalyzer;

  private final AppState appState;

  private MenuBarController menuBarController;

  @FXML
  private VBox vBox;

  @FXML
  private Label label;

  @FXML
  private ProgressBar progressBar;

  private SearchTextViewController searchTextViewController;

  private ApplicationContext applicationContext;

  private String message;

  public AddFilesProgressBarController(WindowsFileAnalyzer windowsFileAnalyzer, AppState appState) {
    this.windowsFileAnalyzer = windowsFileAnalyzer;
    this.appState = appState;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    message = resources.getString("addFilesProgressBar.message");
    setLoadingUiMode(false);
  }

  public void addFiles(List<File> files) {
    //setLoadingUiMode(true);
    Task task = new Task() {
      @Override
      protected Void call() throws Exception {
        setLoadingUiMode(true);
        final int filesSize = files.size();
        for (int i = 0; i < filesSize; i++) {
          if (isCancelled()) {
            break;
          }
          updateTitle(String.format(message, i, filesSize));
          updateProgress(i, filesSize);
          File file = files.get(i);
          File actualFile = windowsFileAnalyzer.resolveWindowsShortcut(file);
          String name = windowsFileAnalyzer.getProductName(actualFile);
          String absolutePath = actualFile.getAbsolutePath();
          Image icon = windowsFileAnalyzer.getFileIcon(actualFile);
          Platform.runLater(() -> {
            appState.getObservableItemList().add(new Item(name, absolutePath, icon));
          });
        }
        setLoadingUiMode(false);
        return null;
      }
    };
    label.textProperty().bind(task.titleProperty());
    progressBar.progressProperty().bind(task.progressProperty());
    Thread thread = new Thread(task, "AddFilesThread");
    thread.setDaemon(true);
    thread.start();
  }

  private void setLoadingUiMode(boolean isVisible) {
    Platform.runLater(() -> {
      menuBarController.getMenu1().setDisable(isVisible);
      searchTextViewController.getSearchTextField().setDisable(isVisible);
      vBox.setVisible(isVisible);
      if (!isVisible) {
        progressBar.setProgress(0);
      }
    });
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    // doing DI this way prevents Spring circular dependency exception
    searchTextViewController = applicationContext.getBean(SearchTextViewController.class);
    menuBarController = applicationContext.getBean(MenuBarController.class);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}

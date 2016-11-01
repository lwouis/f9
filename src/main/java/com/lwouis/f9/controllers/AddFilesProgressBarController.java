package com.lwouis.f9.controllers;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

@Component
public class AddFilesProgressBarController implements Initializable, ApplicationContextAware, InitializingBean {
  private final WindowsFileAnalyzer windowsFileAnalyzer;

  private final AppState appState;

  private MenuBarController menuBarController;

  @FXML
  private VBox addFilesProgressBarVbox;

  @FXML
  private ProgressBar addFilesProgressBar;

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
    setLoadingUiMode(true);
    Task task = new Task() {
      @Override
      protected Void call() throws Exception {
        List<Item> itemList = new ArrayList<>();
        final int filesSize = files.size();
        for (int i = 0; i < filesSize; i++) {
          final int progress = i;
          Platform.runLater(() -> {
            updateTitle(String.format(message, progress, filesSize));
            updateProgress(progress, filesSize);
          });
          File file = files.get(i);
          File actualFile = windowsFileAnalyzer.resolveWindowsShortcut(file);
          String name = windowsFileAnalyzer.getProductName(actualFile);
          String absolutePath = actualFile.getAbsolutePath();
          Image icon = windowsFileAnalyzer.getFileIcon(actualFile);
          itemList.add(new Item(name, absolutePath, icon));
        }
        Platform.runLater(() -> {
          appState.getObservableItemList().addAll(itemList);
          setLoadingUiMode(false);
        });
        return null;
      }
    };
    addFilesProgressBar.progressProperty().bind(task.progressProperty());
    new Thread(task).run();
  }

  private void setLoadingUiMode(boolean isVisible) {
    Platform.runLater(() -> {
      menuBarController.getMenu1().setDisable(isVisible);
      searchTextViewController.getSearchTextField().setDisable(isVisible);
      addFilesProgressBarVbox.setVisible(isVisible);
      if (!isVisible) {
        addFilesProgressBar.setProgress(0);
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

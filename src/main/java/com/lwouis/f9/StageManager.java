package com.lwouis.f9;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.melloware.jintellitype.HotkeyListener;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Component
public class StageManager implements HotkeyListener {

  private final TrayIconManager trayIconManager;

  private Stage stage;

  @Inject
  public StageManager(TrayIconManager trayIconManager) {
    this.trayIconManager = trayIconManager;
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  public void exitAllThreads() {
    Platform.exit();
    trayIconManager.exitTrayThread();
  }

  @Override
  public void onHotKey(int hotkeyId) {
    if (stage.isShowing()) {
      hideStage();
    }
    else {
      showStageCenteredOnPrimaryDisplay();
    }
  }

  public void hideStage() {
    Platform.runLater(stage::hide);
  }

  public void showStageCenteredOnPrimaryDisplay() {
    Platform.runLater(() -> {
      stage.setX(0); // hack to get window back on primary display
      stage.centerOnScreen();
      stage.setIconified(false);
      stage.show();
    });
  }

  public void initialize(String appName, Parent root) {
    stage.setTitle(appName);
    stage.setScene(new Scene(root));
    stage.setAlwaysOnTop(true);
    // don't close the app when main window is hidden
    Platform.setImplicitExit(false);
    // close the app when the user requests it
    stage.setOnCloseRequest(event -> exitAllThreads());
    stage.iconifiedProperty().addListener((prop, oldValue, newValue) -> {
      if (newValue) {
        Platform.runLater(stage::hide);
      }
    });
  }
}

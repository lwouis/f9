package com.lwouis.falcon9;

import java.util.Collections;
import javax.inject.Inject;

import com.gluonhq.ignite.guice.GuiceContext;
import com.melloware.jintellitype.HotkeyListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application implements HotkeyListener {

  private Stage primaryStage;

  private GuiceContext guiceContext = new GuiceContext(this, () -> Collections.singletonList(new GuiceModule()));

  @Inject
  private FXMLLoader fxmlLoader;

  @Inject
  private DiskPersistanceManager diskPersistanceManager;

  @Inject
  private GlobalHotkeyManager globalHotkeyManager;

  @Inject
  private TrayIconManager trayIconManager;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      guiceContext.init();
      this.primaryStage = primaryStage; // needed in events callback
      setUncaughtExceptionHandlers();
      String appName = "Falcon9";
      trayIconManager.showTrayIcon(this, appName);
      customMinifyAndCloseBehaviour(primaryStage);
      globalHotkeyManager.registerGlobalHotkey(this);
      diskPersistanceManager.loadFromDisk();

      String mainWindowPath = "com/lwouis/falcon9/components/main_window/mainWindow.fxml";
      fxmlLoader.setLocation(ClassLoader.getSystemResource(mainWindowPath));
      Parent root = fxmlLoader.load();
      primaryStage.setTitle(appName);
      primaryStage.setScene(new Scene(root));
      showStageCenteredOnPrimaryDisplay();
      diskPersistanceManager.startSaveToDiskListener();
    }
    catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private void setUncaughtExceptionHandlers() {
    Thread.setDefaultUncaughtExceptionHandler((t, e) -> Platform.runLater(e::printStackTrace));
    Thread.currentThread().setUncaughtExceptionHandler((t, e) -> e.printStackTrace());
  }

  public void exitAllThreads() {
    Platform.exit();
    trayIconManager.exitTrayThread();
  }

  private void customMinifyAndCloseBehaviour(Stage primaryStage) {
    // don't close the app when main window is hidden
    Platform.setImplicitExit(false);
    // close the app when the user requests it
    primaryStage.setOnCloseRequest(event -> exitAllThreads());
    primaryStage.iconifiedProperty().addListener((prop, oldValue, newValue) -> {
      if (newValue) {
        Platform.runLater(primaryStage::hide);
      }
    });
  }

  @Override
  public void stop() {
    globalHotkeyManager.unregisterGlobalHotkey();
  }

  @Override
  public void onHotKey(int hotkeyId) {
    if (globalHotkeyManager.getHotkeyId() != hotkeyId) {
      return; // TODO Log error
    }
    if (primaryStage.isShowing()) {
      Platform.runLater(primaryStage::hide);
    }
    else {
      Platform.runLater(this::showStageCenteredOnPrimaryDisplay);
    }
  }

  public void showStageCenteredOnPrimaryDisplay() {
    primaryStage.setX(0); // hack to get window back on primary display
    primaryStage.centerOnScreen();
    primaryStage.setIconified(false);
    primaryStage.show();
  }
}
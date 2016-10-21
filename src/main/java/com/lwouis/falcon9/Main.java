package com.lwouis.falcon9;

import java.util.Collections;
import javax.inject.Inject;

import com.gluonhq.ignite.guice.GuiceContext;
import com.lwouis.falcon9.injection.GuiceModule;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Main extends Application {

  private GuiceContext guiceContext = new GuiceContext(this, () -> Collections.singletonList(new GuiceModule()));

  @Inject
  private FXMLLoader fxmlLoader;

  @Inject
  private DiskPersistanceManager diskPersistanceManager;

  @Inject
  private GlobalHotkeyManager globalHotkeyManager;

  @Inject
  private TrayIconManager trayIconManager;

  @Inject
  private StageManager stageManager;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      guiceContext.init();
      setUncaughtExceptionHandlers();
      String appName = "Falcon9";
      trayIconManager.showTrayIcon(appName);
      globalHotkeyManager.registerGlobalHotkey(stageManager);
      diskPersistanceManager.loadFromDisk();
      String mainWindowPath = "fxml/mainWindow.fxml";
      fxmlLoader.setLocation(ClassLoader.getSystemResource(mainWindowPath));
      Parent root = fxmlLoader.load();
      stageManager.setStage(primaryStage);
      stageManager.initialize(appName, root);
      stageManager.showStageCenteredOnPrimaryDisplay();
    }
    catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private void setUncaughtExceptionHandlers() {
    Thread.setDefaultUncaughtExceptionHandler((t, e) -> Platform.runLater(e::printStackTrace));
    Thread.currentThread().setUncaughtExceptionHandler((t, e) -> e.printStackTrace());
  }

  @Override
  public void stop() {
    globalHotkeyManager.unregisterGlobalHotkey();
  }

}

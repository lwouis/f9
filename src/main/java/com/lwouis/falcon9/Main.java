package com.lwouis.falcon9;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.ignite.guice.GuiceContext;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.lwouis.falcon9.injection.GuiceModule;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Main extends Application {

  private GuiceContext guiceContext = new GuiceContext(this,
          () -> Arrays.asList(new GuiceModule(), new JpaPersistModule("jpaPersist")));

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

  @Inject
  private PersistService persistService;


    private Logger logger = LoggerFactory.getLogger(getClass().getName());

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      setUncaughtExceptionHandlers();
      guiceContext.init();
      persistService.start();
      final String APP_NAME = "Falcon9";
      trayIconManager.showTrayIcon(APP_NAME);
      globalHotkeyManager.registerGlobalHotkey(stageManager);
      diskPersistanceManager.loadFromDisk();
      fxmlLoader.setResources(ResourceBundle.getBundle("i18n.strings", Locale.getDefault()));
      fxmlLoader.setLocation(ClassLoader.getSystemResource("fxml/MainWindow.fxml"));
      Parent root = fxmlLoader.load();
      stageManager.setStage(primaryStage);
      stageManager.initialize(APP_NAME, root);
      stageManager.showStageCenteredOnPrimaryDisplay();
    }
    catch (Throwable t) {
      logger.error("Failed to start the app.", t);
      stop();
    }
  }

  private void setUncaughtExceptionHandlers() {
    Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
      Platform.runLater(() -> logger.error("Uncaught exception on another thread than the fx-thread.", e));
      stop();
    });
    Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
      Platform.runLater(() -> logger.error("Uncaught exception on the fx-thread.", e));
      stop();
    });
  }

  @Override
  public void stop() {
    globalHotkeyManager.unregisterGlobalHotkey();
    persistService.stop();
  }

}

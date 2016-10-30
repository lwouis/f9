package com.lwouis.falcon9;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.inject.Inject;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.lwouis.falcon9.injection.FxmlLoaderSpringConfiguration;
import com.lwouis.falcon9.injection.JpaSpringConfiguration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Main extends Application {

  @Inject
  private FXMLLoader fxmlLoader;

  @Inject
  private GlobalHotkeyManager globalHotkeyManager;

  @Inject
  private TrayIconManager trayIconManager;

  @Inject
  private StageManager stageManager;

  private Logger logger = LoggerFactory.getLogger(getClass().getName());

  private Server dbServer;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      startSpring();
      setUncaughtExceptionHandlers();
      globalHotkeyManager.registerGlobalHotkey(stageManager);
      fxmlLoader.setResources(ResourceBundle.getBundle("i18n.strings", Locale.getDefault()));
      fxmlLoader.setLocation(ClassLoader.getSystemResource("fxml/MainWindow.fxml"));
      Parent root = fxmlLoader.load();
      stageManager.setStage(primaryStage);
      stageManager.initialize(Environment.APP_NAME, root);
      stageManager.showStageCenteredOnPrimaryDisplay();
      dbServer = Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9092").start();
    }
    catch (Throwable t) {
      logger.error("Failed to start the app.", t);
      stop();
    }
  }

  private void startSpring() {
    AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext();
    appContext.register(JpaSpringConfiguration.class);
    appContext.register(FxmlLoaderSpringConfiguration.class);
    appContext.refresh();
    appContext.getAutowireCapableBeanFactory().autowireBean(this);
  }

  private void setUncaughtExceptionHandlers() {
    Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
      logger.error("Uncaught exception on another thread than the fx-thread.", e);
      Platform.exit();
    });
    Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
      logger.error("Uncaught exception on the fx-thread.", e);
      Platform.exit();
    });
  }

  @Override
  public void stop() {
    globalHotkeyManager.unregisterGlobalHotkey();
    dbServer.stop();
  }

}

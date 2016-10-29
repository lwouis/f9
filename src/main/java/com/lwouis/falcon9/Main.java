package com.lwouis.falcon9;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.inject.Inject;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;
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
import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

public class Main extends Application {

  private GuiceContext guiceContext = new GuiceContext(this, () -> Arrays.asList(new GuiceModule(), customJpaPersistModule()));

  @Inject
  private FXMLLoader fxmlLoader;

  @Inject
  private GlobalHotkeyManager globalHotkeyManager;

  @Inject
  private TrayIconManager trayIconManager;

  @Inject
  private StageManager stageManager;

  @Inject
  private PersistService persistService;

  private Logger logger = LoggerFactory.getLogger(getClass().getName());

  private Server dbServer;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      setUncaughtExceptionHandlers();
      guiceContext.init();
      persistService.start();

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
    try {
      persistService.stop();
    }
    catch (Throwable t) {
      logger.error("Failed to stop PersistenceService.", t);
    }
  }

  private JpaPersistModule customJpaPersistModule() {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + Environment.USER_HOME_APP_FOLDER + "db/app");
    ProxyDataSource proxyDataSource = ProxyDataSourceBuilder.create(dataSource).logQueryBySlf4j("net.ttddyy.dsproxy")
            .countQuery().build();
    Map<String, Object> properties = new HashMap<>();
    properties.put(org.hibernate.cfg.Environment.DATASOURCE, proxyDataSource);
    return new JpaPersistModule("jpaPersist").properties(properties);
  }

}

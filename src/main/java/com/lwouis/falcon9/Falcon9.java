package com.lwouis.falcon9;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

import com.melloware.jintellitype.HotkeyListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Falcon9 extends Application implements HotkeyListener {
  private final String APP_NAME = "Falcon9";

  private Stage primaryStage;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    this.primaryStage = primaryStage;
    SystemTray tray = showTrayIcon(primaryStage);
    customMinifyAndCloseBehaviour(primaryStage, tray);

    GlobalHotkeyManager.registerGlobalHotkey(this);
    DiskPersistanceManager.loadFromDisk();

    @SuppressWarnings("ConstantConditions")
    Parent root = FXMLLoader
            .load(ClassLoader.getSystemResource("com/lwouis/falcon9/components/main_window/mainWindow.fxml"));
    primaryStage.setTitle(APP_NAME);
    primaryStage.setScene(new Scene(root));
    primaryStage.centerOnScreen();
    primaryStage.show();
    DiskPersistanceManager.startSaveToDiskListener();
  }

  private SystemTray showTrayIcon(Stage stage) throws IOException, AWTException {
    if (!SystemTray.isSupported()) {
      return null;
    }
    SystemTray tray = SystemTray.getSystemTray();
    int trayHeight = (int)tray.getTrayIconSize().getHeight();
    String iconPath = "trayIcon/icon1_" + trayHeight + ".png";
    URL systemResource = ClassLoader.getSystemResource(iconPath);
    if (systemResource == null) {
      System.err.println("Didn't find icon for SystemTray at path " + iconPath);
      return tray;
    }
    Image image = ImageIO.read(systemResource);

    TrayIcon trayIcon = new TrayIcon(image, APP_NAME);
    trayIcon.addActionListener(e -> Platform.runLater(() -> showStageCenteredOnPrimaryDisplay(stage)));
    tray.add(trayIcon);
    MenuItem closeItem = new MenuItem("Quit");
    // https://bugs.openjdk.java.net/browse/JDK-4039705
    //closeItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    PopupMenu popup = new PopupMenu();
    popup.add(closeItem);
    trayIcon.setPopupMenu(popup);
    closeItem.addActionListener(e -> exitAllThreads(tray));
    return tray;
  }

  private void exitAllThreads(SystemTray tray) {
    Platform.exit();
    if (tray != null) {
      for (TrayIcon icon : tray.getTrayIcons()) {
        tray.remove(icon);
      }
    }
  }

  private void customMinifyAndCloseBehaviour(Stage primaryStage, SystemTray tray) {
    // don't close the app when main window is hidden
    Platform.setImplicitExit(false);
    // close the app when the user requests it
    primaryStage.setOnCloseRequest(event -> exitAllThreads(tray));
    primaryStage.iconifiedProperty().addListener((prop, oldValue, newValue) -> {
      if (newValue) {
        Platform.runLater(primaryStage::hide);
      }
    });
  }

  @Override
  public void stop() {
    GlobalHotkeyManager.unregisterGlobalHotkey();
  }

  @Override
  public void onHotKey(int hotkeyId) {
    if (GlobalHotkeyManager.getHotkeyId() != hotkeyId) {
      return; // TODO Log error
    }
    if (primaryStage.isShowing()) {
      Platform.runLater(primaryStage::hide);
    }
    else {
      Platform.runLater(() -> showStageCenteredOnPrimaryDisplay(primaryStage));
    }
  }

  private void showStageCenteredOnPrimaryDisplay(Stage stage) {
    stage.setX(0); // hack to get window back on primary display
    stage.centerOnScreen();
    stage.setIconified(false);
    stage.show();
  }
}

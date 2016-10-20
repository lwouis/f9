package com.lwouis.falcon9;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

import javafx.application.Platform;

public class TrayIconManager {

  private SystemTray tray;

  public void showTrayIcon(Main main, String appName) throws IOException, AWTException {
    if (!SystemTray.isSupported()) {
      return;
    }
    tray = SystemTray.getSystemTray();
    int trayHeight = (int)tray.getTrayIconSize().getHeight();
    String iconPath = "trayIcon/icon1_" + trayHeight + ".png";
    URL systemResource = ClassLoader.getSystemResource(iconPath);
    Image image = ImageIO.read(systemResource);
    TrayIcon trayIcon = new TrayIcon(image, appName);
    trayIcon.addActionListener(e -> Platform.runLater(main::showStageCenteredOnPrimaryDisplay));
    tray.add(trayIcon);
    MenuItem closeItem = new MenuItem("Quit");
    // https://bugs.openjdk.java.net/browse/JDK-4039705
    //closeItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    PopupMenu popup = new PopupMenu();
    popup.add(closeItem);
    trayIcon.setPopupMenu(popup);
    closeItem.addActionListener(e -> main.exitAllThreads());
  }

  public void exitTrayThread() {
    if (!SystemTray.isSupported() || tray == null) {
      return;
    }
    for (TrayIcon icon : tray.getTrayIcons()) {
      tray.remove(icon);
    }
  }
}

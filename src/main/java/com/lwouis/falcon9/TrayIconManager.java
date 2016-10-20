package com.lwouis.falcon9;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

import javafx.application.Platform;

public class TrayIconManager {

  private SystemTray tray;

  public void showTrayIcon(Falcon9 falcon9, String appName) throws IOException, AWTException {
    if (!SystemTray.isSupported()) {
      return;
    }
    tray = SystemTray.getSystemTray();
    int trayHeight = (int)tray.getTrayIconSize().getHeight();
    String iconPath = "trayIcon/icon1_" + trayHeight + ".png";
    URL systemResource = ClassLoader.getSystemResource(iconPath);
    if (systemResource == null) {
      System.err.println("Didn't find icon for SystemTray at path " + iconPath);
      return;
    }
    Image image = ImageIO.read(systemResource);

    TrayIcon trayIcon = new TrayIcon(image, appName);
    trayIcon.addActionListener(e -> Platform.runLater(falcon9::showStageCenteredOnPrimaryDisplay));
    tray.add(trayIcon);
    MenuItem closeItem = new MenuItem("Quit");
    // https://bugs.openjdk.java.net/browse/JDK-4039705
    //closeItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    PopupMenu popup = new PopupMenu();
    popup.add(closeItem);
    trayIcon.setPopupMenu(popup);
    closeItem.addActionListener(e -> falcon9.exitAllThreads());
  }

  public void exitTrayThread() {
    if (tray != null) {
      for (TrayIcon icon : tray.getTrayIcons()) {
        tray.remove(icon);
      }
    }
  }
}

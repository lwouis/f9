package com.lwouis.falcon9;

import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;

import com.lwouis.falcon9.injection.InjectLogger;

@Singleton
public class TrayIconManager {

  private SystemTray tray;

  private final Provider<StageManager> stageManager;

  @InjectLogger
  private Logger logger;

  @Inject
  public TrayIconManager(Provider<StageManager> stageManager) {
    this.stageManager = stageManager;
  }

  public void showTrayIcon(String appName) {
    try {
      tray = SystemTray.getSystemTray();
      int trayHeight = (int)tray.getTrayIconSize().getHeight();
      String iconPath = "trayIcon/icon1_" + trayHeight + ".png";
      URL systemResource = ClassLoader.getSystemResource(iconPath);
      Image image = ImageIO.read(systemResource);
      TrayIcon trayIcon = new TrayIcon(image, appName);
      trayIcon.addActionListener(e -> stageManager.get().showStageCenteredOnPrimaryDisplay());
      tray.add(trayIcon);
      MenuItem closeItem = new MenuItem("Quit");
      // https://bugs.openjdk.java.net/browse/JDK-4039705
      closeItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
      PopupMenu popup = new PopupMenu();
      popup.add(closeItem);
      trayIcon.setPopupMenu(popup);
      closeItem.addActionListener(e -> stageManager.get().exitAllThreads());
    }
    catch (Throwable t) {
      logger.error("Failed to show the system tray icon.", t);
    }
  }

  public void exitTrayThread() {
    if (tray == null) {
      return;
    }
    for (TrayIcon icon : tray.getTrayIcons()) {
      tray.remove(icon);
    }
  }
}

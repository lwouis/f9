package com.lwouis.falcon9;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.inject.Singleton;

import org.apache.logging.log4j.Logger;

import com.lwouis.falcon9.injection.InjectLogger;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

@Singleton
public class GlobalHotkeyManager {

  @InjectLogger
  private Logger logger;

  private static final int HOTKEY_ID = 0;

  private static JIntellitype jIntellitype;

  public void registerGlobalHotkey(HotkeyListener hotkeyListener) throws IOException, URISyntaxException {
    JIntellitype.setLibraryLocation(copyOfDllFromJar());
    jIntellitype = JIntellitype.getInstance();
    jIntellitype.registerHotKey(HOTKEY_ID, JIntellitype.MOD_SHIFT, KeyEvent.VK_TAB);
    jIntellitype.addHotKeyListener(hotkeyListener);
  }

  public void unregisterGlobalHotkey() {
    jIntellitype.unregisterHotKey(HOTKEY_ID);
    jIntellitype.cleanUp();
  }

  private String copyOfDllFromJar() {
    try {
      String dllFile = "JIntellitype64.dll";
      // 64bit check from http://stackoverflow.com/a/2269242
      boolean is64bit = System.getenv("ProgramFiles(x86)") != null;
      if (!is64bit) {
        dllFile = "JIntellitype.dll";
      }
      // System.load() inside JIntellitype.getInstance() fails path is inside a JAR, thus we copy the dll outside
      // the JAR and provide that path instead
      String tmpFilePath = Environment.TPM_DIR + dllFile;
      URL resourceUrl = getClass().getResource("/" + dllFile);
      File tmpFile = new File(tmpFilePath);
      org.apache.commons.io.FileUtils.copyURLToFile(resourceUrl, tmpFile);
      return tmpFilePath;
    }
    catch (Throwable t) {
      logger.error("Failed to extract file from Jar.", t);
      return null;
    }
  }
}

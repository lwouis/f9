package com.lwouis.falcon9;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.inject.Singleton;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

@Singleton
public class GlobalHotkeyManager {

  private static final int HOTKEY_ID = 0;

  private static JIntellitype jIntellitype = JIntellitype.getInstance();

  public void registerGlobalHotkey(HotkeyListener hotkeyListener) throws IOException, URISyntaxException {
    String dllFile = "JIntellitype64.dll";
    // 64bit check from http://stackoverflow.com/a/2269242
    boolean is64bit = System.getenv("ProgramFiles(x86)") != null;
    if (!is64bit) {
      dllFile = "JIntellitype.dll";
    }
    String tmpFilePath = Environment.TPM_DIR + dllFile;
    URI dllFilePath = ClassLoader.getSystemResource(dllFile).toURI();
    Files.copy(Paths.get(dllFilePath), Paths.get(tmpFilePath), StandardCopyOption.REPLACE_EXISTING);
    JIntellitype.setLibraryLocation(tmpFilePath);
    jIntellitype.registerHotKey(HOTKEY_ID, JIntellitype.MOD_SHIFT, KeyEvent.VK_TAB);
    jIntellitype.addHotKeyListener(hotkeyListener);
}

  public void unregisterGlobalHotkey() {
    jIntellitype.unregisterHotKey(HOTKEY_ID);
    jIntellitype.cleanUp();
  }
}

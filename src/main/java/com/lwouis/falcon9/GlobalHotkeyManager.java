package com.lwouis.falcon9;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

public class GlobalHotkeyManager {

  private static final int hotkeyId = 0;

  private static final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

  private static JIntellitype jIntellitype;

  public static int getHotkeyId() {
    return hotkeyId;
  }

  public static void registerGlobalHotkey(HotkeyListener hotkeyListener) {
    if (!isWindows) {
      return;
    }
    String dllFile = "JIntellitype64.dll";
    // 64bit check from http://stackoverflow.com/a/2269242
    boolean is64bit = System.getenv("ProgramFiles(x86)") != null;
    if (!is64bit) {
      dllFile = "JIntellitype.dll";
    }
    String dllPath = ClassLoader.getSystemClassLoader().getResource(dllFile).getPath();
    JIntellitype.setLibraryLocation(dllPath);
    jIntellitype = JIntellitype.getInstance();
    jIntellitype.registerHotKey(hotkeyId, JIntellitype.MOD_ALT + JIntellitype.MOD_SHIFT, (int)'A');
    jIntellitype.addHotKeyListener(hotkeyListener);
  }

  public static void unregisterGlobalHotkey() {
    if (!isWindows) {
      return;
    }
    jIntellitype.unregisterHotKey(hotkeyId);
    jIntellitype.cleanUp();
  }
}

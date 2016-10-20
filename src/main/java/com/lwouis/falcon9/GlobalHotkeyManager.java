package com.lwouis.falcon9;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

public class GlobalHotkeyManager {

  private static final int hotkeyId = 0;

  private static JIntellitype jIntellitype;

  public static int getHotkeyId() {
    return hotkeyId;
  }

  public static void registerGlobalHotkey(HotkeyListener hotkeyListener) throws IOException {
    if (!Environment.IS_WINDOWS) {
      return;
    }
    String dllFile = "JIntellitype64.dll";
    // 64bit check from http://stackoverflow.com/a/2269242
    boolean is64bit = System.getenv("ProgramFiles(x86)") != null;
    if (!is64bit) {
      dllFile = "JIntellitype.dll";
    }
    String tmpFile = Environment.TPM_DIR + dllFile;
    fromJarToFs(dllFile, tmpFile);
    JIntellitype.setLibraryLocation(tmpFile);
    jIntellitype = JIntellitype.getInstance();
    jIntellitype.registerHotKey(hotkeyId, JIntellitype.MOD_ALT + JIntellitype.MOD_SHIFT, (int)'A');
    jIntellitype.addHotKeyListener(hotkeyListener);
  }

  public static void unregisterGlobalHotkey() {
    if (!Environment.IS_WINDOWS) {
      return;
    }
    jIntellitype.unregisterHotKey(hotkeyId);
    jIntellitype.cleanUp();
  }

  private static void fromJarToFs(String jarPath, String filePath) throws IOException {
    InputStream is = null;
    OutputStream os = null;
    try {
      File file = new File(filePath);
      if (file.exists()) {
        boolean success = file.delete();
        if (!success) {
          throw new IOException("Could not delete file: " + filePath);
        }
      }

      is = ClassLoader.getSystemResourceAsStream(jarPath);
      os = new FileOutputStream(filePath);
      byte[] buffer = new byte[8192];
      int bytesRead;
      while ((bytesRead = is.read(buffer)) != -1) {
        os.write(buffer, 0, bytesRead);
      }
    }
    catch (Exception ex) {
      throw new IOException("FromJarToFileSystem could not load DLL: " + jarPath, ex);
    }
    finally {
      if (is != null) {
        is.close();
      }
      if (os != null) {
        os.close();
      }
    }
  }
}

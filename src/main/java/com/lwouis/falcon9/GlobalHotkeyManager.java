package com.lwouis.falcon9;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Singleton;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

@Singleton
public class GlobalHotkeyManager {

  private static final int HOTKEY_ID = 0;

  private static JIntellitype jIntellitype;

  public int getHotkeyId() {
    return HOTKEY_ID;
  }

  public void registerGlobalHotkey(HotkeyListener hotkeyListener) throws IOException {
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
    jIntellitype.registerHotKey(HOTKEY_ID, JIntellitype.MOD_ALT + JIntellitype.MOD_SHIFT, (int)'A');
    jIntellitype.addHotKeyListener(hotkeyListener);
  }

  public void unregisterGlobalHotkey() {
    jIntellitype.unregisterHotKey(HOTKEY_ID);
    jIntellitype.cleanUp();
  }

  private void fromJarToFs(String jarPath, String filePath) throws IOException {
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

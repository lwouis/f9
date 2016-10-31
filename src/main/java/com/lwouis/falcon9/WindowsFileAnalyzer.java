package com.lwouis.falcon9;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import org.boris.pecoff4j.PE;
import org.boris.pecoff4j.ResourceDirectory;
import org.boris.pecoff4j.ResourceEntry;
import org.boris.pecoff4j.constant.ResourceType;
import org.boris.pecoff4j.io.PEParser;
import org.boris.pecoff4j.io.ResourceParser;
import org.boris.pecoff4j.resources.StringPair;
import org.boris.pecoff4j.resources.StringTable;
import org.boris.pecoff4j.resources.VersionInfo;
import org.boris.pecoff4j.util.ResourceHelper;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.stackoverflowusers.file.WindowsShortcut;

import com.lwouis.falcon9.injection.InjectLogger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import sun.awt.shell.ShellFolder;

@Component
public class WindowsFileAnalyzer {

  private final String networkFilePrefix = "\\\\";

  private final List<String> buggyShortcutResolutions = Arrays.asList(networkFilePrefix, ".", "i");

  @InjectLogger
  private Logger logger;

  public File resolveWindowsShortcut(File file) {
    try {
      if (WindowsShortcut.isPotentialValidLink(file)) {
        String realFilename = new WindowsShortcut(file).getRealFilename();
        if (buggyShortcutResolutions.contains(realFilename)) { // buggy .lnk shortcut
          return file;
        }
        return new File(realFilename);
      }
      else {
        return file;
      }
    }
    catch (IOException | ParseException e) {
      e.printStackTrace();
      return file;
    }
  }

  public String getProductName(File file) {
    try {
      if (file.getAbsolutePath().startsWith(networkFilePrefix)) { // network files are too long to resolve
        return file.getName();
      }
      PE pe = PEParser.parse(file);
      ResourceDirectory rd = pe.getImageData().getResourceTable();
      ResourceEntry[] entries = ResourceHelper.findResources(rd, ResourceType.VERSION_INFO);
      if (entries.length == 0) {
        logger.info("Failed to retrieve pretty file name for file: {}.", file.getAbsolutePath());
        return file.getName();
      }
      VersionInfo versionInfo = ResourceParser.readVersionInfo(entries[0].getData());
      StringTable properties = versionInfo.getStringFileInfo().getTable(0);
      for (int i = 0; i < properties.getCount(); i++) {
        StringPair property = properties.getString(i);
        if (property.getKey().equals("ProductName")) {
          return property.getValue();
        }
      }
      logger.info("Failed to retrieve pretty file name for file: {}.", file.getAbsolutePath());
      return file.getName();
    }
    catch (Throwable t) {
      logger.info("Failed to retrieve pretty file name for file: {}.", file.getAbsolutePath(), t);
      return file.getName();
    }
  }

  public Image getFileIcon(File file) {
    try {
      if (file.getAbsolutePath().startsWith(networkFilePrefix)) { // network files are too long to resolve
        return null;
      }
      java.awt.Image icon = ShellFolder.getShellFolder(file).getIcon(true); // true is 32x32, false if 16x16
      BufferedImage bufferedImage = new BufferedImage(icon.getWidth(null), icon.getHeight(null),
          BufferedImage.TYPE_INT_ARGB);
      Graphics2D bGr = bufferedImage.createGraphics();
      bGr.drawImage(icon, 0, 0, null);
      bGr.dispose();
      return SwingFXUtils.toFXImage(bufferedImage, null);
    }
    catch (Throwable t) {
      logger.info("Failed to retrieve icon for file: {}.", file.getAbsolutePath());
      return null;
    }
  }
}

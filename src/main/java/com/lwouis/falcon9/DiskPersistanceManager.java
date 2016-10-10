package com.lwouis.falcon9;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;

// TODO checksum on file before overwriting
// TODO atomic file save (copy then move) to avoid data corruption in unlucky cases
// TODO sequential file rotation so if corrupted, can fallback to previous working file
public class DiskPersistanceManager {
  private static final File JSON_FILE = Paths.get(System.getProperty("user.home") + "/.falcon9/appState.json").toFile();

  private static final Gson gson = new Gson();

  private static final Type type = new TypeToken<List<String>>() {
  }.getType();

  public static void startSaveToDiskListener() {
    AppState.getItemList().addListener((ListChangeListener<String>)change -> new Thread(new Task() {
      @Override
      protected Void call() throws Exception {
        try {
          DiskPersistanceManager.saveToDisk();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }
    }).start());
  }

  private static void saveToDisk() throws IOException {
    String json = gson.toJson(AppState.getItemList(), type);
    Files.createDirectories(JSON_FILE.toPath().getParent());
    FileUtils.writeStringToFile(JSON_FILE, json, StandardCharsets.UTF_8, false);
    System.out.println(json);
  }

  public static void loadFromDisk() {
    if (!JSON_FILE.exists()) {
      return;
    }
    try {
      String json = FileUtils.readFileToString(JSON_FILE, StandardCharsets.UTF_8);
      List<String> appState = gson.fromJson(json, type);
      AppState.getItemList().setAll(appState);
    }
    catch (Exception e) {
      e.printStackTrace();
      // TODO: notify user that their data is lost, and what they could do
    }
  }
}

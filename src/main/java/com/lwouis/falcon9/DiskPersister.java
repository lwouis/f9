package com.lwouis.falcon9;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DiskPersister {
  private static final File JSON_FILE = Paths.get(System.getProperty("user.home") + "/.falcon9/appState.json").toFile();

  private static final int savePeriod = Math.toIntExact(TimeUnit.MINUTES.toSeconds(30));

  private static final Timer timer = new Timer();

  private static final Gson gson = new Gson();

  private static final Type type = new TypeToken<List<String>>() {
  }.getType();

  private static TimerTask task;

  public static void startRecurrentSaveToDisk() {
    task = new TimerTask() {
      @Override
      public void run() {
        try {
          DiskPersister.dihydrateToDisk();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    timer.schedule(task, 0, savePeriod);
  }

  public static void stopRecurrentSaveToDisk() {
    task.run();
    timer.cancel();
  }

  public static void dihydrateToDisk() throws IOException {
    String json = gson.toJson(AppState.getItemList(), type);
    Files.createDirectories(JSON_FILE.toPath().getParent());
    FileUtils.writeStringToFile(JSON_FILE, json, StandardCharsets.UTF_8, false);
  }

  public static void rehydrateFromDisk() {
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

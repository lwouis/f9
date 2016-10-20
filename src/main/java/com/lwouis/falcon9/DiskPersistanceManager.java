package com.lwouis.falcon9;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lwouis.falcon9.models.Launchable;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;

@Singleton
public class DiskPersistanceManager {
  private static final File JSON_FILE = Paths.get(Environment.USER_HOME + "/.falcon9/appState.json").toFile();

  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  private static final Type type = new TypeToken<List<Launchable>>() {
  }.getType();

  private final AppState appState;

  @Inject
  public DiskPersistanceManager(AppState appState) {
    this.appState = appState;
  }

  public void startSaveToDiskListener() {
    appState.getLaunchableObservableList().addListener((ListChangeListener<Launchable>)change -> new Thread(new Task() {
      @Override
      protected Void call() throws Exception {
        try {
          saveToDisk();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }
    }).start());
  }

  private void saveToDisk() throws IOException {
    try {
      String json = gson.toJson(appState.getLaunchableObservableList(), type);
      Files.createDirectories(JSON_FILE.toPath().getParent());
      FileUtils.writeStringToFile(JSON_FILE, json, StandardCharsets.UTF_8, false);
    }
    catch (Throwable e) {
      System.err.println("Failure during DiskPersistanceManager.saveToDisk()");
      e.printStackTrace();
      // TODO: do something about the save failure
    }
  }

  public void loadFromDisk() {
    loadFromDiskInternal(JSON_FILE);
  }

  public void loadFromDisk(File file) {
    loadFromDiskInternal(file);
  }

  private void loadFromDiskInternal(File file) {
    if (!file.exists()) {
      return;
    }
    try {
      String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
      List<Launchable> launchables = gson.fromJson(json, type);
      appState.getLaunchableObservableList().setAll(launchables);
    }
    catch (Throwable e) {
      System.err.println("Failure during DiskPersistanceManager.loadFromDiskInternal()");
      e.printStackTrace();
      // TODO: notify user that their data is lost, and what they could do
    }
  }
}

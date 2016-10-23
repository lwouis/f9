package com.lwouis.falcon9;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.lwouis.falcon9.injection.InjectLogger;
import com.lwouis.falcon9.models.Launchable;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

@Singleton
public class DiskPersistanceManager {
  private static final File jsonFile = Paths.get(Environment.USER_HOME + "/.falcon9/appState.json").toFile();

  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  private static final Type type = new TypeToken<List<Launchable>>() {
  }.getType();

  @InjectLogger
  private Logger logger;

  private ObservableList<Launchable> launchableObservableList;

  @Inject
  public DiskPersistanceManager(AppState appState) {
    launchableObservableList = appState.getLaunchableObservableList();
    launchableObservableList.addListener((ListChangeListener<Launchable>)change -> new Thread(new Task<Void>() {
      @Override
      protected Void call() {
        saveToDisk();
        return null;
      }
    }).start());
  }

  private void saveToDisk() {
    try {
      String json = serializeToJson();
      Files.createDirectories(jsonFile.toPath().getParent());
      FileUtils.writeStringToFile(jsonFile, json, StandardCharsets.UTF_8, false);
    }
    catch (Throwable t) {
      logger.error("Failed to save appState to disk.", t);
    }
  }

  public void loadFromDisk() {
    loadFromDiskInternal(jsonFile);
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
      List<Launchable> launchables = deserializeFromJson(json);
      launchableObservableList.setAll(launchables);
    }
    catch (Throwable t) {
      logger.error("Failed to load appState from disk.", t);
    }
  }

  private String serializeToJson() throws JsonIOException {
    try {
      return gson.toJson(launchableObservableList, type);
    }
    catch (Throwable t) {
      throw new JsonIOException("Failed to serialize AppState to JSON", t);
    }
  }

  private List<Launchable> deserializeFromJson(String json) throws JsonIOException {
    try {
      return gson.fromJson(json, type);
    }
    catch (Throwable t) {
      throw new JsonIOException("Failed to deserialize AppState from JSON", t);
    }
  }
}

package com.lwouis.falcon9;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.lwouis.falcon9.injection.InjectLogger;
import com.lwouis.falcon9.models.Item;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

@Singleton
public class DiskPersistenceManager implements ListChangeListener<Item> {
  private static final File jsonFile = Paths.get(Environment.USER_HOME_APP_FOLDER + "appState.json").toFile();

  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  private static final Type type = new TypeToken<List<Item>>() {
  }.getType();

  private final AppState appState;

  private final Provider<EntityManager> entityManager;

  @InjectLogger
  private Logger logger;

  private final Service<Void> service = new Service<Void>() {
    @Override
    protected Task<Void> createTask() {
      return new Task<Void>() {
        @Override
        protected Void call() {
          try {
            ArrayList<Item> copy = oneLevelDeepCopy(appState.getItemObservableList());
            String json = serializeToJson(copy);
            Files.createDirectories(jsonFile.toPath().getParent());
            FileUtils.writeStringToFile(jsonFile, json, StandardCharsets.UTF_8, false);
          }
          catch (Throwable t) {
            logger.error("Failed to save appState to disk.", t);
          }
          return null;
        }
      };
    }
  };

  private ArrayList<Item> oneLevelDeepCopy(List<Item> list) {
    ArrayList<Item> itemObservableList = new ArrayList<>();
    for (Item item : list) {
      itemObservableList.add(item.oneLevelDeepCopy());
    }
    return itemObservableList;
  }

  @Inject
  public DiskPersistenceManager(AppState appState, Provider<EntityManager> entityManager) {
    this.appState = appState;
    this.entityManager = entityManager;
    appState.getItemObservableList().addListener(this);
  }

  @Override
  public void onChanged(Change<? extends Item> c) {
    Platform.runLater(service::restart);
  }

  public void loadFromDisk() {
    loadFromDiskInternal(jsonFile);
  }

  public void loadFromDisk(File file) {
    loadFromDiskInternal(file);
  }

  @Transactional
  private void loadFromDiskInternal(File file) {
    if (!file.exists()) {
      return;
    }
    try {
      //entityManager.get().find
      String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
      List<Item> items = deserializeFromJson(json);
      appState.getItemObservableList().setAll(items);
    }
    catch (Throwable t) {
      logger.error("Failed to load appState from disk.", t);
    }
  }

  private String serializeToJson(List<Item> itemObservableList) throws JsonIOException {
    try {
      return gson.toJson(itemObservableList, type);
    }
    catch (Throwable t) {
      throw new JsonIOException("Failed to serialize AppState to JSON", t);
    }
  }

  private List<Item> deserializeFromJson(String json) throws JsonIOException {
    try {
      return gson.fromJson(json, type);
    }
    catch (Throwable t) {
      throw new JsonIOException("Failed to deserialize AppState from JSON", t);
    }
  }
}

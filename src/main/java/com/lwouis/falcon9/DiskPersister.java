package com.lwouis.falcon9;

import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;

public class DiskPersister {
  private static final String USER_CONFIG_PATH = Paths.get(System.getProperty("user.home") + "/.falcon9/appState.json")
          .toAbsolutePath().toString();

  private static TimerTask task;

  private static Timer timer;

  public static void startRecurrentSaveToDisk() {
    task = new TimerTask() {
      @Override
      public void run() {
        DiskPersister.persistToDisk(AppState.getItemList());
      }
    };
    timer = new Timer();
    timer.schedule(task, 0, 5000);
  }

  public static void stopRecurrentSaveToDisk() {
    task.run();
    timer.cancel();
  }

  public static void persistToDisk(Object object) {
    Gson gson = new Gson();
    String json = gson.toJson(object);
    System.out.println(json);
  }
}

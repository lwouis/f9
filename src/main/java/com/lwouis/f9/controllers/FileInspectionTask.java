package com.lwouis.f9.controllers;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import com.lwouis.f9.WindowsFileAnalyzer;
import com.lwouis.f9.models.Item;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

public class FileInspectionTask extends Task {
  private final Item item;

  private final WindowsFileAnalyzer windowsFileAnalyzer;

  private final File file;

  private final CountDownLatch countDownLatch;

  public FileInspectionTask(Item item, File file, CountDownLatch countDownLatch,
      WindowsFileAnalyzer windowsFileAnalyzer) {
    this.item = item;
    this.file = file;
    this.countDownLatch = countDownLatch;
    this.windowsFileAnalyzer = windowsFileAnalyzer;
  }

  @Override
  protected Void call() throws Exception {
    File actualFile = windowsFileAnalyzer.resolveWindowsShortcut(file);
    String name = windowsFileAnalyzer.getProductName(actualFile);
    String absolutePath = actualFile.getAbsolutePath();
    Image icon = windowsFileAnalyzer.getFileIcon(actualFile);
    Platform.runLater(() -> {
      item.nameProperty().set(name);
      item.absolutePathProperty().set(absolutePath);
      item.iconProperty().set(icon);
    });
    countDownLatch.countDown();
    return null;
  }
}

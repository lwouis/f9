package com.lwouis.falcon9.models;

import com.google.gson.annotations.JsonAdapter;
import javafx.scene.image.Image;

public class Launchable {
  private String name;

  private String absolutePath;

  @JsonAdapter(ImageJsonSerializer.class)
  private Image image;

  public Launchable(String name, String absolutePath, Image image) {
    this.name = name;
    this.absolutePath = absolutePath;
    this.image = image;
  }

  public Image getImage() {
    return image;
  }

  public String getName() {
    return name;
  }

  public String getAbsolutePath() {
    return absolutePath;
  }
}

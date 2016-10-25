package com.lwouis.falcon9.models;

import com.google.gson.annotations.JsonAdapter;
import javafx.scene.image.Image;

public class Item {
  private String name;

  private String absolutePath;

  @JsonAdapter(ImageJsonSerializer.class)
  private Image image;

  public Item(String name, String absolutePath, Image image) {
    this.name = name;
    this.absolutePath = absolutePath;
    this.image = image;
  }

  public Item oneLevelDeepCopy() {
    return new Item(name, absolutePath, image); // image is not cloned because it's not needed to solve // access issues
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

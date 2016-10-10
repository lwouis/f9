package com.lwouis.falcon9.models;

public class Launchable {
  private String name;

  private String absolutePath;

  public Launchable(String name, String absolutePath) {
    this.name = name;
    this.absolutePath = absolutePath;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAbsolutePath() {
    return absolutePath;
  }

  public void setAbsolutePath(String absolutePath) {
    this.absolutePath = absolutePath;
  }
}

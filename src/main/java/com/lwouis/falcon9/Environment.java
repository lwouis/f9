package com.lwouis.falcon9;

public interface Environment {
  boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");
  boolean IS_MACOS = System.getProperty("os.name").toLowerCase().startsWith("mac");
  String USER_HOME = System.getProperty("user.home");
}

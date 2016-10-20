package com.lwouis.falcon9;

public interface Environment {
  boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");
  String USER_HOME = System.getProperty("user.home");
  String TPM_DIR = System.getProperty("java.io.tmpdir");
}

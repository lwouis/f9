package com.lwouis.falcon9;

public interface Environment {
  String USER_HOME_APP_FOLDER = System.getProperty("user.home") + "/.falcon9/";
  String TPM_DIR = System.getProperty("java.io.tmpdir");
}

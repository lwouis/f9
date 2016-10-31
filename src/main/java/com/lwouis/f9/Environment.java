package com.lwouis.f9;

public interface Environment {
  String APP_NAME = "F9";
  String USER_HOME_APP_FOLDER = System.getProperty("user.home") + "/.f9/";
  String TPM_DIR = System.getProperty("java.io.tmpdir");
}

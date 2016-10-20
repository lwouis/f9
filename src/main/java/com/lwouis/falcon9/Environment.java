package com.lwouis.falcon9;

public interface Environment {
  String USER_HOME = System.getProperty("user.home");
  String TPM_DIR = System.getProperty("java.io.tmpdir");
}

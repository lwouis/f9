package com.lwouis.falcon9.injection;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class GuiceModule extends AbstractModule {
  @Override
  protected void configure() {
    // Most bindings are done through annotations
    bindListener(Matchers.any(), new Log4JTypeListener());
  }
}
package com.lwouis.falcon9.injection;

import java.lang.reflect.Field;

import org.slf4j.Logger;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class Log4JTypeListener implements TypeListener {

  public <T> void hear(TypeLiteral<T> typeLiteral, TypeEncounter<T> typeEncounter) {
    Class<?> clazz = typeLiteral.getRawType();
    while (clazz != null) {
      for (Field field : clazz.getDeclaredFields()) {
        if (field.getType() == Logger.class && field.isAnnotationPresent(InjectLogger.class)) {
          typeEncounter.register(new Log4JMembersInjector<>(field));
        }
      }
      clazz = clazz.getSuperclass();
    }
  }
}

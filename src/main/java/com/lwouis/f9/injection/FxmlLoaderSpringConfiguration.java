package com.lwouis.f9.injection;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javafx.fxml.FXMLLoader;

@Configuration
public class FxmlLoaderSpringConfiguration implements ApplicationContextAware {

  private ApplicationContext appContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.appContext = applicationContext;
  }

  @Bean()
  public FXMLLoader provideFxmlLoader() {
    FXMLLoader loader = new FXMLLoader();
    loader.setControllerFactory(clazz -> appContext.getBean(clazz));
    return loader;
  }

}

package com.lwouis.f9.injection;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
public class LoggerBeanPostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessBeforeInitialization(final Object bean, String name) throws BeansException {
    ReflectionUtils.doWithFields(bean.getClass(), field -> {
      // make the field accessible if defined private
      ReflectionUtils.makeAccessible(field);
      if (field.getAnnotation(InjectLogger.class) != null) {
        org.slf4j.Logger logger = LoggerFactory.getLogger(bean.getClass());
        field.set(bean, logger);
      }
    });
    return bean;
  }
}

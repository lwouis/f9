package com.lwouis.falcon9.injection;

import java.util.Properties;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.lwouis.falcon9.Environment;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

@Configuration
@EnableTransactionManagement
@ComponentScan("com.lwouis.falcon9")
// Fixes com.sun.proxy issue (see http://stackoverflow.com/a/18700986)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class JpaSpringConfiguration {

  @Bean
  public EntityManager entityManager() {
    return transactionManager().getEntityManagerFactory().createEntityManager();
  }

  @Bean
  public JpaTransactionManager transactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
    return transactionManager;
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
    entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
    entityManagerFactoryBean.setPackagesToScan("com.lwouis.falcon9.models");
    entityManagerFactoryBean.setDataSource(dataSource());
    entityManagerFactoryBean.setJpaProperties(jpaProperties());
    return entityManagerFactoryBean;
  }

  @Bean
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.h2.Driver");
    dataSource.setUrl("jdbc:h2:" + Environment.USER_HOME_APP_FOLDER + "db/app");
    return ProxyDataSourceBuilder.create(dataSource).logQueryBySlf4j("net.ttddyy.dsproxy")
        .countQuery().build();
  }

  private Properties jpaProperties() {
    Properties properties = new Properties();
    properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    properties.put("hibernate.hbm2ddl.auto", "update");
    return properties;
  }

}

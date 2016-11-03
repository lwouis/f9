package com.lwouis.f9;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lwouis.f9.injection.InjectLogger;
import com.lwouis.f9.models.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Component
public class AppState {

  private final EntityManager entityManager;

  @InjectLogger
  private Logger logger;

  private final ObservableList<Item> observableItemList;

  @Inject
  public AppState(EntityManager entityManager) {
    this.entityManager = entityManager;
    //noinspection ConstantConditions
    observableItemList = FXCollections.observableList(loadListFromDiskOrCreateOne());
  }

  @Transactional
  private List<Item> loadListFromDiskOrCreateOne() {
    try {
      CriteriaQuery<Item> query = entityManager.getCriteriaBuilder().createQuery(Item.class);
      query = query.select(query.from(Item.class));
      List<Item> loadedAppState = entityManager.createQuery(query).getResultList();
      if (loadedAppState.isEmpty()) {
        return new ArrayList<>();
      }
      return loadedAppState;
    }
    catch (Throwable t) {
      logger.error("Failed to load app state from disk.", t);
    }
    return null;
  }

  public ObservableList<Item> getObservableItemList() {
    return observableItemList;
  }

  @Transactional
  public void addItems(List<Item> itemList) {
    entityManager.getTransaction().begin();
    for (Item item : itemList) {
      entityManager.persist(item);
    }
    entityManager.flush();
    entityManager.getTransaction().commit();
  }

  @Transactional
  public void removeItems(List<Item> itemList) {
    entityManager.getTransaction().begin();
    for (Item item : itemList) {
      entityManager.remove(item);
    }
    entityManager.flush();
    entityManager.getTransaction().commit();
  }
}

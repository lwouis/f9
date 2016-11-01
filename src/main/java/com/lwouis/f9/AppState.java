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
import com.lwouis.f9.models.ItemList;
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
      CriteriaQuery<ItemList> query = entityManager.getCriteriaBuilder().createQuery(ItemList.class);
      query = query.select(query.from(ItemList.class));
      List<ItemList> loadedAppState = entityManager.createQuery(query).getResultList();
      ItemList itemList;
      if (loadedAppState.isEmpty()) {
        itemList = new ItemList();
        itemList.setItemList(new ArrayList<>());
        entityManager.persist(itemList);
        return itemList.getItemList();
      }
      itemList = loadedAppState.get(0);
      return itemList.getItemList();
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
  public void persist() {
    entityManager.getTransaction().begin();
    entityManager.flush();
    // nothing is needed here as the entityManager automatically flushes
    entityManager.getTransaction().commit();
  }
}

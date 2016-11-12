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

@Component
public class PersistenceManager {

  private final EntityManager entityManager;

  @InjectLogger
  private Logger logger;

  @Inject
  public PersistenceManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Transactional
  public List<Item> loadListFromDiskOrCreateOne() {
    try {
      CriteriaQuery<Item> query = entityManager.getCriteriaBuilder().createQuery(Item.class);
      query = query.select(query.from(Item.class));
      List<Item> itemList = entityManager.createQuery(query).getResultList();
      if (itemList.isEmpty()) {
        return new ArrayList<>();
      }
      return itemList;
    }
    catch (Throwable t) {
      logger.error("Failed to load app state from disk.", t);
    }
    return null;
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

  @Transactional
  public void persist() {
    entityManager.getTransaction().begin();
    entityManager.flush();
    entityManager.getTransaction().commit();
  }
}

package com.lwouis.falcon9;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import org.slf4j.Logger;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.lwouis.falcon9.injection.InjectLogger;
import com.lwouis.falcon9.models.Item;
import com.lwouis.falcon9.models.ItemList;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

@Singleton
public class AppState implements ListChangeListener<Item> {

  private final Provider<EntityManager> entityManager;

  @InjectLogger
  private Logger logger;

  private ObservableList<Item> observableItemList;

  private ItemList itemList;

  private final Service<Void> service = new Service<Void>() {
    @Override
    protected Task<Void> createTask() {
      return new Task<Void>() {
        @Override
        protected Void call() {
          try {
//            ArrayList<Item> copy = oneLevelDeepCopy(observableItemList);
//            String json = serializeToJson(copy);
//            Files.createDirectories(jsonFile.toPath().getParent());
//            FileUtils.writeStringToFile(jsonFile, json, StandardCharsets.UTF_8, false);
          }
          catch (Throwable t) {
            logger.error("Failed to save appState to disk.", t);
          }
          return null;
        }
      };
    }
  };

  @Inject
  public AppState(Provider<EntityManager> entityManager) {
    this.entityManager = entityManager;
    observableItemList = FXCollections.observableList(loadListFromDiskOrCreateOne());
    observableItemList.addListener(this);
  }

  @Transactional
  private List<Item> loadListFromDiskOrCreateOne() {
    try {
      CriteriaQuery<ItemList> query = entityManager.get().getCriteriaBuilder().createQuery(ItemList.class);
      CriteriaQuery<ItemList> q = query.select(query.from(ItemList.class));
      List<ItemList> loadedAppState = entityManager.get().createQuery(q).getResultList();
      if (loadedAppState.isEmpty()) {
        EntityManager entityManager = this.entityManager.get();
        itemList = new ItemList();
        itemList.setItemList(new ArrayList<>());
        entityManager.persist(itemList);
        return itemList.getItemList();
      }
      itemList = loadedAppState.get(0);
      return itemList.getItemList();
    }
    catch (Throwable t) {
      logger.error("Failed to load appState from disk.", t);
    }
    return null;
  }

  @Transactional
  @Override
  public void onChanged(Change<? extends Item> c) {
    //Platform.runLater(service::restart);
    EntityManager entityManager = this.entityManager.get();
    entityManager.flush();
  }

  public ObservableList<Item> getObservableItemList() {
    return observableItemList;
  }
}

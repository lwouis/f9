package com.lwouis.f9.models;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity(name = "ItemList")
public class ItemList {

  @Id
  @GeneratedValue
  private Integer id;

  @OneToMany(mappedBy = "itemList",cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Item> itemList;

  public ItemList() {
  }

  public List<Item> getItemList() {
    return itemList;
  }

  public void setItemList(List<Item> itemList) {
    this.itemList = itemList;
  }
}

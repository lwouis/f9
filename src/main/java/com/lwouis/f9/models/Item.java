package com.lwouis.f9.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.TypeDef;

import com.lwouis.f9.persistence.JavafxImageUserType;
import javafx.scene.image.Image;

@TypeDef(defaultForType = Image.class, typeClass = JavafxImageUserType.class)
@Entity(name = "Item")
public class Item {

  @Id
  @GeneratedValue
  private Integer id;

  @Column(name = "name")
  private String name;

  @Column(name = "absolutePath")
  private String absolutePath;

  @Column(name = "image")
  private Image image;

  @ManyToOne
  private ItemList itemList;

  public Item() {
  }

  public Item(String name, String absolutePath, Image image) {
    this.name = name;
    this.absolutePath = absolutePath;
    this.image = image;
  }

  public Image getImage() {
    return image;
  }

  public String getName() {
    return name;
  }

  public String getAbsolutePath() {
    return absolutePath;
  }
}

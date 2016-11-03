package com.lwouis.f9.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.TypeDef;

import com.lwouis.f9.persistence.ObjectPropertyUserType;
import com.lwouis.f9.persistence.StringPropertyUserType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

@TypeDef(defaultForType = StringProperty.class, typeClass = StringPropertyUserType.class)
@TypeDef(defaultForType = ObjectProperty.class, typeClass = ObjectPropertyUserType.class)
@Entity(name = "Item")
public class Item {

  @Id
  @GeneratedValue
  private Integer id;

  @Column(name = "name")
  private StringProperty name;

  @Column(name = "absolutePath")
  private StringProperty absolutePath;

  @Column(name = "icon")
  private ObjectProperty<Image> icon;

  public Item() {
  }

  public Item(String name, String absolutePath, Image icon) {
    this.name = new SimpleStringProperty(name);
    this.absolutePath = new SimpleStringProperty(absolutePath);
    this.icon = new SimpleObjectProperty<>(icon);
  }

  public StringProperty nameProperty() {
    return name;
  }

  public ObjectProperty<Image> iconProperty() {
    return icon;
  }

  public StringProperty absolutePathProperty() {
    return absolutePath;
  }
}

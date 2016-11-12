package com.lwouis.f9.models;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.TypeDef;

import com.lwouis.f9.persistence.ImageUserType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

@TypeDef(defaultForType = Image.class, typeClass = ImageUserType.class)
@Entity(name = "Item")
@Access(AccessType.PROPERTY)
public class Item {

  private Integer id;

  private StringProperty name;

  private StringProperty path;

  private StringProperty arguments;

  private ObjectProperty<Image> icon;

  public Item() {
    this(null, null, null, null);
  }

  public Item(String name, String path, String arguments, Image icon) {
    this.name = new SimpleStringProperty(name);
    this.path = new SimpleStringProperty(path);
    this.arguments = new SimpleStringProperty(arguments);
    this.icon = new SimpleObjectProperty<>(icon);
  }

  public StringProperty nameProperty() {
    return name;
  }

  public ObjectProperty<Image> iconProperty() {
    return icon;
  }

  public StringProperty pathProperty() {
    return path;
  }

  @Column(name = "name")
  public String getName() {
    return name.get();
  }

  public void setName(String name) {
    this.name.set(name);
  }

  @Column(name = "path")
  public String getPath() {
    return path.get();
  }

  public void setPath(String path) {
    this.path.set(path);
  }

  @Column(name = "arguments")
  public String getArguments() {
    return arguments.get();
  }

  public StringProperty argumentsProperty() {
    return arguments;
  }

  public void setArguments(String arguments) {
    this.arguments.set(arguments);
  }

  @Column(name = "icon")
  public Image getIcon() {
    return iconProperty().get();
  }

  public void setIcon(Image icon) {
    this.icon.set(icon);
  }

  @Id
  @GeneratedValue
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }
}

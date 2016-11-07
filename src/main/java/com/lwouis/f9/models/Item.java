package com.lwouis.f9.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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

  @Column(name = "path")
  private StringProperty path;

  @Column(name = "arguments")
  private StringProperty arguments;

  @Column(name = "icon")
  private ObjectProperty<Image> icon;

  public Item() {
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
    // for some reason Hibernate returns null on database queries even though it should return SimpleObjectProperty
    // (null). The culprit code is on BasicExtractor.extract() where this check happens: value == null || rs.wasNull()
    if (icon == null) {
      return new SimpleObjectProperty<>(null);
    }
    return icon;
  }

  public StringProperty pathProperty() {
    return path;
  }

  public String getName() {
    return name.get();
  }

  public void setName(String name) {
    this.name.set(name);
  }

  public String getPath() {
    return path.get();
  }

  public void setPath(String path) {
    this.path.set(path);
  }

  public String getArguments() {
    return arguments.get();
  }

  public StringProperty argumentsProperty() {
    return arguments;
  }

  public void setArguments(String arguments) {
    this.arguments.set(arguments);
  }

  public Image getIcon() {
    return iconProperty().get();
  }

  public void setIcon(Image icon) {
    this.icon.set(icon);
  }
}

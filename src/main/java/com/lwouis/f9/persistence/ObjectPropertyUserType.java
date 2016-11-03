package com.lwouis.f9.persistence;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.BinaryTypeDescriptor;

import javafx.beans.property.ObjectProperty;

public class ObjectPropertyUserType extends AbstractSingleColumnStandardBasicType<ObjectProperty> {

  public ObjectPropertyUserType() {
    super(BinaryTypeDescriptor.INSTANCE, ObjectPropertyJavaTypeDescriptor.INSTANCE);
  }

  @Override
  public String getName() {
    return null;
  }
}

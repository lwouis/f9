package com.lwouis.f9.persistence;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

import javafx.beans.property.StringProperty;

public class StringPropertyUserType extends AbstractSingleColumnStandardBasicType<StringProperty> {

  public StringPropertyUserType() {
    super(VarcharTypeDescriptor.INSTANCE, StringPropertyJavaTypeDescriptor.INSTANCE);
  }

  @Override
  public String getName() {
    return null;
  }
}

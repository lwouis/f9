package com.lwouis.f9.persistence;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StringPropertyJavaTypeDescriptor extends AbstractTypeDescriptor<StringProperty> {

  public static final StringPropertyJavaTypeDescriptor INSTANCE = new StringPropertyJavaTypeDescriptor();

  private StringPropertyJavaTypeDescriptor() {
    super(StringProperty.class);
  }

  @Override
  public String toString(StringProperty value) {
    return null;
  }

  @Override
  public StringProperty fromString(String string) {
    return null;
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public <X> X unwrap(StringProperty value, Class<X> type, WrapperOptions options) {
    if (value == null) {
      return null;
    }
    return (X)value.get();
  }

  @Override
  public <X> StringProperty wrap(X value, WrapperOptions options) {
    if (value == null) {
      return null;
    }
    return new SimpleStringProperty((String)value);
  }

  @Override
  public boolean areEqual(StringProperty one, StringProperty another) {
    return one == another || one.get().equals(another.get());
  }
}

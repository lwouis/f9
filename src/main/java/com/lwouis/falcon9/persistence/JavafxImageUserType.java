package com.lwouis.falcon9.persistence;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.BinaryTypeDescriptor;

import javafx.scene.image.Image;

public class JavafxImageUserType extends AbstractSingleColumnStandardBasicType<Image> {

  public JavafxImageUserType() {
    super(BinaryTypeDescriptor.INSTANCE, JavafxImageJavaTypeDescriptor.INSTANCE);
  }

  @Override
  public String getName() {
    return null;
  }
}

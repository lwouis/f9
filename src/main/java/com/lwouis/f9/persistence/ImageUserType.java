package com.lwouis.f9.persistence;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.BinaryTypeDescriptor;

import javafx.scene.image.Image;

public class ImageUserType extends AbstractSingleColumnStandardBasicType<Image> {

  public ImageUserType() {
    super(BinaryTypeDescriptor.INSTANCE, ImageJavaTypeDescriptor.INSTANCE);
  }

  @Override
  public String getName() {
    return null;
  }
}

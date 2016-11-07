package com.lwouis.f9.persistence;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import javax.imageio.ImageIO;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ByteArrayTypeDescriptor;
import org.slf4j.Logger;

import com.lwouis.f9.injection.InjectLogger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class ObjectPropertyJavaTypeDescriptor extends AbstractTypeDescriptor<ObjectProperty> {

  @InjectLogger
  private Logger logger;

  public static final ObjectPropertyJavaTypeDescriptor INSTANCE = new ObjectPropertyJavaTypeDescriptor();

  private ObjectPropertyJavaTypeDescriptor() {
    super(ObjectProperty.class);
  }

  @Override
  public String toString(ObjectProperty value) {
    return null;
  }

  @Override
  public ObjectProperty fromString(String string) {
    return null;
  }

  @Override
  public <X> X unwrap(ObjectProperty image, Class<X> type, WrapperOptions options) {
    Byte[] bytes = ArrayUtils.toObject(getBytes((Image)image.getValue()));
    return ByteArrayTypeDescriptor.INSTANCE.unwrap(bytes, type, options);
  }

  @Override
  public <X> ObjectProperty wrap(X databaseValue, WrapperOptions options) {
    Byte[] bytes = ByteArrayTypeDescriptor.INSTANCE.wrap(databaseValue, options);
    return new SimpleObjectProperty<>(imageFromBytes(ArrayUtils.toPrimitive(bytes)));
  }

  @Override
  public boolean areEqual(ObjectProperty one, ObjectProperty another) {
    return one == another || Arrays.equals(getBytes((Image)one.getValue()), getBytes((Image)another.getValue()));
  }

  private Image imageFromBytes(byte[] bytes) {
    try {
      if (bytes == null) {
        return null;
      }
      ByteArrayInputStream in = new ByteArrayInputStream(bytes);
      BufferedImage read = ImageIO.read(in);
      return SwingFXUtils.toFXImage(read, null);
    }
    catch (Throwable t) {
      logger.error("Failed convert Image to byte[] for database persistence.", t);
    }
    return null;
  }

  private byte[] getBytes(Image image) {
    try {
      if (image == null) {
        return null;
      }
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", out);
      return out.toByteArray();
    }
    catch (Throwable t) {
      logger.error("Failed convert Image to byte[] for database persistence.", t);
    }
    return null;
  }
}

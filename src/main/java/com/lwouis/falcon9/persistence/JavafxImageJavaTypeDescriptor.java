package com.lwouis.falcon9.persistence;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import javax.imageio.ImageIO;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ByteArrayTypeDescriptor;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class JavafxImageJavaTypeDescriptor extends AbstractTypeDescriptor<Image> {

  public static final JavafxImageJavaTypeDescriptor INSTANCE = new JavafxImageJavaTypeDescriptor();

  public JavafxImageJavaTypeDescriptor() {
    super(Image.class);
  }

  @Override
  public String toString(Image value) {
    return null;
  }

  @Override
  public Image fromString(String string) {
    return null;
  }

  @Override
  public <X> X unwrap(Image value, Class<X> type, WrapperOptions options) {
    Byte[] bytes = ArrayUtils.toObject(getBytes(value));
    return ByteArrayTypeDescriptor.INSTANCE.unwrap(bytes, type, options);
  }

  @Override
  public <X> Image wrap(X value, WrapperOptions options) {
    Byte[] bytes = ByteArrayTypeDescriptor.INSTANCE.wrap(value, options);
    return imageFromBytes(ArrayUtils.toPrimitive(bytes));

  }

  @Override
  public boolean areEqual(Image one, Image another) {
    return one == another || Arrays.equals(getBytes(one), getBytes(another));
  }

  private Image imageFromBytes(byte[] bytes) {
    try {
      ByteArrayInputStream in = new ByteArrayInputStream(bytes);
      BufferedImage read = ImageIO.read(in);
      return SwingFXUtils.toFXImage(read, null);
    }
    catch (Throwable t) {
      //logger.error("Failed convert Image to byte[] for database persistence.", t);
    }
    return null;
  }

  private byte[] getBytes(Image image) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", out);
      return out.toByteArray();
    }
    catch (Throwable t) {
      //logger.error("Failed convert Image to byte[] for database persistence.", t);
    }
    return null;
  }
}

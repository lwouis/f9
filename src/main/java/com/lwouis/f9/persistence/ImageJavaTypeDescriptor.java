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
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class ImageJavaTypeDescriptor extends AbstractTypeDescriptor<Image> {

  @InjectLogger
  private Logger logger;

  public static final ImageJavaTypeDescriptor INSTANCE = new ImageJavaTypeDescriptor();

  private ImageJavaTypeDescriptor() {
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
  public <X> X unwrap(Image image, Class<X> type, WrapperOptions options) {
    Byte[] bytes = ArrayUtils.toObject(bytesFromImage(image));
    return ByteArrayTypeDescriptor.INSTANCE.unwrap(bytes, type, options);
  }

  @Override
  public <X> Image wrap(X databaseValue, WrapperOptions options) {
    Byte[] bytes = ByteArrayTypeDescriptor.INSTANCE.wrap(databaseValue, options);
    return imageFromBytes(ArrayUtils.toPrimitive(bytes));
  }

  @Override
  public boolean areEqual(Image one, Image another) {
    return one == another || Arrays.equals(bytesFromImage(one), bytesFromImage(another));
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

  private byte[] bytesFromImage(Image image) {
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

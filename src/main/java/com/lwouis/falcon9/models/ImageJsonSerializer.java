package com.lwouis.falcon9.models;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;
import javax.imageio.ImageIO;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class ImageJsonSerializer implements JsonSerializer<Image>, JsonDeserializer<Image> {
  @Override
  public JsonElement serialize(Image src, Type typeOfSrc, JsonSerializationContext context) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      ImageIO.write(SwingFXUtils.fromFXImage(src, null), "png", out);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return new JsonPrimitive(Base64.getEncoder().encodeToString(out.toByteArray()));
  }

  @Override
  public Image deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
          throws JsonParseException {
    WritableImage writableImage = null;
    try {
      byte[] bytes = Base64.getDecoder().decode(json.getAsString());
      BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
      writableImage = SwingFXUtils.toFXImage(image, null);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return writableImage;
  }
}

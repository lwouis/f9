package com.lwouis.falcon9;

import java.util.Arrays;
import java.util.Optional;

import org.controlsfx.control.textfield.CustomTextField;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import static org.testfx.api.FxAssert.verifyThat;

public class Falcon9Test extends ApplicationTest {

  @BeforeClass
  public static void setupHeadlessMode() {
    if (Boolean.getBoolean("headless")) {
      System.setProperty("testfx.robot", "glass");
      System.setProperty("testfx.headless", "true");
      System.setProperty("prism.order", "sw");
      System.setProperty("prism.text", "t2k");
      System.setProperty("java.awt.headless", "true");
      System.setProperty("testfx.setup.timeout", "2500");
    }
  }

  @Before
  public void setUpClass() throws Exception {
    ApplicationTest.launch(Main.class);
  }

  //  @After
  //  public void afterEachTest() throws TimeoutException {
  //    FxToolkit.hideStage();
  //    release(new KeyCode[] {});
  //    release(new MouseButton[]{});
  //  }

  @Override
  public void start(Stage stage) throws Exception {
    stage.show();
  }

  @Test
  public void typing_in_the_filter_text_field_works() {
    String testText = "Test";
    type(testText);
    verifyThat("#filterTextField", (CustomTextField filterTextField) -> filterTextField.getText().equals(testText));
  }

  private FxRobot type(final String text) {
    for (final char ch : text.toCharArray()) {
      if (Character.isAlphabetic(ch)) {
        final KeyCode keyCode = KeyCode.valueOf(Character.toString(ch).toUpperCase());

        if (Character.isUpperCase(ch)) {
          press(KeyCode.SHIFT).type(keyCode).release(KeyCode.SHIFT);
        } else {
          type(keyCode);
        }
      } else {
        Optional<KeyCode> optionalKeyCode = Arrays.stream(KeyCode.values())
                .filter(kc -> kc.impl_getCode() == ch).findAny();
        if (optionalKeyCode.isPresent()) {
          type(optionalKeyCode.get());
        } else {
          throw new IllegalArgumentException("Unsupported character: " + ch);
        }
      }
    }

    return type();
  }
}

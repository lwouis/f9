package com.lwouis.falcon9;

import org.controlsfx.control.textfield.CustomTextField;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.stage.Stage;

import static org.testfx.api.FxAssert.verifyThat;

public class Falcon9Test extends ApplicationTest {
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
    write(testText);
    verifyThat("#filterTextField", (CustomTextField filterTextField) -> filterTextField.getText().equals(testText));
  }
}

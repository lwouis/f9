package com.lwouis.f9;

import org.controlsfx.control.textfield.CustomTextField;
import org.junit.Before;
import org.junit.BeforeClass;
import org.testfx.framework.junit.ApplicationTest;

import javafx.stage.Stage;

import static org.testfx.api.FxAssert.verifyThat;

public class F9Test extends ApplicationTest {

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

  //TODO: fix UI tests on Travis
  //@Test
  public void typing_in_the_search_text_field_works() {
    String testText = "Test";
    write(testText);
    verifyThat("#searchTextField", (CustomTextField searchTextField) -> searchTextField.getText().equals(testText));
  }

}

package com.lwouis.f9;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lwouis.f9.models.Item;
import javafx.collections.FXCollections;
import javafx.scene.image.Image;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;

@RunWith(JMockit.class)
public class WindowsFileAnalyzerTest {

  @Tested
  private WindowsFileAnalyzer windowsFileAnalyzer;

  @Injectable
  private AppState appState;

  @Injectable
  private Logger logger = LoggerFactory.getLogger(getClass());

  private static Collection<File> files;

  private static Image testImage;

  @BeforeClass
  public static void loadTestFiles() throws IOException {
    File file = new File(ClassLoader.getSystemResource("sample_exe/").getFile());
    //File file = new File("C:/Program Files (x86)/");
    files = FileUtils.listFiles(file, new String[] {"exe"}, true);
    testImage = new Image("test_image.png");
  }

  //TODO: find a way to finish this test
  //@Test
  public void test_background_threads() throws URISyntaxException, InterruptedException, MalformedURLException {
    new Expectations(windowsFileAnalyzer) {{
      appState.getObservableItemList();
      result = FXCollections.observableArrayList();
      Deencapsulation.invoke(windowsFileAnalyzer, "getFileIcon", any);
      result = testImage;
    }};
    List<Item> items = windowsFileAnalyzer.itemsFromBackgroundThreadInspections(files);
    Thread.sleep(500);
    assertThat(items.get(0).iconProperty().get(), notNullValue());
  }
}

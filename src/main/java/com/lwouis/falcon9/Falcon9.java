package com.lwouis.falcon9;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Falcon9 extends Application {

//  private class GuiceModule extends AbstractModule {
//    @Override
//    protected void configure() {
//      bind(ItemListController.class);
//      bind(Falcon9.class);
//      bind(MainWindowController.class);
//      bind(MenuBarController.class);
//    }
//  }

  //private GuiceContext guiceContext = new GuiceContext(this, Collections::emptyList);

  //@Inject
  //private FXMLLoader fxmlLoader;

  @Override
  public void start(Stage primaryStage) throws Exception {
    //guiceContext.init();
    Parent root = FXMLLoader.load(ClassLoader.getSystemClassLoader()
            .getResource("com/lwouis/falcon9/components/main_window/mainWindow.fxml"));
    primaryStage.setTitle("Hello World");
    primaryStage.setScene(new Scene(root, 300, 275));
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}

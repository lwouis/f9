package com.lwouis.falcon9;

import com.melloware.jintellitype.HotkeyListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Falcon9 extends Application implements HotkeyListener {
  private Stage primaryStage;

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

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    //guiceContext.init();
    this.primaryStage = primaryStage;
    customCloseBehaviour(primaryStage);

    GlobalHotkeyManager.registerGlobalHotkey(this);
    DiskPersistanceManager.loadFromDisk();

    @SuppressWarnings("ConstantConditions")
    Parent root = FXMLLoader.load(ClassLoader.getSystemClassLoader()
            .getResource("com/lwouis/falcon9/components/main_window/mainWindow.fxml"));
    primaryStage.setTitle("Falcon9");
    primaryStage.setScene(new Scene(root));
    primaryStage.show();
    DiskPersistanceManager.startSaveToDiskListener();
  }

  private void customCloseBehaviour(Stage primaryStage) {
    // don't close the app when main window is hidden
    Platform.setImplicitExit(false);
    // close the app when the user requests it
    primaryStage.setOnCloseRequest(event -> Platform.exit());
  }

  @Override
  public void stop() {
    GlobalHotkeyManager.unregisterGlobalHotkey();
  }

  @Override
  public void onHotKey(int hotkeyId) {
    if (GlobalHotkeyManager.getHotkeyId() != hotkeyId) {
      return; // TODO Log error
    }
    if (primaryStage.isShowing()) {
      Platform.runLater(() -> primaryStage.hide());
    }
    else {
      Platform.runLater(() -> primaryStage.show());
    }
  }
}

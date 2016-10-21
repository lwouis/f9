package com.lwouis.falcon9.controllers;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.Collator;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.boris.pecoff4j.PE;
import org.boris.pecoff4j.ResourceDirectory;
import org.boris.pecoff4j.ResourceEntry;
import org.boris.pecoff4j.constant.ResourceType;
import org.boris.pecoff4j.io.PEParser;
import org.boris.pecoff4j.io.ResourceParser;
import org.boris.pecoff4j.resources.StringPair;
import org.boris.pecoff4j.resources.StringTable;
import org.boris.pecoff4j.resources.VersionInfo;
import org.boris.pecoff4j.util.ResourceHelper;
import org.stackoverflowusers.file.WindowsShortcut;

import com.lwouis.falcon9.AppState;
import com.lwouis.falcon9.StageManager;
import com.lwouis.falcon9.injection.InjectLogger;
import com.lwouis.falcon9.models.Launchable;
import javafx.collections.transformation.FilteredList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import sun.awt.shell.ShellFolder;

public class ItemListController implements Initializable {

  private final StageManager stageManager;

  @FXML
  private ListView<Launchable> launchableListView;

  @FXML
  private TextField filterTextField;

  private final AppState appState;

  private static final String NETWORK_FILE_PREFIX = "\\\\";

  private static final List<String> BUGGY_SHORTCUT_RESOLUTIONS = Arrays.asList(NETWORK_FILE_PREFIX, ".", "i");

  private static final KeyCharacterCombination ONLY_ENTER = new KeyCharacterCombination("\r");

  private static final KeyCharacterCombination ONLY_TAB = new KeyCharacterCombination("\t");

  private static final KeyCharacterCombination ONLY_DELETE = new KeyCharacterCombination("");

  private static final KeyCombination ONLY_UP = new KeyCodeCombination(KeyCode.UP);

  private static final KeyCombination ONLY_DOWN = new KeyCodeCombination(KeyCode.DOWN);

  private static double opacity = 1;

  @Inject
  public ItemListController(AppState appState, StageManager stageManager) {
    this.appState = appState;
    this.stageManager = stageManager;
  }

  @InjectLogger
  private Logger logger;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeFilterTextField();
    initializeLaunchableListView();
  }

  private void initializeFilterTextField() {
    filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      String filterText = filterTextField.getText();
      FilteredList<Launchable> launchableFilteredList = appState.getLaunchableFilteredList();
      MultipleSelectionModel<Launchable> selectionModel = launchableListView.getSelectionModel();
      if (filterText == null || filterText.length() == 0) {
        launchableFilteredList.setPredicate(s -> true);
      }
      else {
        String filterTextTrimmed = filterText.trim(); // ignore extra spaces on the sides
        launchableFilteredList.setPredicate(s -> StringUtils.containsIgnoreCase(s.getName(), filterTextTrimmed));
      }
      selectionModel.selectFirst();
    });
  }

  private void initializeLaunchableListView() {
    appState.getLaunchableSortedList().setComparator((o1, o2) -> {
      String text = filterTextField.getText();
      Collator coll = Collator.getInstance();
      coll.setStrength(Collator.PRIMARY);
      boolean o1StartsWithText = coll.compare(o1.getName().substring(0, text.length()), text) == 0;
      boolean o2StartsWithText = coll.compare(o2.getName().substring(0, text.length()), text) == 0;
      if ((o1StartsWithText && o2StartsWithText) || (!o1StartsWithText && !o2StartsWithText)) {
        return coll.compare(o1.getName(), o2.getName());
      }
      else if (o1StartsWithText) {
        return -1;
      }
      else {
        return 1;
      }
    });
    launchableListView.setItems(appState.getLaunchableSortedList());
    launchableListView.setCellFactory(lv -> new LaunchableCell());
    MultipleSelectionModel<Launchable> selectionModel = launchableListView.getSelectionModel();
    selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
    selectionModel.selectFirst();
  }

  @FXML
  public void removeSelected() {
    appState.getLaunchableObservableList().removeAll(launchableListView.getSelectionModel().getSelectedItems());
  }

  @FXML
  public void onMouseEvent(MouseEvent mouseEvent) {
    if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
      launchSelectedInternal();
    }
    mouseEvent.consume();
  }

  @FXML
  public void onKeyPressedOnFilterTextField(KeyEvent keyEvent) {
    MultipleSelectionModel<Launchable> selectionModel = launchableListView.getSelectionModel();
    if (ONLY_ENTER.match(keyEvent)) {
      launchSelectedInternal();
      keyEvent.consume();
    }
    else if (ONLY_UP.match(keyEvent) || ONLY_DOWN.match(keyEvent)) {
      if (ONLY_UP.match(keyEvent)) {
        int newIndex = selectionModel.getSelectedIndex() - 1;
        if (newIndex < 0) {
          newIndex = launchableListView.getItems().size() - 1;
        }
        selectionModel.clearAndSelect(newIndex);
      }
      else if (ONLY_DOWN.match(keyEvent)) {
        int newIndex = selectionModel.getSelectedIndex() + 1;
        if (newIndex > launchableListView.getItems().size() - 1) {
          newIndex = 0;
        }
        selectionModel.clearAndSelect(newIndex);
      }
      keyEvent.consume();
    }
  }

  @FXML
  public void onKeyPressedOnLaunchableListView(KeyEvent keyEvent) {
    if (ONLY_TAB.match(keyEvent)) {
      keyEvent.consume();
      filterTextField.requestFocus();
      filterTextField.end();
    }
  }

  @FXML
  public void onKeyTypedOnLaunchableListView(KeyEvent keyEvent) {
    String typedChar = keyEvent.getCharacter();
    if (ONLY_ENTER.getCharacter().equals(typedChar)) {
      launchSelectedInternal();
    }
    else if (ONLY_DELETE.getCharacter().equals(typedChar)) {
      removeSelected();
    }
    keyEvent.consume();
  }

  private void launchSelectedInternal() {
    for (Launchable launchable : launchableListView.getSelectionModel().getSelectedItems()) {
      File file = new File(launchable.getAbsolutePath());
      try {
        if (Desktop.isDesktopSupported()) {
          Desktop.getDesktop().open(file);
        }
        else {
          new ProcessBuilder(launchable.getAbsolutePath()).start();
        }
      }
      catch (IOException e) {
        logger.error("Failed opening the selected file.", e);
      }
      stageManager.hideStage();
    }
  }

  public void addFiles(List<File> files) {
    List<Launchable> launchableList = new ArrayList<>();
    for (File file : files) {
      File actualFile = resolveWindowsShortcut(file);
      launchableList
              .add(new Launchable(getProductName(actualFile), actualFile.getAbsolutePath(), getFileIcon(actualFile)));
    }
    appState.getLaunchableObservableList().addAll(launchableList);
  }

  private File resolveWindowsShortcut(File file) {
    try {
      if (WindowsShortcut.isPotentialValidLink(file)) {
        String realFilename = new WindowsShortcut(file).getRealFilename();
        if (BUGGY_SHORTCUT_RESOLUTIONS.contains(realFilename)) { // buggy .lnk shortcut
          return file;
        }
        return new File(realFilename);
      }
      else {
        return file;
      }
    }
    catch (IOException | ParseException e) {
      e.printStackTrace();
      return file;
    }
  }

  private String getProductName(File file) {
    try {
      if (file.getAbsolutePath().startsWith(NETWORK_FILE_PREFIX)) { // network files are too long to resolve
        return file.getName();
      }
      PE pe = PEParser.parse(file);
      ResourceDirectory rd = pe.getImageData().getResourceTable();
      ResourceEntry[] entries = ResourceHelper.findResources(rd, ResourceType.VERSION_INFO);
      if (entries.length == 0) {
        return file.getName();
      }
      VersionInfo versionInfo = ResourceParser.readVersionInfo(entries[0].getData());
      StringTable properties = versionInfo.getStringFileInfo().getTable(0);
      for (int i = 0; i < properties.getCount(); i++) {
        StringPair property = properties.getString(i);
        if (property.getKey().equals("ProductName")) {
          return property.getValue();
        }
      }
      return file.getName();
    }
    catch (Throwable t) {
      //t.printStackTrace();
      System.out.println(file.getName());
      return file.getName();
    }
  }

  private Image getFileIcon(File file) {
    try {
      if (file.getAbsolutePath().startsWith(NETWORK_FILE_PREFIX)) { // network files are too long to resolve
        return null;
      }
      java.awt.Image icon = ShellFolder.getShellFolder(file).getIcon(true); // true is 32x32, false if 16x16
      BufferedImage bufferedImage = new BufferedImage(icon.getWidth(null), icon.getHeight(null),
              BufferedImage.TYPE_INT_ARGB);
      Graphics2D bGr = bufferedImage.createGraphics();
      bGr.drawImage(icon, 0, 0, null);
      bGr.dispose();
      return SwingFXUtils.toFXImage(bufferedImage, null);
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public void toggleDragOverFeedback() {
    opacity = opacity == 1 ? 0.5 : 1;
    launchableListView.setOpacity(opacity);
  }
}

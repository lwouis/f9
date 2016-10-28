package com.lwouis.falcon9.controllers;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.Collator;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
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
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.stackoverflowusers.file.WindowsShortcut;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.lwouis.falcon9.AppState;
import com.lwouis.falcon9.FontAwesomeManager;
import com.lwouis.falcon9.StageManager;
import com.lwouis.falcon9.injection.InjectLogger;
import com.lwouis.falcon9.models.Item;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
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

  private final AppState appState;

  private final StageManager stageManager;

  private final FontAwesomeManager fontAwesomeManager;

  @FXML
  private ListView<Item> itemListView;

  @FXML
  private CustomTextField searchTextField;

  private static FilteredList<Item> itemFilteredList;

  private static final String NETWORK_FILE_PREFIX = "\\\\";

  private static final List<String> BUGGY_SHORTCUT_RESOLUTIONS = Arrays.asList(NETWORK_FILE_PREFIX, ".", "i");

  private static final KeyCharacterCombination ONLY_ENTER = new KeyCharacterCombination("\r");

  private static final KeyCharacterCombination ONLY_TAB = new KeyCharacterCombination("\t");

  private static final KeyCharacterCombination ONLY_DELETE = new KeyCharacterCombination("");

  private static final KeyCombination ONLY_UP = new KeyCodeCombination(KeyCode.UP);

  private static final KeyCombination ONLY_DOWN = new KeyCodeCombination(KeyCode.DOWN);

  private static double opacity = 1;

  @InjectLogger
  private Logger logger;

  private final Provider<EntityManager> entityManager;

  @Inject
  public ItemListController(AppState appState, StageManager stageManager, FontAwesomeManager fontAwesomeManager,
          Provider<EntityManager> entityManager) {
    this.appState = appState;
    this.stageManager = stageManager;
    this.fontAwesomeManager = fontAwesomeManager;
    this.entityManager = entityManager;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeItemListView();
    initializeSearchTextField();
  }

  private void initializeSearchTextField() {
    Glyph searchIcon = fontAwesomeManager.getGlyph(FontAwesome.Glyph.SEARCH);
    searchIcon.setTranslateX(35);
    searchIcon.setTranslateY(-1);
    searchTextField.setLeft(searchIcon);
    searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      String searchText = searchTextField.getText();
      MultipleSelectionModel<Item> selectionModel = itemListView.getSelectionModel();
      if (searchText == null || searchText.length() == 0) {
        itemFilteredList.setPredicate(s -> true);
      }
      else {
        String searchTextTrimmed = searchText.trim(); // ignore extra spaces on the sides
        itemFilteredList.setPredicate(s -> StringUtils.containsIgnoreCase(s.getName(), searchTextTrimmed));
      }
      selectionModel.selectFirst();
    });
  }

  private void initializeItemListView() {
    itemFilteredList = new FilteredList<>(appState.getItemObservableList());
    SortedList<Item> itemSortedList = new SortedList<>(itemFilteredList);
    itemSortedList.setComparator((o1, o2) -> {
      String text = searchTextField.getText();
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
    itemListView.setItems(itemSortedList);
    itemListView.setCellFactory(lv -> new ItemListCell());
    MultipleSelectionModel<Item> selectionModel = itemListView.getSelectionModel();
    selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
    selectionModel.selectFirst();
  }

  @FXML
  @Transactional
  public void removeSelected() {
    appState.getItemObservableList().removeAll(itemListView.getSelectionModel().getSelectedItems());
    EntityManager entityManager = this.entityManager.get();
    entityManager.persist(new AppState());
    entityManager.flush();
  }

  @FXML
  public void onMouseEvent(MouseEvent mouseEvent) {
    if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
      launchSelectedInternal();
    }
    mouseEvent.consume();
  }

  @FXML
  public void onKeyPressedOnSearchTextField(KeyEvent keyEvent) {
    MultipleSelectionModel<Item> selectionModel = itemListView.getSelectionModel();
    if (ONLY_ENTER.match(keyEvent)) {
      launchSelectedInternal();
      keyEvent.consume();
    }
    else if (ONLY_UP.match(keyEvent) || ONLY_DOWN.match(keyEvent)) {
      if (ONLY_UP.match(keyEvent)) {
        int newIndex = selectionModel.getSelectedIndex() - 1;
        if (newIndex < 0) {
          newIndex = itemListView.getItems().size() - 1;
        }
        selectionModel.clearAndSelect(newIndex);
      }
      else if (ONLY_DOWN.match(keyEvent)) {
        int newIndex = selectionModel.getSelectedIndex() + 1;
        if (newIndex > itemListView.getItems().size() - 1) {
          newIndex = 0;
        }
        selectionModel.clearAndSelect(newIndex);
      }
      keyEvent.consume();
    }
  }

  @FXML
  public void onKeyPressedOnItemListView(KeyEvent keyEvent) {
    if (ONLY_TAB.match(keyEvent)) {
      keyEvent.consume();
      searchTextField.requestFocus();
      searchTextField.end();
    }
  }

  @FXML
  public void onKeyTypedOnItemListView(KeyEvent keyEvent) {
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
    for (Item item : itemListView.getSelectionModel().getSelectedItems()) {
      File file = new File(item.getAbsolutePath());
      try {
        if (Desktop.isDesktopSupported()) {
          Desktop.getDesktop().open(file);
        }
        else {
          new ProcessBuilder(item.getAbsolutePath()).start();
        }
      }
      catch (Throwable t) {
        logger.error("Failed to open the selected item with user's prefered app.", t);
      }
      stageManager.hideStage();
    }
  }

  @Transactional
  public void addFiles(List<File> files) {
    List<Item> itemList = new ArrayList<>();
    for (File file : files) {
      File actualFile = resolveWindowsShortcut(file);
      itemList
              .add(new Item(getProductName(actualFile), actualFile.getAbsolutePath(), getFileIcon(actualFile)));
    }
    appState.getItemObservableList().addAll(itemList);
    EntityManager entityManager = this.entityManager.get();
    entityManager.persist(new AppState());
    entityManager.flush();
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
        logger.info("Failed to retrieve pretty file name for file: {}.", file.getAbsolutePath());
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
      logger.info("Failed to retrieve pretty file name for file: {}.", file.getAbsolutePath());
      return file.getName();
    }
    catch (Throwable t) {
      logger.info("Failed to retrieve pretty file name for file: {}.", file.getAbsolutePath(), t);
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
    catch (Throwable t) {
      logger.info("Failed to retrieve icon for file: {}.", file.getAbsolutePath());
      return null;
    }
  }

  public void toggleDragOverFeedback() {
    opacity = opacity == 1 ? 0.5 : 1;
    itemListView.setOpacity(opacity);
  }
}

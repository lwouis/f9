package com.lwouis.falcon9;

import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public interface Keyboard {
  KeyCharacterCombination ONLY_ENTER = new KeyCharacterCombination("\r");

  KeyCharacterCombination ONLY_TAB = new KeyCharacterCombination("\t");

  KeyCharacterCombination ONLY_DELETE = new KeyCharacterCombination("");

  KeyCombination ONLY_UP = new KeyCodeCombination(KeyCode.UP);

  KeyCombination ONLY_DOWN = new KeyCodeCombination(KeyCode.DOWN);
}

package com.lwouis.f9.nodes;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class SearchableTextFlow extends TextFlow {

  private StringProperty text = new SimpleStringProperty("");

  private StringProperty textToHighlight = new SimpleStringProperty("");

  public SearchableTextFlow() {
    text.addListener((obs, old, val) -> recomputeHighlight());
    textToHighlight.addListener((obs, old, val) -> recomputeHighlight());
  }

  private void recomputeHighlight() {
    getChildren().clear();
    if (textToHighlight.get().isEmpty()) {
      addInitialText();
    } else {
      addPartitionedText();
    }
  }

  private void addInitialText() {
    getChildren().add(new Text(text.get()));
  }

  private void addPartitionedText() {
    int matchStart = nextMatchPositionStartingAt(0);
    int matchLength = this.textToHighlight.get().length();
    int pos = 0;
    while (matchStart != -1) {
      addNonMatchingText(pos, matchStart);
      addMatchingText(matchStart);
      pos = matchStart + matchLength;
      matchStart = nextMatchPositionStartingAt(pos);

    }
    addNonMatchingText(pos, text.get().length());
  }

  private int nextMatchPositionStartingAt(int fromIndex) {
    return StringUtils.indexOfIgnoreCase(text.get(), textToHighlight.get(), fromIndex);
  }

  private void addNonMatchingText(int start, int end) {
    addStyledTextPart(start, end, "normal");
  }

  private void addMatchingText(int start) {
    int matchEnd = start + textToHighlight.get().length();
    addStyledTextPart(start, matchEnd, "bold");
  }

  private void addStyledTextPart(int start, int end, String fontWeight) {
    if (start < end) {
      getChildren().add(createTextWithWeight(text.get().substring(start, end), fontWeight));
    }
  }

  private static Text createTextWithWeight(String content, String fontWeight) {
    Text text = new Text(content);
    text.setStyle("-fx-font-weight: " + fontWeight);
    return text;
  }

  public String getText() {
    return text.get();
  }

  public StringProperty textProperty() {
    return text;
  }

  public void setText(String text) {
    this.text.set(text);
  }

  public String getTextToHighlight() {
    return textToHighlight.get();
  }

  public StringProperty textToHighlightProperty() {
    return textToHighlight;
  }

  public void setTextToHighlight(String textToHighlight) {
    this.textToHighlight.set(textToHighlight);
  }
}
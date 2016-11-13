package com.lwouis.f9.nodes;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
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
    Platform.runLater(() -> getChildren().clear());
    if (textToHighlight.get().isEmpty()) {
      addInitialText();
    }
    else {
      addPartitionedText();
    }
  }

  private void addInitialText() {
    Text text = new Text(this.text.get());
    Platform.runLater(() -> getChildren().add(text));
  }

  private void addPartitionedText() {
    List<Node> nodeList = new ArrayList<>();
    int matchStart = nextMatchPositionStartingAt(0);
    int matchLength = this.textToHighlight.get().length();
    int pos = 0;
    while (matchStart != -1) {
      Node nonMatchingText = nonMatchingText(pos, matchStart);
      if (nonMatchingText != null) {
        nodeList.add(nonMatchingText);
      }
      Node matchingText = matchingText(matchStart);
      if (matchingText != null) {
        nodeList.add(matchingText);
      }
      pos = matchStart + matchLength;
      matchStart = nextMatchPositionStartingAt(pos);
    }
    Node nonMatchingText = nonMatchingText(pos, text.get().length());
    if (nonMatchingText != null) {
      nodeList.add(nonMatchingText);
    }
    updateUi(nodeList);
  }

  private void updateUi(List<Node> nodeList) {
    Platform.runLater(() -> getChildren().addAll(nodeList));
  }

  private int nextMatchPositionStartingAt(int fromIndex) {
    return StringUtils.indexOfIgnoreCase(text.get(), textToHighlight.get(), fromIndex);
  }

  private Node nonMatchingText(int start, int end) {
    return styledTextPart(start, end, "normal");
  }

  private Node matchingText(int start) {
    int matchEnd = start + textToHighlight.get().length();
    return styledTextPart(start, matchEnd, "bold");
  }

  private Node styledTextPart(int start, int end, String fontWeight) {
    if (start < end) {
      return createTextWithWeight(text.get().substring(start, end), fontWeight);
    }
    return null;
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
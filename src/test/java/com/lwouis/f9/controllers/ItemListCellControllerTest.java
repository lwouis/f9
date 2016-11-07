package com.lwouis.f9.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.lwouis.f9.nodes.SearchableTextFlow;
import de.saxsys.javafx.test.JfxRunner;
import javafx.scene.text.TextFlow;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@RunWith(JfxRunner.class)
public class ItemListCellControllerTest {

  private final TextFlow textFlow = new TextFlow();

  private SearchableTextFlow searchableTextFlow = new SearchableTextFlow();

  @Before
  public void clearTextFlowChildren() {
    searchableTextFlow.getChildren().clear();
  }

  private void initSearchableTextFlow(String b, String baka) {
    searchableTextFlow.textToHighlightProperty().set(b);
    searchableTextFlow.textProperty().set(baka);
  }

  @Test
  public void one_match() {
    initSearchableTextFlow("b", "baka");
    assertThat(searchableTextFlow.getChildren(), hasSize(2));
  }

  @Test
  public void two_matches() {
    initSearchableTextFlow("b", "baba");
    assertThat(searchableTextFlow.getChildren(), hasSize(4));
  }

  @Test
  public void two_matches_many_letters() {
    initSearchableTextFlow("b", "BaaaaaBaaaaa");
    assertThat(searchableTextFlow.getChildren(), hasSize(4));
  }

  @Test
  public void two_matches_different_case() {
    initSearchableTextFlow("b", "aBaBa");
    assertThat(searchableTextFlow.getChildren(), hasSize(5));
  }

  @Test
  public void two_matches_mixed_case() {
    initSearchableTextFlow("b", "BabaBa");
    assertThat(searchableTextFlow.getChildren(), hasSize(6));
  }

  //TODO: introduce regional/fuzzy matches, and activate this test
  public void two_localized_matches() {
    initSearchableTextFlow("éclipse", "eclipse");
    assertThat(textFlow.getChildren(), hasSize(4));
  }

  //TODO: introduce regional/fuzzy matches, and activate this test
  public void one_missing_char() {
    initSearchableTextFlow("firefo", "firefox.exe");
    assertThat(textFlow.getChildren(), hasSize(2));
  }

  //TODO: introduce regional/fuzzy matches, and activate this test
  public void one_permutatedchar() {
    initSearchableTextFlow("firefow", "firefox.exe");
    assertThat(textFlow.getChildren(), hasSize(2));
  }
}


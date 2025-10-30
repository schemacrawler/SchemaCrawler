/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.CollectionsUtility;

public class CollectionsUtilityTest {

  @Test
  public void equal() {
    // Zero length
    assertThat(
        CollectionsUtility.compareLists(new ArrayList<String>(), new ArrayList<String>()), is(0));

    // Same length and values
    assertThat(CollectionsUtility.compareLists(List.of("hello"), List.of("hello")), is(0));
  }

  @Test
  public void listStartsWithTest() {

    assertThat(CollectionsUtility.listStartsWith(null, new ArrayList<>()), is(false));
    assertThat(CollectionsUtility.listStartsWith(new ArrayList<>(), null), is(false));
    assertThat(CollectionsUtility.listStartsWith(null, null), is(false));

    assertThat(CollectionsUtility.listStartsWith(new ArrayList<>(), new ArrayList<>()), is(true));
    assertThat(CollectionsUtility.listStartsWith(new ArrayList<>(), List.of("1", "2")), is(false));
    assertThat(CollectionsUtility.listStartsWith(List.of("1"), List.of("1", "2")), is(false));
    assertThat(CollectionsUtility.listStartsWith(List.of("1", "2"), List.of("1")), is(true));

    assertThat(CollectionsUtility.listStartsWith(List.of("3", "4"), List.of("1")), is(false));
  }

  @Test
  public void nullArgs() {
    assertThat(CollectionsUtility.compareLists(null, null), is(0));
    assertThat(CollectionsUtility.compareLists(null, new ArrayList<String>()), is(lessThan(0)));
    assertThat(CollectionsUtility.compareLists(new ArrayList<String>(), null), is(greaterThan(0)));
  }

  @Test
  public void unequal() {
    // Different lengths
    assertThat(
        CollectionsUtility.compareLists(List.of("hello"), new ArrayList<String>()),
        is(greaterThan(0)));
    assertThat(
        CollectionsUtility.compareLists(new ArrayList<String>(), List.of("hello")),
        is(lessThan(0)));

    // Same length different values
    assertThat(
        CollectionsUtility.compareLists(List.of("zorro"), List.of("hello")), is(greaterThan(0)));
    assertThat(
        CollectionsUtility.compareLists(List.of("hello"), List.of("zorro")), is(lessThan(0)));
  }

  @Test
  @DisplayName("Split should return empty set for edge conditions")
  void shouldReturnEmptySetWhenExcludeToolsUnsetOrEmpty() {
    // Arrange and act
    final Collection<String> excluded1 = CollectionsUtility.setOfStrings(null);
    // Assert
    assertThat(excluded1.size(), is(0));

    // Arrange and act
    final Collection<String> excluded2 = CollectionsUtility.setOfStrings("");
    // Assert
    assertThat(excluded2.size(), is(0));

    // Arrange and act
    // Only commas and spaces
    final Collection<String> excluded3 = CollectionsUtility.setOfStrings(", , ,  ,");
    // Assert
    assertThat(excluded3.size(), is(0));
  }

  @Test
  @DisplayName("Split should handle blanks, spaces, and duplicates")
  void splitShouldHandleBlanksSpacesAndDuplicatesInExcludeTools() {
    // Arrange and act
    final Collection<String> excluded =
        CollectionsUtility.setOfStrings("  toolA , , toolB,toolA ,  ,ToolA  ");

    // Assert
    // "toolA" appears twice but should be included once due to Set semantics
    assertThat(excluded.contains("toolA"), is(true));
    assertThat(excluded.contains("toolB"), is(true));
    // "ToolA" (different case) is a different entry because matching is
    // case-sensitive
    assertThat(excluded.contains("ToolA"), is(true));
    assertThat(excluded.size(), is(3));
  }

  @Test
  @DisplayName("Split should parse comma-separated list")
  void splitShouldParseExcludeToolsList() {
    // Arrange and act
    final Collection<String> excluded = CollectionsUtility.setOfStrings("tool1,tool2,tool3");

    // Assert
    assertThat(excluded.contains("tool1"), is(true));
    assertThat(excluded.contains("tool2"), is(true));
    assertThat(excluded.contains("tool3"), is(true));
    assertThat(excluded.size(), is(3));
  }
}

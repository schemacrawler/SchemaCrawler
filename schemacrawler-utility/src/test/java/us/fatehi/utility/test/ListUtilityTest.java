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
import java.util.List;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.ListUtility;

public class ListUtilityTest {

  @Test
  public void equal() {
    // Zero length
    assertThat(ListUtility.compareLists(new ArrayList<String>(), new ArrayList<String>()), is(0));

    // Same length and values
    assertThat(ListUtility.compareLists(List.of("hello"), List.of("hello")), is(0));
  }

  @Test
  public void listStartsWithTest() {

    assertThat(ListUtility.listStartsWith(null, new ArrayList<>()), is(false));
    assertThat(ListUtility.listStartsWith(new ArrayList<>(), null), is(false));
    assertThat(ListUtility.listStartsWith(null, null), is(false));

    assertThat(ListUtility.listStartsWith(new ArrayList<>(), new ArrayList<>()), is(true));
    assertThat(ListUtility.listStartsWith(new ArrayList<>(), List.of("1", "2")), is(false));
    assertThat(ListUtility.listStartsWith(List.of("1"), List.of("1", "2")), is(false));
    assertThat(ListUtility.listStartsWith(List.of("1", "2"), List.of("1")), is(true));

    assertThat(ListUtility.listStartsWith(List.of("3", "4"), List.of("1")), is(false));
  }

  @Test
  public void nullArgs() {
    assertThat(ListUtility.compareLists(null, null), is(0));
    assertThat(ListUtility.compareLists(null, new ArrayList<String>()), is(lessThan(0)));
    assertThat(ListUtility.compareLists(new ArrayList<String>(), null), is(greaterThan(0)));
  }

  @Test
  public void unequal() {
    // Different lengths
    assertThat(
        ListUtility.compareLists(List.of("hello"), new ArrayList<String>()), is(greaterThan(0)));
    assertThat(
        ListUtility.compareLists(new ArrayList<String>(), List.of("hello")), is(lessThan(0)));

    // Same length different values
    assertThat(ListUtility.compareLists(List.of("zorro"), List.of("hello")), is(greaterThan(0)));
    assertThat(ListUtility.compareLists(List.of("hello"), List.of("zorro")), is(lessThan(0)));
  }
}

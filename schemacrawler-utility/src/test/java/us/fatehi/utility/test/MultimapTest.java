/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package us.fatehi.utility.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import org.junit.jupiter.api.Test;

import us.fatehi.utility.Multimap;

public class MultimapTest {

  @Test
  public void add() {
    final Multimap<String, Integer> multimap = new Multimap<>();
    multimap.add("foo", 1);
    multimap.add("bar", 2);
    multimap.add("foo", 3);
    assertThat(multimap.get("foo"), containsInAnyOrder(1, 3));
    assertThat(multimap.get("bar"), containsInAnyOrder(2));
  }
}

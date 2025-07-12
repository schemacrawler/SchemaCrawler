/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.crawl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.comparesEqualTo;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ServerInfoPropertyTest {

  @Test
  public void compare() {
    final ImmutableServerInfoProperty serverInfoProperty =
        new ImmutableServerInfoProperty("name", null, null);
    final ImmutableServerInfoProperty serverInfoProperty1 =
        new ImmutableServerInfoProperty("name1", "value1", "desc");

    assertThat(serverInfoProperty, lessThan(null));
    assertThat(serverInfoProperty1, lessThan(null));

    assertThat(serverInfoProperty, comparesEqualTo(new ImmutableDatabaseProperty("name", null)));
    assertThat(serverInfoProperty, comparesEqualTo(new ImmutableDatabaseProperty("NAME", null)));

    assertThat(serverInfoProperty, lessThan(serverInfoProperty1));
    assertThat(serverInfoProperty1, greaterThan(serverInfoProperty));
  }

  @Test
  public void nullArgumentsConstructor() {

    assertThrows(
        IllegalArgumentException.class, () -> new ImmutableServerInfoProperty(null, "", ""));
  }

  @Test
  public void properties() {

    final ImmutableServerInfoProperty serverInfoProperty =
        new ImmutableServerInfoProperty("name", null, null);

    assertThat(serverInfoProperty.getName(), is("name"));
    assertThat(serverInfoProperty.getValue(), nullValue());
    assertThat(serverInfoProperty.getDescription(), is(""));
    assertThat(serverInfoProperty.toString(), is("name = null"));
  }

  @Test
  public void properties2() {

    final ImmutableServerInfoProperty serverInfoProperty =
        new ImmutableServerInfoProperty("name1", "value1", "desc");

    assertThat(serverInfoProperty.getName(), is("name1"));
    assertThat(serverInfoProperty.getValue(), is("value1"));
    assertThat(serverInfoProperty.getDescription(), is("desc"));
    assertThat(serverInfoProperty.toString(), is("name1 = value1"));
  }
}

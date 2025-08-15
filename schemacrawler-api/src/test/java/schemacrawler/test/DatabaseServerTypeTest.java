/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.comparesEqualTo;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.datasource.DatabaseServerType;

public class DatabaseServerTypeTest {

  @Test
  public void badConstructorArguments() {
    assertThrows(IllegalArgumentException.class, () -> new DatabaseServerType(null, "NewDB"));
    assertThrows(IllegalArgumentException.class, () -> new DatabaseServerType(" ", "NewDB"));
    assertThrows(IllegalArgumentException.class, () -> new DatabaseServerType("newdb", null));
    assertThrows(IllegalArgumentException.class, () -> new DatabaseServerType("newdb", ""));
  }

  @Test
  public void compareTo() {
    final DatabaseServerType databaseServerType0 = DatabaseServerType.UNKNOWN;
    final DatabaseServerType databaseServerType1 = new DatabaseServerType("newdb", "NewDB");

    assertThat(databaseServerType0, lessThan(null));
    assertThat(databaseServerType1, lessThan(null));

    assertThat(databaseServerType0, comparesEqualTo(databaseServerType0));
    assertThat(databaseServerType1, comparesEqualTo(databaseServerType1));

    assertThat(databaseServerType0, lessThan(databaseServerType1));
    assertThat(databaseServerType1, greaterThan(databaseServerType0));
  }

  @Test
  public void equals() {
    EqualsVerifier.forClass(DatabaseServerType.class)
        .withIgnoredFields("databaseSystemName")
        .verify();
  }

  @Test
  public void properties0() {
    final DatabaseServerType databaseServerType = DatabaseServerType.UNKNOWN;
    assertThat(databaseServerType.getDatabaseSystemIdentifier(), is(nullValue()));
    assertThat(databaseServerType.getDatabaseSystemIdentifier(), is(nullValue()));
    assertThat(databaseServerType.isUnknownDatabaseSystem(), is(true));
    assertThat(databaseServerType.hashCode(), is(31));
  }

  @Test
  public void properties1() {
    final DatabaseServerType databaseServerType = new DatabaseServerType("newdb", "NewDB");
    assertThat(databaseServerType.getDatabaseSystemIdentifier(), is("newdb"));
    assertThat(databaseServerType.getDatabaseSystemIdentifier(), is("newdb"));
    assertThat(databaseServerType.isUnknownDatabaseSystem(), is(false));
  }

  @Test
  public void string() {
    final DatabaseServerType databaseServerType0 = DatabaseServerType.UNKNOWN;
    final DatabaseServerType databaseServerType1 = new DatabaseServerType("newdb", "NewDB");

    assertThat(databaseServerType0.toString(), is(""));
    assertThat(databaseServerType1.toString(), is("newdb - NewDB"));
  }
}

/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.commandline.schemaspy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

public class DatabaseTypeTest {

  @Test
  public void resolvesKnownType() {
    final DatabaseType databaseType = DatabaseType.fromType("pgsql").orElseThrow();
    assertThat(databaseType.getSchemaCrawlerServer().orElseThrow(), is("postgresql"));
  }

  @Test
  public void supportsDatabaseTypesListing() {
    final String supportedDatabaseTypes = DatabaseType.supportedDatabaseTypes();
    assertThat(supportedDatabaseTypes.contains("pgsql"), is(true));
  }

  @Test
  public void rejectsUnknownType() {
    assertThat(DatabaseType.fromType("not-real").isPresent(), is(false));
  }
}

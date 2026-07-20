/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.commandline.schemaspy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

public class SchemaSpyDatabaseTypeTest {

  @Test
  public void mapsKnownTypeToSchemaCrawlerServer() {
    final SchemaSpyDatabaseType databaseType =
        SchemaSpyDatabaseType.fromType("pgsql").orElseThrow();
    assertThat(databaseType.getSchemaCrawlerServer().orElseThrow(), is("postgresql"));
    assertThat(databaseType.isUrlFallback(), is(false));
  }

  @Test
  public void supportedTypeListingContainsCanonicalValues() {
    final String supportedDatabaseTypes = SchemaSpyDatabaseType.supportedDatabaseTypes();
    assertThat(supportedDatabaseTypes, containsString("pgsql"));
    assertThat(supportedDatabaseTypes, containsString("snowflake"));
    assertThat(supportedDatabaseTypes, containsString("ora"));
  }

  @Test
  public void unknownTypeIsNotMapped() {
    assertThat(SchemaSpyDatabaseType.fromType("not-real").isPresent(), is(false));
  }
}

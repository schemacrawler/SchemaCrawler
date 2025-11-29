/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;

import java.sql.Connection;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.WithTestDatabase;

@WithTestDatabase
public class SerializationTest {

  @Test
  public void catalogSerialization(final Connection connection) throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    final Catalog catalog = getCatalog(connection, schemaCrawlerOptions);
    assertThat("Could not obtain catalog", catalog, notNullValue());
    assertThat("Could not find any schemas", catalog.getSchemas(), is(not(empty())));

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertThat("Could not obtain schema", schema, notNullValue());
    assertThat("Unexpected number of tables in the schema", catalog.getTables(schema), hasSize(11));

    final Catalog clonedCatalog = SerializationUtils.clone(catalog);

    assertThat(catalog, equalTo(clonedCatalog));

    assertThat("Could not obtain catalog", clonedCatalog, notNullValue());
    assertThat("Could not find any schemas", clonedCatalog.getSchemas(), is(not(empty())));

    final Schema clonedSchema = clonedCatalog.lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertThat("Could not obtain schema", clonedSchema, notNullValue());
    assertThat(
        "Unexpected number of tables in the schema",
        clonedCatalog.getTables(clonedSchema),
        hasSize(11));
  }
}

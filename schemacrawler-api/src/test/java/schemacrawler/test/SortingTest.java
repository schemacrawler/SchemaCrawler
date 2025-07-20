/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import java.sql.Connection;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.utility.NamedObjectSort;

@WithTestDatabase
public class SortingTest {

  @Test
  public void columnSort(final Connection connection) throws Exception {

    final String[] sortedNatural = {
      "ID",
      "FIRSTNAME",
      "LASTNAME",
      "ADDRESS1",
      "ADDRESS2",
      "CITY",
      "STATE",
      "POSTALCODE",
      "COUNTRY",
    };

    final String[] sortedAlpha = Arrays.copyOf(sortedNatural, sortedNatural.length);
    Arrays.sort(sortedAlpha);

    checkColumnSort(connection, "AUTHORS", sortedAlpha, true);
    checkColumnSort(connection, "AUTHORS", sortedNatural, false);
  }

  @Test
  public void fkSort(final Connection connection) throws Exception {

    final String[] sortedNatural = {
      "Z_FK_AUTHOR", "SYS_FK_10118",
    };

    final String[] sortedAlpha = Arrays.copyOf(sortedNatural, sortedNatural.length);
    Arrays.sort(sortedAlpha);

    checkFkSort(connection, "BOOKAUTHORS", sortedAlpha, true);
    checkFkSort(connection, "BOOKAUTHORS", sortedNatural, false);
  }

  @Test
  public void indexSort(final Connection connection) throws Exception {

    final String[] sortedNatural = {
      "PK_AUTHORS", "IDX_B_AUTHORS", "IDX_A_AUTHORS",
    };
    final String[] sortedAlpha = Arrays.copyOf(sortedNatural, sortedNatural.length);
    Arrays.sort(sortedAlpha);

    checkIndexSort(connection, "AUTHORS", sortedAlpha, true);
    checkIndexSort(connection, "AUTHORS", sortedNatural, false);
  }

  @SuppressWarnings("boxing")
  private void checkColumnSort(
      final Connection connection,
      final String tableName,
      final String[] expectedValues,
      final boolean sortAlphabetically)
      throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final Catalog catalog = getCatalog(connection, schemaCrawlerOptions);
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    assertThat("Schema not found", schema, notNullValue());

    final Table table = catalog.lookupTable(schema, tableName).orElse(null);
    assertThat("Table " + tableName + " not found", table, notNullValue());
    if (table.getName().equals(tableName)) {
      final Column[] columns = table.getColumns().toArray(new Column[0]);
      Arrays.sort(columns, NamedObjectSort.getNamedObjectSort(sortAlphabetically));
      assertThat("Column count does not match", expectedValues.length, equalTo(columns.length));
      for (int i = 0; i < columns.length; i++) {
        final Column column = columns[i];
        assertThat(
            "Columns not " + (sortAlphabetically ? "alphabetically" : "naturally") + " sorted",
            expectedValues[i],
            equalTo(column.getName()));
      }
    }
  }

  @SuppressWarnings("boxing")
  private void checkFkSort(
      final Connection connection,
      final String tableName,
      final String[] expectedValues,
      final boolean sortAlphabetically)
      throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final Catalog catalog = getCatalog(connection, schemaCrawlerOptions);
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    assertThat("Schema not found", schema, notNullValue());

    final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
    assertThat("Table count does not match", tables, arrayWithSize(11));
    for (final Table table : tables) {
      if (table.getName().equals(tableName)) {
        final ForeignKey[] foreignKeys = table.getForeignKeys().toArray(new ForeignKey[0]);
        Arrays.sort(foreignKeys, NamedObjectSort.getNamedObjectSort(sortAlphabetically));
        assertThat(
            "Foreign key count does not match", expectedValues.length, equalTo(foreignKeys.length));
        for (int i = 0; i < foreignKeys.length; i++) {
          final ForeignKey foreignKey = foreignKeys[i];
          assertThat(
              "Foreign keys not "
                  + (sortAlphabetically ? "alphabetically" : "naturally")
                  + " sorted",
              expectedValues[i],
              equalTo(foreignKey.getName()));
        }
      }
    }
  }

  private void checkIndexSort(
      final Connection connection,
      final String tableName,
      final String[] expectedValues,
      final boolean sortAlphabetically)
      throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final Catalog catalog = getCatalog(connection, schemaCrawlerOptions);
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
    assertThat("Table count does not match", tables, arrayWithSize(11));
    for (final Table table : tables) {
      if (table.getName().equals(tableName)) {
        final Index[] indexes = table.getIndexes().toArray(new Index[0]);
        Arrays.sort(indexes, NamedObjectSort.getNamedObjectSort(sortAlphabetically));
        assertThat(
            "Index count does not match for table " + table,
            expectedValues.length,
            equalTo(indexes.length));
        for (int i = 0; i < indexes.length; i++) {
          final Index index = indexes[i];
          assertThat(
              "Indexes not "
                  + (sortAlphabetically ? "alphabetically" : "naturally")
                  + " sorted  for table "
                  + table,
              expectedValues[i],
              equalTo(index.getName()));
        }
      }
    }
  }
}

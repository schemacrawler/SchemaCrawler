/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;
import static schemacrawler.model.implementation.MutablePrimaryKey.newAlternateKey;
import static us.fatehi.utility.Utility.requireNotBlank;

import schemacrawler.model.implementation.MutableColumn;
import schemacrawler.model.implementation.MutablePrimaryKey;
import schemacrawler.model.implementation.MutableTable;
import schemacrawler.model.implementation.MutableTableConstraintColumn;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

public final class AlternateKeyBuilder {

  public static final class AlternateKeyDefinition {

    private final Schema schema;
    private final String tableName;
    private final String alternateKeyName;
    private final List<String> columns;

    public AlternateKeyDefinition(
        final Schema schema,
        final String table,
        final String alternateKeyName,
        final List<String> columns) {
      this.schema = requireNonNull(schema, "No schema provided");
      tableName = requireNotBlank(table, "No table name provided");
      this.alternateKeyName = requireNotBlank(alternateKeyName, "No alternate key name provided");

      if (columns == null || columns.isEmpty()) {
        throw new IllegalArgumentException("No columns provided");
      }
      this.columns = new ArrayList<>(columns);
    }

    public String getAlternateKeyName() {
      return alternateKeyName;
    }

    public List<String> getColumns() {
      return columns;
    }

    public Schema getSchema() {
      return schema;
    }

    public String getTableName() {
      return tableName;
    }

    @Override
    public String toString() {
      return "alternate key <%s.%s.%s[%s]>".formatted(schema, tableName, alternateKeyName, columns);
    }
  }

  private static final Logger LOGGER = Logger.getLogger(AlternateKeyBuilder.class.getName());

  public static AlternateKeyBuilder builder(final Catalog catalog) {
    return new AlternateKeyBuilder(catalog);
  }

  private final Catalog catalog;

  private AlternateKeyBuilder(final Catalog catalog) {
    this.catalog = requireNonNull(catalog, "No catalog provided");
  }

  public Optional<PrimaryKey> addAlternateKey(final AlternateKeyDefinition alternateKeyDefinition) {
    requireNonNull(alternateKeyDefinition, "No alternate key provided");

    final MutableTable table;
    final Optional<Table> lookupTable =
        catalog.lookupTable(
            alternateKeyDefinition.getSchema(), alternateKeyDefinition.getTableName());
    if (lookupTable.isEmpty()) {
      LOGGER.log(Level.CONFIG, "Table not found, for " + alternateKeyDefinition);
      return Optional.empty();
    }
    table = (MutableTable) lookupTable.get();

    final MutablePrimaryKey alternateKey =
        newAlternateKey(table, alternateKeyDefinition.getAlternateKeyName());
    final List<String> columns = alternateKeyDefinition.getColumns();
    for (int i = 0; i < columns.size(); i++) {
      final String columnName = columns.get(i);
      final Column column;
      final Optional<MutableColumn> lookupColumn = table.lookupColumn(columnName);
      if (lookupColumn.isEmpty()) {
        LOGGER.log(
            Level.CONFIG,
            "Column <%s> not found, for %s".formatted(columnName, alternateKeyDefinition));
        return Optional.empty();
      }
      column = lookupColumn.get();

      final MutableTableConstraintColumn pkColumn =
          new MutableTableConstraintColumn(alternateKey, column);
      pkColumn.setKeyOrdinalPosition(i + 1);
      //
      alternateKey.addColumn(pkColumn);
    }

    table.addAlternateKey(alternateKey);

    return Optional.of(alternateKey);
  }
}

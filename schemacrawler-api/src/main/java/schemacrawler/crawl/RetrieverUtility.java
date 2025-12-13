/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import schemacrawler.model.implementation.ColumnPartial;
import schemacrawler.model.implementation.TablePartial;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
public final class RetrieverUtility {

  private static final Logger LOGGER = Logger.getLogger(RetrieverUtility.class.getName());

  static String constructForeignKeyName(final Table pkTable, final Table fkTable) {
    requireNonNull(pkTable, "No referenced table provided");
    requireNonNull(fkTable, "No dependent table provided");

    return "SCHCRWLR_%1$08X_%2$08X"
        .formatted(fkTable.getFullName().hashCode(), pkTable.getFullName().hashCode());
  }

  static Column lookupOrCreateColumn(
      final Catalog catalog, final Schema schema, final String tableName, final String columnName) {

    if (isBlank(columnName)) {
      return null;
    }

    requireNonNull(catalog, "No catalog provided");
    requireNonNull(schema, "No schema provided");

    Column column = null;
    final Optional<? extends Column> columnOptional =
        catalog.lookupColumn(schema, tableName, columnName);
    if (columnOptional.isPresent()) {
      column = columnOptional.get();
    }

    if (column == null) {
      // Create the table and column, but do not add it to the schema
      final TablePartial table = new TablePartial(schema, tableName);
      column = new ColumnPartial(table, columnName);
      table.addColumn(column);

      LOGGER.log(
          Level.FINER,
          new StringFormat("Creating partial column for a column reference <%s>", column));
    }
    return column;
  }

  /**
   * Looks up a column in the database. If the column and table are not found, they are created, and
   * added to the schema. This is prevent foreign key relationships from having a null pointer.
   */
  static Column lookupOrCreateColumn(
      final Catalog catalog,
      final String catalogName,
      final String schemaName,
      final String tableName,
      final String columnName) {
    return lookupOrCreateColumn(
        catalog, new SchemaReference(catalogName, schemaName), tableName, columnName);
  }

  private RetrieverUtility() {
    // Prevent instantiaton
  }
}

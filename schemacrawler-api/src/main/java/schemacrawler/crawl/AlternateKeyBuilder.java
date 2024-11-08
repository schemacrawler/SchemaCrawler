/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;
import static schemacrawler.crawl.MutablePrimaryKey.newAlternateKey;
import static us.fatehi.utility.Utility.requireNotBlank;

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
      this.tableName = requireNotBlank(table, "No table name provided");
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
      return String.format(
          "alternate key <%s.%s.%s[%s]>", schema, tableName, alternateKeyName, columns);
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
    if (lookupTable.isPresent()) {
      table = (MutableTable) lookupTable.get();
    } else {
      LOGGER.log(Level.CONFIG, "Table not found, for " + alternateKeyDefinition);
      return Optional.empty();
    }

    final MutablePrimaryKey alternateKey =
        newAlternateKey(table, alternateKeyDefinition.getAlternateKeyName());
    final List<String> columns = alternateKeyDefinition.getColumns();
    for (int i = 0; i < columns.size(); i++) {
      final String columnName = columns.get(i);
      final Column column;
      final Optional<MutableColumn> lookupColumn = table.lookupColumn(columnName);
      if (lookupColumn.isPresent()) {
        column = lookupColumn.get();
      } else {
        LOGGER.log(
            Level.CONFIG,
            String.format("Column <%s> not found, for %s", columnName, alternateKeyDefinition));
        return Optional.empty();
      }

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

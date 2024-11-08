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

package schemacrawler.tools.command.serialize.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Table;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class CompactCatalogUtility {

  public static CatalogDocument createCatalogDocument(final Catalog catalog) {
    requireNonNull(catalog, "No catalog provided");

    final CatalogDocument catalogDocument =
        new CatalogDocument(catalog.getDatabaseInfo().getDatabaseProductName());
    for (final Table table : catalog.getTables()) {
      final TableDocument tableDocument = getTableDocument(table, true);
      catalogDocument.addTable(tableDocument);
    }
    return catalogDocument;
  }

  public static TableDocument getTableDocument(final Table table, final boolean withDependents) {
    requireNonNull(table, "No table provided");

    final Map<String, Column> referencedColumns = mapReferencedColumns(table);
    final TableDocument tableDocument = new TableDocument(table);
    for (final Column column : table.getColumns()) {
      final ColumnDocument columnDocument =
          new ColumnDocument(column, referencedColumns.get(column.getName()));
      tableDocument.addColumn(columnDocument);
    }

    if (withDependents) {
      final Collection<Table> dependentTables = table.getDependentTables();
      for (final Table dependentTable : dependentTables) {
        final TableDocument dependentTableDocument = new TableDocument(dependentTable);
        tableDocument.addDependentTable(dependentTableDocument);
      }
    }

    return tableDocument;
  }

  private static Map<String, Column> mapReferencedColumns(final Table table) {
    requireNonNull(table, "No table provided");

    final Map<String, Column> referencedColumns = new HashMap<>();
    for (final ForeignKey foreignKey : table.getImportedForeignKeys()) {
      final List<ColumnReference> columnReferences = foreignKey.getColumnReferences();
      for (final ColumnReference columnReference : columnReferences) {
        referencedColumns.put(
            columnReference.getForeignKeyColumn().getName(), columnReference.getPrimaryKeyColumn());
      }
    }
    return referencedColumns;
  }

  private CompactCatalogUtility() {
    // Prevent instantiation
  }
}

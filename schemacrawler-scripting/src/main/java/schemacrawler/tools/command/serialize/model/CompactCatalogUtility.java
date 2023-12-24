/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class CompactCatalogUtility {

  private CompactCatalogUtility() {
    // Prevent instantiation
  }

  public static CatalogDescription createCatalogDescription(final Catalog catalog) {
    requireNonNull(catalog, "No catalog provided");

    final CatalogDescription catalogDescription = new CatalogDescription();
    final String databaseProductName = catalog.getDatabaseInfo().getDatabaseProductName();
    catalogDescription.setDatabaseProductName(databaseProductName);
    for (final Schema schema : catalog.getSchemas()) {
      final SchemaDescription schemaDescription = new SchemaDescription();
      schemaDescription.setName(schema.getFullName());
      // Add tables
      for (final Table table : catalog.getTables(schema)) {
        final TableDescription tableDescription = new TableDescription();
        tableDescription.setName(table.getName());
        tableDescription.setRemarks(table.getRemarks());
        // Add columns
        for (final Column column : table.getColumns()) {
          final ColumnDescription columnDescription = new ColumnDescription();
          columnDescription.setName(column.getName());
          columnDescription.setDataType(column.getColumnDataType().getName());
          columnDescription.setRemarks(column.getRemarks());
          tableDescription.addColumn(columnDescription);
        }
        // Add referenced tables
        for (final ForeignKey foreignKey : table.getImportedForeignKeys()) {
          final Table referencedTable = foreignKey.getReferencedTable();
          final TableDescription referencedTableDescription = new TableDescription();
          if (schema.equals(referencedTable.getSchema())) {
            referencedTableDescription.setName(referencedTable.getName());
          } else {
            referencedTableDescription.setName(referencedTable.getFullName());
          }
          tableDescription.addReferencedTable(referencedTableDescription);
        }
        // Add table to the schema
        schemaDescription.addTable(tableDescription);
      }
      catalogDescription.addSchema(schemaDescription);
    }
    return catalogDescription;
  }
}

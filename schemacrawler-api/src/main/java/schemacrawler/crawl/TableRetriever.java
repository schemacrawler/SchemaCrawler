/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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


import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableType;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.utility.TableTypes;
import sf.util.StringFormat;

/**
 * A retriever uses database metadata to get the details about the
 * database tables.
 *
 * @author Sualeh Fatehi
 */
final class TableRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger
    .getLogger(TableRetriever.class.getName());

  TableRetriever(final RetrieverConnection retrieverConnection,
                 final MutableCatalog catalog)
    throws SQLException
  {
    super(retrieverConnection, catalog);
  }

  void retrieveTables(final String catalogName,
                      final String schemaName,
                      final String tableNamePattern,
                      final Collection<String> tableTypes,
                      final InclusionRule tableInclusionRule)
    throws SQLException
  {
    final InclusionRuleFilter<Table> tableFilter = new InclusionRuleFilter<>(tableInclusionRule,
                                                                             false);
    if (tableFilter.isExcludeAll())
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving tables, since this was not requested");
      return;
    }

    final TableTypes supportedTableTypes = getRetrieverConnection()
      .getTableTypes();
    final String[] filteredTableTypes = supportedTableTypes
      .filterUnknown(tableTypes);
    LOGGER.log(Level.FINER,
               new StringFormat("Retrieving table types: %s",
                                filteredTableTypes == null? "<<all>>": Arrays
                                  .asList(filteredTableTypes)));

    LOGGER.log(Level.INFO, "Retrieving tables");

    try (
        final MetadataResultSet results = new MetadataResultSet("retrieveTables",
                                                                getMetaData()
                                                                  .getTables(unquotedName(catalogName),
                                                                             unquotedName(schemaName),
                                                                             tableNamePattern,
                                                                             filteredTableTypes));)
    {
      while (results.next())
      {
        // "TABLE_CAT", "TABLE_SCHEM"
        final String tableName = quotedName(results.getString("TABLE_NAME"));
        LOGGER
          .log(Level.FINER,
               String.format("Retrieving table: %s.%s", schemaName, tableName));
        final String tableTypeString = results.getString("TABLE_TYPE");
        final String remarks = results.getString("REMARKS");

        final SchemaReference schemaReference = new SchemaReference(catalogName,
                                                                    schemaName);
        final Optional<Schema> schemaOptional = catalog
          .lookupSchema(schemaReference.getFullName());
        if (!schemaOptional.isPresent())
        {
          LOGGER.log(Level.FINER,
                     new StringFormat("Cannot locate schema: %s.%s",
                                      catalogName,
                                      schemaName));
          continue;
        }

        final Schema schema = schemaOptional.get();

        final TableType tableType = supportedTableTypes
          .lookupTableType(tableTypeString).orElse(TableType.UNKNOWN);

        final MutableTable table;
        if (tableType.isView())
        {
          table = new MutableView(schema, tableName);
        }
        else
        {
          table = new MutableTable(schema, tableName);
        }
        if (tableFilter.test(table))
        {
          table.setTableType(tableType);
          table.setRemarks(remarks);

          catalog.addTable(table);
        }
      }
    }
  }

}

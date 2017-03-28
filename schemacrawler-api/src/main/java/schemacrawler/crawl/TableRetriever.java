/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
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
                 final MutableCatalog catalog,
                 final SchemaCrawlerOptions options)
    throws SQLException
  {
    super(retrieverConnection, catalog, options);
  }

  void retrieveTables(final NamedObjectList<SchemaReference> schemas,
                      final String tableNamePattern,
                      final Collection<String> tableTypes,
                      final InclusionRule tableInclusionRule)
    throws SQLException
  {
    requireNonNull(schemas);

    final InclusionRuleFilter<Table> tableFilter = new InclusionRuleFilter<>(tableInclusionRule,
                                                                             false);
    if (tableFilter.isExcludeAll())
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving tables, since this was not requested");
      return;
    }

    final MetadataRetrievalStrategy tableRetrievalStrategy = getRetrieverConnection()
      .getTableRetrievalStrategy();
    switch (tableRetrievalStrategy)
    {
      case data_dictionary_all:
        LOGGER.log(Level.INFO,
                   "Retrieving tables, using fast data dictionary retrieval");
        retrieveTablesFromDataDictionary(schemas,
                                         tableNamePattern,
                                         tableTypes,
                                         tableFilter);
        break;

      case metadata_all:
        LOGGER.log(Level.INFO,
                   "Retrieving tables, using fast meta-data retrieval");
        retrieveTablesFromMetadataForAllTables(schemas,
                                               tableNamePattern,
                                               tableTypes,
                                               tableFilter);
        break;

      case metadata:
        LOGGER.log(Level.INFO, "Retrieving tables");
        retrieveTablesFromMetadata(schemas,
                                   tableNamePattern,
                                   tableTypes,
                                   tableFilter);
        break;

      default:
        break;
    }
  }

  private void createTable(final MetadataResultSet results,
                           final NamedObjectList<SchemaReference> schemas,
                           final InclusionRuleFilter<Table> tableFilter,
                           final TableTypes supportedTableTypes)
  {
    final String columnCatalogName = quotedName(results.getString("TABLE_CAT"));
    final String schemaName = quotedName(results.getString("TABLE_SCHEM"));
    final String tableName = quotedName(results.getString("TABLE_NAME"));
    LOGGER.log(Level.FINE,
               String.format("Retrieving table <%s.%s.%s>",
                             columnCatalogName,
                             schemaName,
                             tableName));
    final String tableTypeString = results.getString("TABLE_TYPE");
    final String remarks = results.getString("REMARKS");

    final Optional<SchemaReference> optionalSchema = schemas
      .lookup(new SchemaReference(columnCatalogName, schemaName).toString());
    if (!optionalSchema.isPresent())
    {
      return;
    }
    final Schema schema = optionalSchema.get();

    final TableType tableType = supportedTableTypes
      .lookupTableType(tableTypeString).orElse(TableType.UNKNOWN);
    if (tableType.equals(TableType.UNKNOWN))
    {
      LOGGER.log(Level.FINE,
                 new StringFormat("Unknown table type <%s> for <%s.%s>",
                                  tableTypeString,
                                  schema,
                                  tableName));
    }

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

  private void retrieveTablesFromDataDictionary(final NamedObjectList<SchemaReference> schemas,
                                                final String tableNamePattern,
                                                final Collection<String> tableTypes,
                                                final InclusionRuleFilter<Table> tableFilter)
  {
    // TODO Auto-generated method stub

  }

  private void retrieveTablesFromMetadata(final NamedObjectList<SchemaReference> schemas,
                                          final String tableNamePattern,
                                          final Collection<String> tableTypes,
                                          final InclusionRuleFilter<Table> tableFilter)
    throws SQLException
  {
    for (final Schema schema: schemas)
    {
      LOGGER.log(Level.INFO,
                 new StringFormat("Retrieving tables for schema <%s>", schema));

      final TableTypes supportedTableTypes = getRetrieverConnection()
        .getTableTypes();
      final String[] filteredTableTypes = supportedTableTypes
        .filterUnknown(tableTypes);
      LOGGER.log(Level.FINER,
                 new StringFormat("Retrieving table types <%s>",
                                  filteredTableTypes == null? "<<all>>": Arrays
                                    .asList(filteredTableTypes)));

      final String catalogName = schema.getCatalogName();
      final String schemaName = schema.getName();

      try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
        .getTables(unquotedName(catalogName),
                   unquotedName(schemaName),
                   tableNamePattern,
                   filteredTableTypes));)
      {
        results.setDescription("retrieveTables");
        while (results.next())
        {
          createTable(results, schemas, tableFilter, supportedTableTypes);
        }
      }
    }
  }

  private void retrieveTablesFromMetadataForAllTables(final NamedObjectList<SchemaReference> schemas,
                                                      final String tableNamePattern,
                                                      final Collection<String> tableTypes,
                                                      final InclusionRuleFilter<Table> tableFilter)
    throws SQLException
  {
    final TableTypes supportedTableTypes = getRetrieverConnection()
      .getTableTypes();
    final String[] filteredTableTypes = supportedTableTypes
      .filterUnknown(tableTypes);
    LOGGER.log(Level.FINER,
               new StringFormat("Retrieving table types <%s>",
                                filteredTableTypes == null? "<<all>>": Arrays
                                  .asList(filteredTableTypes)));

    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getTables(null,
                 null,
                 tableNamePattern,
                 filteredTableTypes));)
    {
      results.setDescription("retrieveTables");
      while (results.next())
      {
        createTable(results, schemas, tableFilter, supportedTableTypes);
      }
    }
  }

}

/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnsRetrievalStrategy;
import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.Column;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerSQLException;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

/**
 * A retriever uses database metadata to get the details about the database
 * table columns.
 *
 * @author Sualeh Fatehi
 */
final class TableColumnRetriever
  extends AbstractRetriever
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(TableColumnRetriever.class.getName());

  TableColumnRetriever(final RetrieverConnection retrieverConnection,
                       final MutableCatalog catalog,
                       final SchemaCrawlerOptions options)
    throws SQLException
  {
    super(retrieverConnection, catalog, options);
  }

  void retrieveTableColumns(final NamedObjectList<MutableTable> allTables,
                            final InclusionRule columnInclusionRule)
    throws SQLException
  {
    requireNonNull(allTables, "No tables provided");

    final InclusionRuleFilter<Column> columnFilter =
      new InclusionRuleFilter<>(columnInclusionRule, true);
    if (columnFilter.isExcludeAll())
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving table columns, since this was not requested");
      return;
    }

    final Set<List<String>> hiddenColumns = retrieveHiddenTableColumns();

    switch (getRetrieverConnection().get(tableColumnsRetrievalStrategy))
    {
      case data_dictionary_all:
        LOGGER.log(Level.INFO,
                   "Retrieving table columns, using fast data dictionary retrieval");
        retrieveTableColumnsFromDataDictionary(allTables,
                                               columnFilter,
                                               hiddenColumns);
        break;

      case metadata:
        LOGGER.log(Level.INFO, "Retrieving table columns");
        retrieveTableColumnsFromMetadata(allTables,
                                         columnFilter,
                                         hiddenColumns);
        break;

      default:
        break;
    }

  }

  private void createTableColumn(final MetadataResultSet results,
                                 final NamedObjectList<MutableTable> allTables,
                                 final InclusionRuleFilter<Column> columnFilter,
                                 final Set<List<String>> hiddenColumns)
  {
    // Get the "COLUMN_DEF" value first as it the Oracle drivers
    // don't handle it properly otherwise.
    // https://community.oracle.com/message/5940745#5940745
    // NOTE: Still an issue with Oracle JDBC driver 11.2.0.3.0
    final String defaultValue = results.getString("COLUMN_DEF");
    //

    final String columnCatalogName =
      normalizeCatalogName(results.getString("TABLE_CAT"));
    final String schemaName =
      normalizeSchemaName(results.getString("TABLE_SCHEM"));
    final String tableName = results.getString("TABLE_NAME");
    final String columnName = results.getString("COLUMN_NAME");
    LOGGER.log(Level.FINE,
               new StringFormat("Retrieving table column <%s.%s.%s.%s>",
                                columnCatalogName,
                                schemaName,
                                tableName,
                                columnName));
    if (isBlank(columnName))
    {
      return;
    }

    final Optional<MutableTable> optionalTable =
      allTables.lookup(Arrays.asList(columnCatalogName, schemaName, tableName));
    if (!optionalTable.isPresent())
    {
      return;
    }

    final MutableTable table = optionalTable.get();
    final MutableColumn column = lookupOrCreateTableColumn(table, columnName);
    if (columnFilter.test(column) && belongsToSchema(table,
                                                     columnCatalogName,
                                                     schemaName))
    {
      final int ordinalPosition = results.getInt("ORDINAL_POSITION", 0);
      final int dataType = results.getInt("DATA_TYPE", 0);
      final String typeName = results.getString("TYPE_NAME");
      final int size = results.getInt("COLUMN_SIZE", 0);
      final int decimalDigits = results.getInt("DECIMAL_DIGITS", 0);
      final boolean isNullable =
        results.getInt("NULLABLE", DatabaseMetaData.columnNullableUnknown)
        == DatabaseMetaData.columnNullable;
      final boolean isAutoIncremented = results.getBoolean("IS_AUTOINCREMENT");
      final boolean isGenerated = results.getBoolean("IS_GENERATEDCOLUMN");
      final String remarks = results.getString("REMARKS");

      final List<String> lookupKey =
        Arrays.asList(columnCatalogName, schemaName, tableName, columnName);
      final boolean isHidden = hiddenColumns.contains(lookupKey);

      String columnDataTypeName = null;
      if (!isBlank(typeName))
      {
        final String[] split = typeName.split("\\.");
        if (split.length > 0)
        {
          columnDataTypeName = split[split.length - 1];
        }
      }
      if (isBlank(columnDataTypeName))
      {
        columnDataTypeName = typeName;
      }

      column.setOrdinalPosition(ordinalPosition);
      column.setColumnDataType(lookupOrCreateColumnDataType(table.getSchema(),
                                                            dataType,
                                                            columnDataTypeName));
      column.setSize(size);
      column.setDecimalDigits(decimalDigits);
      column.setNullable(isNullable);
      column.setAutoIncremented(isAutoIncremented);
      column.setGenerated(isGenerated);
      column.setRemarks(remarks);
      if (defaultValue != null)
      {
        column.setDefaultValue(defaultValue);
      }

      column.addAttributes(results.getAttributes());

      LOGGER.log(Level.FINER,
                 new StringFormat("Adding %scolumn to table <%s>",
                                  isHidden? "hidden ": "",
                                  column));
      if (isHidden)
      {
        column.setHidden(true);
        table.addHiddenColumn(column);
      }
      else
      {
        table.addColumn(column);
      }
    }

  }

  private MutableColumn lookupOrCreateTableColumn(final MutableTable table,
                                                  final String columnName)
  {
    final Optional<MutableColumn> columnOptional =
      table.lookupColumn(columnName);
    final MutableColumn column =
      columnOptional.orElseGet(() -> new MutableColumn(table, columnName));
    return column;
  }

  private Set<List<String>> retrieveHiddenTableColumns()
    throws SQLException
  {

    final Set<List<String>> hiddenColumns = new HashSet<>();

    final InformationSchemaViews informationSchemaViews =
      getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(InformationSchemaKey.EXT_HIDDEN_TABLE_COLUMNS))
    {
      LOGGER.log(Level.INFO, "No hidden table columns SQL provided");
      return hiddenColumns;
    }
    final Query hiddenColumnsSql =
      informationSchemaViews.getQuery(InformationSchemaKey.EXT_HIDDEN_TABLE_COLUMNS);
    final Connection connection = getDatabaseConnection();
    try (
      final Statement statement = connection.createStatement();
      final MetadataResultSet results = new MetadataResultSet(hiddenColumnsSql,
                                                              statement,
                                                              getSchemaInclusionRule())
    )
    {
      results.setDescription("retrieveHiddenColumns");
      while (results.next())
      {
        // NOTE: The column names in the extension table are different
        // than the database metadata column names
        final String catalogName =
          normalizeCatalogName(results.getString("TABLE_CATALOG"));
        final String schemaName =
          normalizeSchemaName(results.getString("TABLE_SCHEMA"));
        final String tableName = results.getString("TABLE_NAME");
        final String columnName = results.getString("COLUMN_NAME");

        LOGGER.log(Level.FINE,
                   new StringFormat("Retrieving hidden column <%s.%s.%s.%s>",
                                    catalogName,
                                    schemaName,
                                    tableName,
                                    columnName));

        final List<String> lookupKey =
          Arrays.asList(catalogName, schemaName, tableName, columnName);
        hiddenColumns.add(lookupKey);
      }
    }

    return hiddenColumns;
  }

  private void retrieveTableColumnsFromDataDictionary(final NamedObjectList<MutableTable> allTables,
                                                      final InclusionRuleFilter<Column> columnFilter,
                                                      final Set<List<String>> hiddenColumns)
    throws SQLException
  {
    final InformationSchemaViews informationSchemaViews =
      getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(InformationSchemaKey.TABLE_COLUMNS))
    {
      throw new SchemaCrawlerSQLException("No table columns SQL provided",
                                          null);
    }
    final Query tableColumnsSql =
      informationSchemaViews.getQuery(InformationSchemaKey.TABLE_COLUMNS);
    final Connection connection = getDatabaseConnection();
    try (
      final Statement statement = connection.createStatement();
      final MetadataResultSet results = new MetadataResultSet(tableColumnsSql,
                                                              statement,
                                                              getSchemaInclusionRule())
    )
    {
      results.setDescription("retrieveTableColumnsFromDataDictionary");
      while (results.next())
      {
        createTableColumn(results, allTables, columnFilter, hiddenColumns);
      }
    }
  }

  private void retrieveTableColumnsFromMetadata(final NamedObjectList<MutableTable> allTables,
                                                final InclusionRuleFilter<Column> columnFilter,
                                                final Set<List<String>> hiddenColumns)
    throws SchemaCrawlerSQLException
  {
    for (final MutableTable table : allTables)
    {
      LOGGER.log(Level.FINE, "Retrieving table columns for " + table);
      try (
        final MetadataResultSet results = new MetadataResultSet(getMetaData().getColumns(
          table
            .getSchema()
            .getCatalogName(),
          table
            .getSchema()
            .getName(),
          table.getName(),
          null))
      )
      {
        while (results.next())
        {
          createTableColumn(results, allTables, columnFilter, hiddenColumns);
        }
      }
      catch (final SQLException e)
      {
        throw new SchemaCrawlerSQLException(String.format(
          "Could not retrieve table columns for %s <%s>",
          table.getTableType(),
          table), e);
      }
    }
  }

}

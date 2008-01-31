/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.crawl;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.crawl.NamedObjectList.NamedObjectSort;
import schemacrawler.schema.ForeignKeyDeferrability;
import schemacrawler.schema.ForeignKeyUpdateRule;
import schemacrawler.schema.IndexSortSequence;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.TableType;

/**
 * TableRetriever uses database metadata to get the details about the
 * schema.
 * 
 * @author Sualeh Fatehi
 */
final class TableRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger.getLogger(TableRetriever.class
    .getName());

  TableRetriever(final RetrieverConnection retrieverConnection)
    throws SQLException
  {
    super(retrieverConnection);
  }

  /**
   * Retrieves a list of columns from the database, for the table
   * specified.
   * 
   * @param table
   *        Table for which data is required.
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveColumns(final MutableTable table,
                       final InclusionRule columnInclusionRule,
                       final NamedObjectList<MutableColumnDataType> columnDataTypes)
    throws SQLException
  {
    final MetadataResultSet results = new MetadataResultSet(getRetrieverConnection()
      .getMetaData().getColumns(getRetrieverConnection().getCatalog(),
                                table.getSchemaName(),
                                table.getName(),
                                null));
    try
    {
      while (results.next())
      {
        // Get the "COLUMN_DEF" value first as it the Oracle drivers
        // don't handle it properly otherwise.
        // http://issues.apache.org/jira/browse/DDLUTILS-29?page=all
        final String defaultValue = results.getString("COLUMN_DEF");
        //
        final String tableName = results.getString("TABLE_NAME");
        final String columnName = results.getString(COLUMN_NAME);

        final MutableColumn column = new MutableColumn(columnName, table);
        final String columnFullName = column.getFullName();
        // Note: If the table name contains an underscore character,
        // this is a wildcard character. We need to do another check to
        // see if the table name matches.
        if (columnInclusionRule.include(columnFullName)
            && table.getName().equals(tableName))
        {
          LOGGER.log(Level.FINEST, "Retrieving column: " + columnName);
          final int ordinalPosition = results.getInt(ORDINAL_POSITION, 0);
          final int dataType = results.getInt(DATA_TYPE, 0);
          final String typeName = results.getString(TYPE_NAME);
          final int size = results.getInt("COLUMN_SIZE", 0);
          final int decimalDigits = results.getInt("DECIMAL_DIGITS", 0);
          final boolean isNullable = results
            .getInt(NULLABLE, DatabaseMetaData.columnNullableUnknown) == DatabaseMetaData.columnNullable;
          final String remarks = results.getString(REMARKS);

          column.setOrdinalPosition(ordinalPosition);
          lookupAndSetDataType(column, dataType, typeName, columnDataTypes);
          column.setSize(size);
          column.setDecimalDigits(decimalDigits);
          column.setRemarks(remarks);
          column.setNullable(isNullable);
          if (defaultValue != null)
          {
            column.setDefaultValue(defaultValue);
          }

          column.addAttributes(results.getAttributes());

          table.addColumn(column);
        }
      }
    }
    finally
    {
      results.close();
    }

  }

  void retrieveForeignKeys(final NamedObjectList<MutableTable> tables,
                           final String tableName)
    throws SQLException
  {

    final MutableTable table = tables.lookup(tableName);
    final String schema = table.getSchemaName();

    final Map<String, MutableForeignKey> foreignKeysMap = new HashMap<String, MutableForeignKey>();

    MetadataResultSet results;

    final String catalog = getRetrieverConnection().getCatalog();
    final DatabaseMetaData metaData = getRetrieverConnection().getMetaData();

    results = new MetadataResultSet(metaData.getImportedKeys(catalog,
                                                             schema,
                                                             tableName));
    createForeignKeys(results, tables, table, foreignKeysMap);

    results = new MetadataResultSet(metaData.getExportedKeys(catalog,
                                                             schema,
                                                             tableName));
    createForeignKeys(results, tables, table, foreignKeysMap);

    final Collection<MutableForeignKey> foreignKeyCollection = foreignKeysMap
      .values();
    for (final MutableForeignKey foreignKey: foreignKeyCollection)
    {
      table.addForeignKey(foreignKey);
    }

  }

  void retrieveIndices(final MutableTable table,
                       final boolean unique,
                       final boolean approximate)
    throws SQLException
  {

    final Map<String, MutableIndex> indicesMap = new HashMap<String, MutableIndex>();

    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();

    MetadataResultSet results = null;
    if (informationSchemaViews.hasIndexInfoSql())
    {
      final String indexInfoSql = informationSchemaViews.getIndexInfo()
        .getQueryForTable(table);
      LOGGER.log(Level.FINE, "Using getIndexInfo SQL:\n" + indexInfoSql);
      final Connection connection = getDatabaseConnection();
      final Statement statement = connection.createStatement();
      results = new MetadataResultSet(statement.executeQuery(indexInfoSql));
    }
    else
    {
      LOGGER.log(Level.FINE, "Using getIndexInfo()");
      results = new MetadataResultSet(getRetrieverConnection().getMetaData()
        .getIndexInfo(getRetrieverConnection().getCatalog(),
                      table.getSchemaName(),
                      table.getName(),
                      unique,
                      approximate));
    }
    createIndices(results, table, indicesMap);

    final Collection<MutableIndex> indexCollection = indicesMap.values();
    for (final MutableIndex index: indexCollection)
    {
      if (index.getColumns().length > 0)
      {
        table.addIndex(index);
      }
    }

  }

  void retrievePrimaryKeys(final MutableTable table)
    throws SQLException
  {

    final String tableName = table.getName();
    // final String getCatalog() = table.getCatalogName();
    final String schema = table.getSchemaName();

    ResultSet results = null;
    try
    {
      results = getRetrieverConnection().getMetaData()
        .getPrimaryKeys(getRetrieverConnection().getCatalog(),
                        schema,
                        tableName);
      MutablePrimaryKey primaryKey = null;
      while (results.next())
      {
        if (primaryKey == null)
        {
          final String primaryKeyName = results.getString("PK_NAME");
          primaryKey = new MutablePrimaryKey(primaryKeyName, table);
        }
        final String columnName = results.getString(COLUMN_NAME);
        final int keySequence = Integer.parseInt(results.getString(KEY_SEQ));
        // register primary key information
        final MutableColumn column = table.lookupColumn(columnName);
        if (column != null)
        {
          column.setPartOfPrimaryKey(true);
          primaryKey.addColumn(keySequence, column);
        }
      }
      table.setPrimaryKey(primaryKey);
    }
    finally
    {
      if (results != null)
      {
        results.close();
      }
    }

  }

  /**
   * Retrieves table metadata according to the parameters specified. No
   * column metadata is retrieved, for reasons of efficiency.
   * 
   * @param tableTypes
   *        Array of table types
   * @param tablePatternInclude
   *        Table name pattern for table
   * @param useRegExpPattern
   *        True is the table name pattern is a regular expression;
   *        false if the table name pattern is the JDBC pattern
   * @return A list of tables in the database that matech the pattern
   * @throws SQLException
   *         On a SQL exception
   */
  NamedObjectList<MutableTable> retrieveTables(final TableType[] tableTypes,
                                               final InclusionRule tableInclusionRule)
    throws SQLException
  {
    final NamedObjectList<MutableTable> tables = new NamedObjectList<MutableTable>(NamedObjectSort.alphabetical);
    final String catalog = getRetrieverConnection().getCatalog();
    final ResultSet results = getRetrieverConnection().getMetaData()
      .getTables(catalog,
                 getRetrieverConnection().getSchemaPattern(),
                 "%",
                 TableType.toStrings(tableTypes));
    try
    {
      results.setFetchSize(FETCHSIZE);
    }
    catch (final NullPointerException e)
    {
      // Need this catch for the JDBC/ ODBC driver
      LOGGER.log(Level.WARNING, "", e);
    }
    try
    {
      while (results.next())
      {
        // final String catalog = results.getString("TABLE_CAT");
        final String schema = results.getString("TABLE_SCHEM");

        final String tableName = results.getString(TABLE_NAME);
        LOGGER.log(Level.FINEST, "Retrieving table: " + tableName);
        final TableType tableType = TableType.valueOf(results
          .getString("TABLE_TYPE").toLowerCase(Locale.ENGLISH));
        final String remarks = results.getString(REMARKS);

        if (tableInclusionRule.include(tableName))
        {
          final MutableTable table;
          if (tableType == TableType.view)
          {
            table = new MutableView(catalog, schema, tableName);
          }
          else
          {
            table = new MutableTable(catalog, schema, tableName);
          }
          table.setType(tableType);
          table.setRemarks(remarks);

          tables.add(table);
        }
      }
    }
    finally
    {
      results.close();
    }

    return tables;

  }

  /**
   * @param results
   * @param tablesMap
   * @param table
   * @param foreignKeysMap
   * @throws SQLException
   */
  private void createForeignKeys(final MetadataResultSet results,
                                 final NamedObjectList<MutableTable> tables,
                                 final MutableTable table,
                                 final Map<String, MutableForeignKey> foreignKeysMap)
    throws SQLException
  {

    try
    {
      while (results.next())
      {
        String foreignKeyName = results.getString("FK_NAME");
        if (foreignKeyName == null || foreignKeyName.length() == 0)
        {
          foreignKeyName = UNKNOWN;
        }
        MutableForeignKey foreignKey = foreignKeysMap.get(foreignKeyName);
        if (foreignKey == null)
        {
          foreignKey = new MutableForeignKey(table.getCatalogName(), table
            .getSchemaName(), foreignKeyName);
          foreignKeysMap.put(foreignKeyName, foreignKey);
        }
        final String pkTableSchema = results.getString("PKTABLE_SCHEM");
        final String pkTableName = results.getString("PKTABLE_NAME");
        final String pkColumnName = results.getString("PKCOLUMN_NAME");
        final String fkTableName = results.getString("FKTABLE_NAME");
        final String fkColumnName = results.getString("FKCOLUMN_NAME");
        final int keySequence = results.getInt(KEY_SEQ, 0);
        final int updateRule = results.getInt("UPDATE_RULE",
                                              ForeignKeyUpdateRule.unknown
                                                .getId());
        final int deleteRule = results.getInt("DELETE_RULE",
                                              ForeignKeyUpdateRule.unknown
                                                .getId());
        final int deferrability = results
          .getInt("DEFERRABILITY", ForeignKeyDeferrability.unknown.getId());
        final MutableColumn pkColumn = lookupOrCreateColumn(tables,
                                                            pkTableSchema,
                                                            pkTableName,
                                                            pkColumnName);
        final MutableColumn fkColumn = lookupOrCreateColumn(tables,
                                                            pkTableSchema,
                                                            fkTableName,
                                                            fkColumnName);
        // Make a direct connection between the two columns
        fkColumn.setReferencedColumn(pkColumn);
        foreignKey.addColumnPair(keySequence, pkColumn, fkColumn);
        foreignKey.setUpdateRule(ForeignKeyUpdateRule.valueOf(updateRule));
        foreignKey.setDeleteRule(ForeignKeyUpdateRule.valueOf(deleteRule));
        foreignKey.setDeferrability(ForeignKeyDeferrability
          .valueOf(deferrability));

        foreignKey.addAttributes(results.getAttributes());
      }
    }
    finally
    {
      results.close();
    }

  }

  private void createIndices(final MetadataResultSet results,
                             final MutableTable table,
                             final Map<String, MutableIndex> indicesMap)
    throws SQLException
  {
    try
    {
      while (results.next())
      {
        String indexName = results.getString("INDEX_NAME");
        if (indexName == null || indexName.length() == 0)
        {
          indexName = UNKNOWN;
        }
        MutableIndex index = indicesMap.get(indexName);
        if (index == null)
        {
          index = new MutableIndex(indexName, table);
          indicesMap.put(indexName, index);
        }
        final String columnName = results.getString(COLUMN_NAME);
        if (columnName == null || columnName.trim().length() == 0)
        {
          continue;
        }
        final boolean uniqueIndex = !results.getBoolean("NON_UNIQUE");
        final int type = results.getInt("TYPE", IndexType.unknown.getId());
        final int ordinalPosition = results.getInt(ORDINAL_POSITION, 0);
        final String sortSequence = results.getString("ASC_OR_DESC");
        final int cardinality = results.getInt("CARDINALITY", 0);
        final int pages = results.getInt("PAGES", 0);

        final MutableColumn column = table.lookupColumn(columnName);
        if (column != null)
        {
          index.addColumn(ordinalPosition, column);
          index.setUnique(uniqueIndex);
          column.setPartOfUniqueIndex(uniqueIndex);
          index.setType(IndexType.valueOf(type));
          index
            .setSortSequence(IndexSortSequence.valueOfFromCode(sortSequence));
          index.setCardinality(cardinality);
          index.setPages(pages);

          index.addAttributes(results.getAttributes());
        }
      }
    }
    finally
    {
      results.close();
    }
  }

  private MutableColumn lookupOrCreateColumn(final NamedObjectList<MutableTable> tables,
                                             final String schema,
                                             final String tableName,
                                             final String columnName)
  {

    MutableColumn column = null;
    MutableTable table = tables.lookup(tableName);
    if (table != null)
    {
      column = table.lookupColumn(columnName);
    }
    if (column == null)
    {
      final String catalog = getRetrieverConnection().getCatalog();
      table = new MutableTable(catalog, schema, tableName);
      column = new MutableColumn(columnName, table);
    }
    return column;
  }

}

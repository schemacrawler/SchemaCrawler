/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ForeignKeyDeferrability;
import schemacrawler.schema.ForeignKeyUpdateRule;
import schemacrawler.schema.IndexSortSequence;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.TableType;
import schemacrawler.util.AlphabeticalSortComparator;
import sf.util.Utilities;

/**
 * TableRetriever uses database metadata to get the details about the
 * schema.
 * 
 * @author sfatehi
 */
final class TableRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger.getLogger(TableRetriever.class
      .getName());

  /**
   * Constructs a SchemaCrawler object, from a connection.
   * 
   * @param connection
   *        An open database connection.
   * @param driverClassName
   *        Class name of the JDBC driver
   * @param schemaPatternString
   *        JDBC schema pattern, or null
   * @throws SQLException
   *         On a SQL exception
   */
  TableRetriever(final RetrieverConnection retrieverConnection)
    throws SQLException
  {
    super(retrieverConnection);
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
  NamedObjectList retrieveTables(final TableType[] tableTypes,
      final InclusionRule tableInclusionRule)
    throws SQLException
  {
    LOGGER.entering(getClass().getName(), "retrieveTables", new Object[]
    { tableTypes, tableInclusionRule });

    final NamedObjectList tables = new NamedObjectList(
        new AlphabeticalSortComparator());
    String catalog = getRetrieverConnection().getCatalog();
    final ResultSet results = getRetrieverConnection().getMetaData().getTables(
        catalog, getRetrieverConnection().getSchemaPattern(), "%",
        TableType.toStringArray(tableTypes));
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
            .getString("TABLE_TYPE"));
        final String remarks = results.getString(REMARKS);

        if (tableInclusionRule.include(tableName))
        {
          final MutableTable table;
          if (tableType.isView())
          {
            table = new MutableView(catalog, schema, tableName);
          } else
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
      final NamedObjectList columnDataTypes)
    throws SQLException
  {
    LOGGER.entering(getClass().getName(), "retrieveColumns", new Object[]
    { table, columnInclusionRule });

    final ResultSet results = getRetrieverConnection().getMetaData()
        .getColumns(getRetrieverConnection().getCatalog(),
            table.getSchemaName(), table.getName(), null);
    try
    {
      while (results.next())
      {
        // Get the "COLUMN_DEF" value first as it the Oracle drivers
        // don't handle it properly otherwise.
        // http://issues.apache.org/jira/browse/DDLUTILS-29?page=all
        final String defaultValue = results.getString("COLUMN_DEF");
        //        
        final String columnName = results.getString(COLUMN_NAME);
        LOGGER.log(Level.FINEST, "Retrieving column: " + columnName);
        final int oridinalPosition = results.getInt(ORDINAL_POSITION);
        final int dataType = results.getInt(DATA_TYPE);
        final String typeName = results.getString(TYPE_NAME);
        final int size = results.getInt("COLUMN_SIZE");
        final int decimalDigits = results.getInt("DECIMAL_DIGITS");
        final boolean isNullable = results.getInt(NULLABLE) == DatabaseMetaData.columnNullable;
        final String remarks = results.getString(REMARKS);

        final MutableColumn column = new MutableColumn(columnName, table);
        final String columnFullName = column.getFullName();
        if (columnInclusionRule.include(columnFullName))
        {
          column.setOrdinalPosition(oridinalPosition);
          column.lookupAndSetDataType(dataType, typeName, columnDataTypes);
          column.setSize(size);
          column.setDecimalDigits(decimalDigits);
          column.setRemarks(remarks);
          column.setNullable(isNullable);
          if (defaultValue != null)
          {
            column.setDefaultValue(defaultValue);
          }

          table.addColumn(column);
        }
      }
    }
    finally
    {
      results.close();
    }

  }

  void retrieveIndices(final MutableTable table, final boolean unique,
      final boolean approximate)
    throws SQLException
  {

    final Map indicesMap = new HashMap();

    InformationSchemaViews informationSchemaViews = getRetrieverConnection()
        .getInformationSchemaViews();

    ResultSet results = null;
    if (informationSchemaViews.hasIndexInfoSql())
    {
      String indexInfoSql = informationSchemaViews.getIndexInfoSql();
      indexInfoSql = CrawlerUtililties.expandSqlForTable(indexInfoSql, table);
      LOGGER.log(Level.FINE, "Using getIndexInfo SQL:" + Utilities.NEWLINE
          + indexInfoSql);
      final Connection connection = getRetrieverConnection().getMetaData()
          .getConnection();
      final Statement statement = connection.createStatement();
      results = statement.executeQuery(indexInfoSql);
    } else
    {
      LOGGER.log(Level.FINE, "Using getIndexInfo()");
      results = getRetrieverConnection().getMetaData().getIndexInfo(
          getRetrieverConnection().getCatalog(), table.getSchemaName(),
          table.getName(), unique, approximate);
    }
    createIndices(results, table, indicesMap);

    final Collection indexCollection = indicesMap.values();
    for (final Iterator iter = indexCollection.iterator(); iter.hasNext();)
    {
      final MutableIndex index = (MutableIndex) iter.next();
      if (index.getColumns().length > 0)
      {
        table.addIndex(index);
      }
    }

  }

  private static void createIndices(final ResultSet results,
      final MutableTable table, final Map indicesMap)
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
        MutableIndex index = (MutableIndex) indicesMap.get(indexName);
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
        final int type = results.getInt("TYPE");
        final int ordinalPosition = results.getInt(ORDINAL_POSITION);
        final String sortSequence = results.getString("ASC_OR_DESC");
        final int cardinality = results.getInt("CARDINALITY");
        final int pages = results.getInt("PAGES");

        final MutableColumn column = (MutableColumn) table
            .lookupColumn(columnName);
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
        }
      }
    }
    finally
    {
      results.close();
    }
  }

  void retrieveForeignKeys(final NamedObjectList tables, final int index)
    throws SQLException
  {

    final Map tablesMap = tables.getMap();
    final MutableTable table = (MutableTable) tables.get(index);

    final String tableName = table.getName();
    final String schema = table.getSchemaName();

    final Map foreignKeysMap = new HashMap();

    ResultSet results;

    final String catalog = getRetrieverConnection().getCatalog();
    final DatabaseMetaData metaData = getRetrieverConnection().getMetaData();

    results = metaData.getImportedKeys(catalog, schema, tableName);
    createForeignKeys(results, tablesMap, table, foreignKeysMap);

    results = metaData.getExportedKeys(catalog, schema, tableName);
    createForeignKeys(results, tablesMap, table, foreignKeysMap);

    final Collection foreignKeyCollection = foreignKeysMap.values();
    for (final Iterator iter = foreignKeyCollection.iterator(); iter.hasNext();)
    {
      final MutableForeignKey foreignKey = (MutableForeignKey) iter.next();
      table.addForeignKey(foreignKey);
    }

  }

  /**
   * @param results
   * @param tablesMap
   * @param table
   * @param foreignKeysMap
   * @throws SQLException
   */
  private static void createForeignKeys(final ResultSet results,
      final Map tablesMap, final MutableTable table, final Map foreignKeysMap)
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
        MutableForeignKey foreignKey = (MutableForeignKey) foreignKeysMap
            .get(foreignKeyName);
        if (foreignKey == null)
        {
          foreignKey = new MutableForeignKey(table
              .getCatalogName(), table.getSchemaName(), foreignKeyName);
          foreignKeysMap.put(foreignKeyName, foreignKey);
        }
        final String pkTableName = results.getString("PKTABLE_NAME");
        final String pkColumnName = results.getString("PKCOLUMN_NAME");
        final String fkTableName = results.getString("FKTABLE_NAME");
        final String fkColumnName = results.getString("FKCOLUMN_NAME");
        final int keySequence = results.getInt(KEY_SEQ);
        final int updateRule = results.getInt("UPDATE_RULE");
        final int deleteRule = results.getInt("DELETE_RULE");
        final int deferrability = results.getInt("DEFERRABILITY");
        final MutableColumn pkColumn = lookupOrCreateColumn(tablesMap,
            pkTableName, pkColumnName);
        final MutableColumn fkColumn = lookupOrCreateColumn(tablesMap,
            fkTableName, fkColumnName);
        foreignKey.addColumnPair(keySequence, pkColumn, fkColumn);
        foreignKey
            .setUpdateRule(ForeignKeyUpdateRule.valueOfFromId(updateRule));
        foreignKey
            .setDeleteRule(ForeignKeyUpdateRule.valueOfFromId(deleteRule));
        foreignKey.setDeferrability(ForeignKeyDeferrability
            .valueOfFromId(deferrability));
      }
    }
    finally
    {
      results.close();
    }

  }

  private static MutableColumn lookupOrCreateColumn(final Map tablesMap,
      final String tableName, final String columnName)
  {

    MutableColumn column = null;
    MutableTable table = (MutableTable) tablesMap.get(tableName);
    if (table != null)
    {
      column = (MutableColumn) table.lookupColumn(columnName);
    }
    if (column == null)
    {
      final List tables = new ArrayList(tablesMap.values());
      final MutableTable firstTable = (MutableTable) tables.get(0);
      final String catalog = firstTable.getCatalogName();
      final String schema = firstTable.getSchemaName();
      table = new MutableTable(catalog, schema, tableName);
      column = new MutableColumn(columnName, table);
    }
    return column;
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
      results = getRetrieverConnection().getMetaData().getPrimaryKeys(
          getRetrieverConnection().getCatalog(), schema, tableName);
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
        final MutableColumn column = (MutableColumn) table
            .lookupColumn(columnName);
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

}

/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ForeignKeyDeferrability;
import schemacrawler.schema.ForeignKeyUpdateRule;
import schemacrawler.schema.IndexColumnSortSequence;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.TableType;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.InformationSchemaViews;
import sf.util.TemplatingUtility;
import sf.util.Utility;

/**
 * A retriever uses database metadata to get the details about the
 * database tables.
 * 
 * @author Sualeh Fatehi
 */
final class TableRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger.getLogger(TableRetriever.class
    .getName());

  TableRetriever(final RetrieverConnection retrieverConnection,
                 final MutableDatabase database)
  {
    super(retrieverConnection, database);
  }

  void retrieveColumns(final MutableTable table,
                       final InclusionRule columnInclusionRule)
    throws SQLException
  {
    MetadataResultSet results = null;
    try
    {
      results = new MetadataResultSet(getMetaData()
        .getColumns(table.getSchema().getCatalogName(),
                    table.getSchema().getSchemaName(),
                    getUnquotedName(table.getName()),
                    null), getDatabaseSystemParameters());

      while (results.next())
      {
        // Get the "COLUMN_DEF" value first as it the Oracle drivers
        // don't handle it properly otherwise.
        // http://issues.apache.org/jira/browse/DDLUTILS-29?page=all
        final String defaultValue = results.getString("COLUMN_DEF");
        //
        final String columnCatalogName = results.getString("TABLE_CAT");
        final String schemaName = results.getString("TABLE_SCHEM");
        final String tableName = results.getQuotedName("TABLE_NAME");
        final String columnName = results.getQuotedName("COLUMN_NAME");
        LOGGER.log(Level.FINER, String.format("Retrieving column: %s.%s",
                                              tableName,
                                              columnName));

        MutableColumn column;

        column = lookupOrCreateColumn(table, columnName, false/* add */);
        final String columnFullName = column.getFullName();
        // Note: If the table name contains an underscore character,
        // this is a wildcard character. We need to do another check to
        // see if the table name matches.
        if (columnInclusionRule.include(columnFullName)
            && table.getName().equals(tableName)
            && belongsToSchema(table, columnCatalogName, schemaName))
        {
          column = lookupOrCreateColumn(table, columnName, true/* add */);

          final int ordinalPosition = results.getInt("ORDINAL_POSITION", 0);
          final int dataType = results.getInt("DATA_TYPE", 0);
          final String typeName = results.getString("TYPE_NAME");
          final int size = results.getInt("COLUMN_SIZE", 0);
          final int decimalDigits = results.getInt("DECIMAL_DIGITS", 0);
          final boolean isNullable = results
            .getInt("NULLABLE", DatabaseMetaData.columnNullableUnknown) == DatabaseMetaData.columnNullable;
          final String remarks = results.getString("REMARKS");

          column.setOrdinalPosition(ordinalPosition);
          column.setType(lookupOrCreateColumnDataType((MutableSchema) table
            .getSchema(), dataType, typeName));
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
    catch (final SQLException e)
    {
      final SQLException sqlEx = new SQLException("Could not retrieve columns for table "
                                                  + table
                                                  + ":"
                                                  + e.getMessage());
      sqlEx.setNextException(e);
      throw sqlEx;
    }
    finally
    {
      if (results != null)
      {
        results.close();
      }
    }

  }

  void retrieveForeignKeys(final MutableTable table)
    throws SQLException
  {

    final NamedObjectList<MutableForeignKey> foreignKeys = new NamedObjectList<MutableForeignKey>();
    MetadataResultSet results;

    final DatabaseMetaData metaData = getMetaData();

    results = new MetadataResultSet(metaData.getImportedKeys(table.getSchema()
                                      .getCatalogName(), table.getSchema()
                                      .getSchemaName(), getUnquotedName(table
                                      .getName())),
                                    getDatabaseSystemParameters());
    createForeignKeys(results, foreignKeys);

    results = new MetadataResultSet(metaData.getExportedKeys(table.getSchema()
                                      .getCatalogName(), table.getSchema()
                                      .getSchemaName(), getUnquotedName(table
                                      .getName())),
                                    getDatabaseSystemParameters());
    createForeignKeys(results, foreignKeys);
  }

  void retrieveIndices(final MutableTable table, final boolean unique)
    throws SQLException
  {

    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();

    Statement statement = null;
    MetadataResultSet results = null;
    try
    {
      if (informationSchemaViews.hasIndexInfoSql())
      {
        final Map<String, String> map = new HashMap<String, String>();
        map.put("table", table.getFullName());
        final String indexInfoSql = TemplatingUtility
          .expandTemplate(informationSchemaViews.getIndexInfo(), map);
        LOGGER.log(Level.FINE, "Using getIndexInfo SQL:\n" + indexInfoSql);
        final Connection connection = getDatabaseConnection();
        statement = connection.createStatement();
        results = new MetadataResultSet(statement.executeQuery(indexInfoSql),
                                        getDatabaseSystemParameters());
        createIndices(table, results);
      }
      else
      {
        results = new MetadataResultSet(getMetaData()
          .getIndexInfo(table.getSchema().getCatalogName(),
                        table.getSchema().getSchemaName(),
                        getUnquotedName(table.getName()),
                        unique,
                        true/* approximate */), getDatabaseSystemParameters());
        createIndices(table, results);
      }
    }
    catch (final SQLException e)
    {
      final SQLException sqlEx = new SQLException("Could not retrieve indices for table "
                                                  + table
                                                  + ": "
                                                  + e.getMessage());
      sqlEx.setNextException(e);
      throw sqlEx;
    }
    finally
    {
      if (results != null)
      {
        results.close();
      }
      if (statement != null)
      {
        statement.close();
      }
    }

  }

  void retrievePrimaryKey(final MutableTable table)
    throws SQLException
  {
    MetadataResultSet results = null;
    try
    {
      results = new MetadataResultSet(getMetaData()
                                        .getPrimaryKeys(table.getSchema()
                                                          .getCatalogName(),
                                                        table.getSchema()
                                                          .getSchemaName(),
                                                        getUnquotedName(table
                                                          .getName())),
                                      getDatabaseSystemParameters());

      MutablePrimaryKey primaryKey;
      while (results.next())
      {
        // final String catalogName = results.getString("TABLE_CAT");
        // final String schemaName = results.getString("TABLE_SCHEM");
        // final String tableName = results.getQuotedName("TABLE_NAME");
        final String columnName = results.getQuotedName("COLUMN_NAME");
        final String primaryKeyName = results.getQuotedName("PK_NAME");
        final int keySequence = Integer.parseInt(results.getString("KEY_SEQ"));

        primaryKey = table.getPrimaryKey();
        if (primaryKey == null)
        {
          primaryKey = new MutablePrimaryKey(table, primaryKeyName);
        }

        // Register primary key information
        final MutableColumn column = table.getColumn(columnName);
        if (column != null)
        {
          column.setPartOfPrimaryKey(true);
          final MutableIndexColumn indexColumn = new MutableIndexColumn(primaryKey,
                                                                        column);
          indexColumn.setSortSequence(IndexColumnSortSequence.ascending);
          indexColumn.setIndexOrdinalPosition(keySequence);
          //
          primaryKey.addColumn(indexColumn);
        }

        table.setPrimaryKey(primaryKey);
      }
    }
    catch (final SQLException e)
    {
      final SQLException sqlEx = new SQLException("Could not retrieve primary keys for table "
                                                  + table
                                                  + ": "
                                                  + e.getMessage());
      sqlEx.setNextException(e);
      throw sqlEx;
    }
    finally
    {
      if (results != null)
      {
        results.close();
      }
    }

  }

  void retrieveTables(final String catalogName,
                      final String schemaName,
                      final String tableNamePattern,
                      final TableType[] tableTypes,
                      final InclusionRule tableInclusionRule)
    throws SQLException
  {
    MetadataResultSet results = null;
    try
    {
      results = new MetadataResultSet(getMetaData()
                                        .getTables(catalogName,
                                                   schemaName,
                                                   tableNamePattern,
                                                   TableType
                                                     .toStrings(tableTypes)),
                                      getDatabaseSystemParameters());

      while (results.next())
      {
        // final String catalogName = results.getString("TABLE_CAT");
        // final String schemaName = results.getString("TABLE_SCHEM");
        final String tableName = results.getQuotedName("TABLE_NAME");
        LOGGER.log(Level.FINER, String.format("Retrieving table: %s.%s",
                                              schemaName,
                                              tableName));
        final TableType tableType = TableType.valueOf(results
          .getString("TABLE_TYPE").toLowerCase(Locale.ENGLISH));
        final String remarks = results.getString("REMARKS");

        final MutableSchema schema = lookupSchema(catalogName, schemaName);
        if (schema == null)
        {
          LOGGER.log(Level.FINE, String.format("Cannot find schema, %s.%s",
                                               catalogName,
                                               schemaName));
          continue;
        }

        final MutableTable table;
        if (tableType == TableType.view)
        {
          table = new MutableView(schema, tableName);
        }
        else
        {
          table = new MutableTable(schema, tableName);
        }
        if (tableInclusionRule.include(table.getFullName()))
        {
          table.setType(tableType);
          table.setRemarks(remarks);

          schema.addTable(table);
        }
      }
    }
    finally
    {
      if (results != null)
      {
        results.close();
      }
    }
  }

  private void createForeignKeys(final MetadataResultSet results,
                                 final NamedObjectList<MutableForeignKey> foreignKeys)
    throws SQLException
  {
    try
    {
      while (results.next())
      {
        String foreignKeyName = results.getQuotedName("FK_NAME");
        if (Utility.isBlank(foreignKeyName))
        {
          foreignKeyName = UNKNOWN;
        }
        LOGGER.log(Level.FINER, "Retrieving foreign key: " + foreignKeyName);

        final String pkTableCatalogName = results.getString("PKTABLE_CAT");
        final String pkTableSchemaName = results.getString("PKTABLE_SCHEM");
        final String pkTableName = results.getQuotedName("PKTABLE_NAME");
        final String pkColumnName = results.getQuotedName("PKCOLUMN_NAME");

        final String fkTableCatalogName = results.getString("FKTABLE_CAT");
        final String fkTableSchemaName = results.getString("FKTABLE_SCHEM");
        final String fkTableName = results.getQuotedName("FKTABLE_NAME");
        final String fkColumnName = results.getQuotedName("FKCOLUMN_NAME");

        MutableForeignKey foreignKey = foreignKeys.lookup(foreignKeyName);
        if (foreignKey == null)
        {
          foreignKey = new MutableForeignKey(foreignKeyName);
          foreignKeys.add(foreignKey);
        }

        final int keySequence = results.getInt("KEY_SEQ", 0);
        final int updateRule = results.getInt("UPDATE_RULE",
                                              ForeignKeyUpdateRule.unknown
                                                .getId());
        final int deleteRule = results.getInt("DELETE_RULE",
                                              ForeignKeyUpdateRule.unknown
                                                .getId());
        final int deferrability = results
          .getInt("DEFERRABILITY", ForeignKeyDeferrability.unknown.getId());
        final MutableColumn pkColumn = lookupOrCreateColumn(pkTableCatalogName,
                                                            pkTableSchemaName,
                                                            pkTableName,
                                                            pkColumnName);
        final MutableColumn fkColumn = lookupOrCreateColumn(fkTableCatalogName,
                                                            fkTableSchemaName,
                                                            fkTableName,
                                                            fkColumnName);
        // Make a direct connection between the two columns
        if (pkColumn != null && fkColumn != null)
        {
          foreignKey.addColumnPair(keySequence, pkColumn, fkColumn);
          foreignKey.setUpdateRule(ForeignKeyUpdateRule.valueOf(updateRule));
          foreignKey.setDeleteRule(ForeignKeyUpdateRule.valueOf(deleteRule));
          foreignKey.setDeferrability(ForeignKeyDeferrability
            .valueOf(deferrability));
          foreignKey.addAttributes(results.getAttributes());

          fkColumn.setReferencedColumn(pkColumn);
          ((MutableTable) pkColumn.getParent()).addForeignKey(foreignKey);
          ((MutableTable) fkColumn.getParent()).addForeignKey(foreignKey);
        }
      }
    }
    finally
    {
      results.close();
    }

  }

  private void createIndices(final MutableTable table,
                             final MetadataResultSet results)
    throws SQLException
  {
    try
    {
      while (results.next())
      {
        // final String catalogName = results.getString("TABLE_CAT");
        // final String schemaName = results.getString("TABLE_SCHEM");
        // final String tableName = results.getQuotedName("TABLE_NAME");
        String indexName = results.getQuotedName("INDEX_NAME");
        if (Utility.isBlank(indexName))
        {
          indexName = UNKNOWN;
        }
        LOGGER.log(Level.FINER, String.format("Retrieving index: %s.%s", table
          .getFullName(), indexName));
        final String columnName = results.getQuotedName("COLUMN_NAME");
        if (Utility.isBlank(columnName))
        {
          continue;
        }

        MutableIndex index = table.getIndex(indexName);
        if (index == null)
        {
          index = new MutableIndex(table, indexName);
          table.addIndex(index);
        }

        final boolean uniqueIndex = !results.getBoolean("NON_UNIQUE");
        final int type = results.getInt("TYPE", IndexType.unknown.getId());
        final int ordinalPosition = results.getInt("ORDINAL_POSITION", 0);
        final String sortSequence = results.getString("ASC_OR_DESC");
        final int cardinality = results.getInt("CARDINALITY", 0);
        final int pages = results.getInt("PAGES", 0);

        final MutableColumn column = table.getColumn(columnName);
        if (column != null)
        {
          column.setPartOfUniqueIndex(uniqueIndex);
          final MutableIndexColumn indexColumn = new MutableIndexColumn(index,
                                                                        column);
          indexColumn.setIndexOrdinalPosition(ordinalPosition);
          indexColumn.setSortSequence(IndexColumnSortSequence
            .valueOfFromCode(sortSequence));
          //
          index.addColumn(indexColumn);
          index.setUnique(uniqueIndex);
          index.setType(IndexType.valueOf(type));
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

  private MutableColumn lookupOrCreateColumn(final MutableTable table,
                                             final String columnName,
                                             final boolean add)
  {
    MutableColumn column = null;
    if (table != null)
    {
      column = table.getColumn(columnName);
    }
    if (column == null)
    {
      column = new MutableColumn(table, columnName);
      if (add)
      {
        LOGGER.log(Level.FINER, String.format("Adding column to table: %s",
                                              column.getFullName()));
        table.addColumn(column);
      }
    }
    return column;
  }

  /**
   * Looks up a column in the database. If the column and table are not
   * found, they are created, and added to the schema. This is prevent
   * foreign key relationships from having a null pointer.
   */
  private MutableColumn lookupOrCreateColumn(final String catalogName,
                                             final String schemaName,
                                             final String tableName,
                                             final String columnName)
  {
    MutableColumn column = null;
    final MutableSchema schema = lookupSchema(catalogName, schemaName);
    if (schema != null)
    {
      MutableTable table = schema.getTable(tableName);
      if (table != null)
      {
        column = table.getColumn(columnName);
      }
      else
      {
        // Create the table, but do not add it to the schema
        table = new MutableTable(schema, tableName);
      }
      if (column == null)
      {
        column = new MutableColumn(table, columnName);
        LOGGER.log(Level.FINER, String
          .format("Adding referenced foreign key column to table: %s", column
            .getFullName()));
        table.addColumn(column);
      }
    }
    return column;
  }

}

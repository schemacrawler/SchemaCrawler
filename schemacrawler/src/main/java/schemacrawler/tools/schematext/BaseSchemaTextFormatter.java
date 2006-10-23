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

package schemacrawler.tools.schematext;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.crawl.SchemaInfoLevel;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.Index;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.tools.util.FormatUtils;

/**
 * Base functionality for the text formatting of schema.
 * 
 * @author sfatehi
 */
public abstract class BaseSchemaTextFormatter
  implements CrawlHandler
{

  private static final Logger LOGGER = Logger.getLogger(BaseSchemaTextFormatter.class
                                                        .getName());
  
  protected final PrintWriter out;
  private final SchemaTextOptions options;

  private int tableCount;

  /**
   * @param writer
   *        Writer to output to.
   */
  BaseSchemaTextFormatter(final SchemaTextOptions options)
    throws SchemaCrawlerException
  {
    if (options == null)
    {
      throw new IllegalArgumentException("Options not provided");
    }
    this.options = options;

    try
    {
      out = options.getOutputOptions().getOutputWriter();
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException("Could not obtain output writer", e);
    }

  }

  /**
   * {@inheritDoc}
   * 
   * @see CrawlHandler#begin()
   */
  public void begin()
    throws SchemaCrawlerException
  {
    // do nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see CrawlHandler#end()
   */
  public void end()
    throws SchemaCrawlerException
  {    
    out.close();
    LOGGER.log(Level.FINER, "Output writer closed");
  }

  /**
   * {@inheritDoc}
   * 
   * @see CrawlHandler#getInfoLevelHint()
   */
  public SchemaInfoLevel getInfoLevelHint()
  {
    return options.getSchemaTextDetailType().mapToInfoLevel();
  }

  final boolean isShowOrdinalNumbers()
  {
    return options.isShowOrdinalNumbers();
  }

  final boolean isShowJdbcColumnTypeNames()
  {
    return options.isShowJdbcColumnTypeNames();
  }

  final boolean isShowIndexNames()
  {
    return !options.isHideIndexNames();
  }

  final SchemaTextDetailType getSchemaTextDetailType()
  {
    return options.getSchemaTextDetailType();
  }

  /**
   * Tables count for tables processed.
   * 
   * @return Table count
   */
  public final int getTableCount()
  {
    return tableCount;
  }

  /**
   * {@inheritDoc}
   * 
   * @see CrawlHandler#handle(DatabaseInfo)
   */
  public void handle(final DatabaseInfo databaseInfo)
  {
    handleDatabaseInfo(databaseInfo);

    final Set propertySet = databaseInfo.getProperties().entrySet();
    if (propertySet.size() > 0)
    {
      handleDatabasePropertiesStart();
      for (final Iterator iter = propertySet.iterator(); iter.hasNext();)
      {
        final Map.Entry property = (Map.Entry) iter.next();
        handleDatabaseProperty((String) property.getKey(), property.getValue()
            .toString());
      }
      handleDatabasePropertiesEnd();
    }

    handleColumnDataTypesStart();
    final ColumnDataType[] columnDataTypes = databaseInfo.getColumnDataTypes();
    for (int i = 0; i < columnDataTypes.length; i++)
    {
      final ColumnDataType columnDataType = columnDataTypes[i];
      handleColumnDataType(columnDataType);
    }
    handleColumnDataTypesEnd();

  }

  String makeDefinedWithString(final ColumnDataType columnDataType)
  {
    String definedWith = "defined with ";
    if (columnDataType.getCreateParameters() == null)
    {
      definedWith = definedWith + "no parameters";
    } else
    {
      definedWith = definedWith + columnDataType.getCreateParameters();
    }
    return definedWith;
  }

  void handleDatabaseInfo(final DatabaseInfo databaseInfo)
  {
    if (!getNoInfo())
    {
      FormatUtils.printDatabaseInfo(databaseInfo, out);
    }
  }

  /**
   * Provides information on the database schema.
   * 
   * @param procedure
   *        Procedure metadata.
   */
  public final void handle(final Procedure procedure)
  {

    handleProcedureStart();

    final String procedureTypeDetail = "procedure, " + procedure.getType();
    handleProcedureName(++tableCount, procedure.getName(), procedureTypeDetail);

    if (options.getSchemaTextDetailType() != SchemaTextDetailType.BRIEF)
    {

      handleStartTableColumns();

      final ProcedureColumn[] columns = procedure.getColumns();
      for (int i = 0; i < columns.length; i++)
      {
        final ProcedureColumn column = columns[i];
        String columnTypeName = column.getType().getDatabaseSpecificTypeName();
        if (options.isShowJdbcColumnTypeNames())
        {
          columnTypeName = column.getType().toString();
        }
        final String columnType = columnTypeName + column.getWidth();
        String procedureColumnType = "";
        if (column.getProcedureColumnType() != null)
        {
          procedureColumnType = column.getProcedureColumnType().toString();
        }

        handleProcedureColumn(column.getOrdinalPosition() + 1,
            column.getName(), columnType, procedureColumnType);
      }

      handleProcedureEnd();
    }

    out.flush();

  }

  /**
   * Provides information on the database schema.
   * 
   * @param table
   *        Table metadata.
   */
  public final void handle(final Table table)
  {

    handleTableStart();
    handleTableName(++tableCount, table.getName(), table.getType().toString());

    final SchemaTextDetailType schemaTextDetailType = options
        .getSchemaTextDetailType();

    if (schemaTextDetailType != SchemaTextDetailType.BRIEF)
    {
      handleStartTableColumns();
      printColumns(table.getColumns());
    }

    if (schemaTextDetailType
        .isGreaterThanOrEqualTo(SchemaTextDetailType.VERBOSE))
    {
      printPrimaryKey(table.getPrimaryKey());
      printForeignKeys(table.getName(), table.getForeignKeys());
      printIndices(table.getIndices());
      if (table instanceof View)
      {
        final View view = (View) table;
        handleDefinition(view.getDefinition());
      }
    }

    handleTableEnd();

    out.flush();

  }

  /**
   * Handles the output for a column.
   * 
   * @param ordinalNumber
   *        Ordinal number for the column
   * @param name
   *        Column name
   * @param type
   *        Column type
   * @param symbol
   *        Symbol
   */
  abstract void handleColumn(final int ordinalNumber, final String name,
      final String type, final String symbol);

  /**
   * Handles the output for a foreign key column pair.
   * 
   * @param pkColumnName
   *        Primary key column name
   * @param fkColumnName
   *        Foreign key column name
   * @param keySequence
   *        Key squence number
   */
  abstract void handleForeignKeyColumnPair(final String pkColumnName,
      final String fkColumnName, final int keySequence);

  /**
   * Handles the output for a foreign key name.
   * 
   * @param ordinalNumber
   *        Ordinal number for the foreign key
   * @param name
   *        Foreign key name
   * @param updateRule
   *        Update rule
   */
  abstract void handleForeignKeyName(final int ordinalNumber,
      final String name, final String updateRule);

  /**
   * Handles the output for a index name.
   * 
   * @param ordinalNumber
   *        Ordinal number for the index
   * @param name
   *        Index name
   * @param type
   *        Index type
   * @param unique
   *        Is the index is unique
   * @param sortSequence
   *        Sort sequence
   */
  abstract void handleIndexName(final int ordinalNumber, final String name,
      final String type, final boolean unique, final String sortSequence);

  /**
   * Handles the output for a primaey key name.
   * 
   * @param name
   *        Primary key name
   */
  abstract void handlePrimaryKeyName(final String name);

  /**
   * Handles the output for a column.
   * 
   * @param ordinalNumber
   *        Ordinal number for the column
   * @param name
   *        Column name
   * @param type
   *        Column type
   * @param procedureColumnType
   *        Procedure column type
   */
  abstract void handleProcedureColumn(final int ordinalNumber,
      final String name, final String type, final String procedureColumnType);

  /**
   * Handles the end of output for a procedure.
   */
  abstract void handleProcedureEnd();

  /**
   * Handles the output for a procedure.
   * 
   * @param ordinalNumber
   *        Ordinal number for the procedure
   * @param name
   *        Procedure name
   * @param type
   *        Procedure type
   */
  abstract void handleProcedureName(final int ordinalNumber, final String name,
      final String type);

  /**
   * Handles the start of output for a procedure.
   */
  abstract void handleProcedureStart();

  /**
   * Handles the start of output for table columns.
   */
  abstract void handleStartTableColumns();

  /**
   * Handles the end of output for a table.
   */
  abstract void handleTableEnd();

  /**
   * Handles the output for a table name.
   * 
   * @param ordinalNumber
   *        Ordinal number for the table
   * @param name
   *        Table name
   * @param type
   *        Table type
   */
  abstract void handleTableName(final int ordinalNumber, final String name,
      final String type);

  /**
   * Handles the start of output for a table.
   */
  abstract void handleTableStart();

  abstract void handleDatabaseProperty(String name, String value);

  abstract void handleDatabasePropertiesEnd();

  abstract void handleDatabasePropertiesStart();

  abstract void handleColumnDataTypesEnd();

  abstract void handleColumnDataType(ColumnDataType columnDataType);

  abstract void handleColumnDataTypesStart();

  boolean getNoFooter()
  {
    return options.getOutputOptions().isNoFooter();
  }

  boolean getNoHeader()
  {
    return options.getOutputOptions().isNoHeader();
  }

  boolean getNoInfo()
  {
    return options.getOutputOptions().isNoInfo();
  }

  /**
   * @param table
   * @param columnPairs
   */
  private void printColumnPairs(final String tableName,
      final ForeignKeyColumnMap[] columnPairs)
  {
    for (int j = 0; j < columnPairs.length; j++)
    {
      final ForeignKeyColumnMap columnPair = columnPairs[j];
      final Column pkColumn;
      final Column fkColumn;
      final String pkColumnName;
      final String fkColumnName;
      pkColumn = columnPair.getPrimaryKeyColumn();
      fkColumn = columnPair.getForeignKeyColumn();
      if (pkColumn.getParent().getName().equals(tableName))
      {
        pkColumnName = pkColumn.getName();
      } else
      {
        pkColumnName = pkColumn.getFullName();
      }
      if (fkColumn.getParent().getName().equals(tableName))
      {
        fkColumnName = fkColumn.getName();
      } else
      {
        fkColumnName = fkColumn.getFullName();
      }
      final int keySequence = columnPair.getKeySequence();
      handleForeignKeyColumnPair(pkColumnName, fkColumnName, keySequence);
    }
  }

  /**
   * @param columns
   */
  private void printColumns(final Column[] columns)
  {
    for (int i = 0; i < columns.length; i++)
    {
      final Column column = columns[i];
      final String columnName = column.getName();
      String columnTypeName = column.getType().getDatabaseSpecificTypeName();
      if (options.isShowJdbcColumnTypeNames())
      {
        columnTypeName = column.getType().toString();
      }
      final String columnType = columnTypeName + column.getWidth();
      String symbol = "";
      if (column.isPartOfPrimaryKey())
      {
        symbol = "primary key";
      } else if (column.isPartOfUniqueIndex())
      {
        symbol = "unique index";
      } else if (!column.isNullable())
      {
        symbol = "not null";
      }

      handleColumn(i + 1, columnName, columnType, symbol);
    }
  }

  private void printForeignKeys(final String tableName,
      final ForeignKey[] foreignKeys)
  {
    for (int i = 0; i < foreignKeys.length; i++)
    {
      final ForeignKey foreignKey = foreignKeys[i];
      if (foreignKey != null)
      {
        final String name = foreignKey.getName();
        final String updateRule = foreignKey.getUpdateRule().toString();
        handleForeignKeyName(i + 1, name, updateRule);
        final ForeignKeyColumnMap[] columnPairs = foreignKey.getColumnPairs();
        printColumnPairs(tableName, columnPairs);
      }
    }
  }

  private void printIndices(final Index[] indices)
  {
    for (int i = 0; i < indices.length; i++)
    {
      final Index index = indices[i];
      if (index != null)
      {
        handleIndexName(i + 1, index.getName(), index.getType().toString(),
            index.isUnique(), index.getSortSequence().toString());
        printColumns(index.getColumns());
      }
    }
  }

  private void printPrimaryKey(final Index primaryKey)
  {
    if (primaryKey != null)
    {
      final String name = primaryKey.getName();
      handlePrimaryKeyName(name);
      printColumns(primaryKey.getColumns());
    }
  }

  protected abstract void handleDefinition(final String definition);

  String negate(final boolean positive, final String text)
  {
    String textValue = text;
    if (!positive)
    {
      textValue = "not " + textValue;
    }
    return textValue;
  }
}

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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.crawl.SchemaInfoLevel;
import schemacrawler.schema.CheckConstraint;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.Index;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.View;
import schemacrawler.tools.util.TextFormattingHelper;
import sf.util.Utilities;

/**
 * Base functionality for the text formatting of schema.
 * 
 * @author sfatehi
 */
public abstract class BaseSchemaTextFormatter
  implements CrawlHandler
{

  private static final Logger LOGGER = Logger
    .getLogger(BaseSchemaTextFormatter.class.getName());

  protected final PrintWriter out;
  private final SchemaTextOptions options;
  protected final TextFormattingHelper formattingHelper;

  private int tableCount;

  /**
   * @param writer
   *        Writer to output to.
   */
  BaseSchemaTextFormatter(final SchemaTextOptions options,
                          final TextFormattingHelper formattingHelper)
    throws SchemaCrawlerException
  {
    if (options == null)
    {
      throw new IllegalArgumentException("Options not provided");
    }
    this.options = options;

    try
    {
      out = options.getOutputOptions().openOutputWriter();
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException("Could not obtain output writer", e);
    }

    this.formattingHelper = formattingHelper;

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
    if (!options.getOutputOptions().isNoInfo())
    {
      handleDatabaseInfo(databaseInfo);
    }

    final Set propertySet = databaseInfo.getProperties().entrySet();
    if (propertySet.size() > 0)
    {
      handleDatabasePropertiesStart();
      for (final Iterator iter = propertySet.iterator(); iter.hasNext();)
      {
        final Map.Entry property = (Map.Entry) iter.next();
        out.println(formattingHelper.createNameValueRow((String) property
          .getKey(), property.getValue().toString()));
      }
      handleDatabasePropertiesEnd();
    }

    handleColumnDataTypesStart();
    final ColumnDataType[] columnDataTypes = databaseInfo.getColumnDataTypes();
    for (int i = 0; i < columnDataTypes.length; i++)
    {
      final ColumnDataType columnDataType = columnDataTypes[i];
      handleColumnDataTypeStart();
      printColumnDataType(columnDataType);
      handleColumnDataTypeEnd();
    }
    handleColumnDataTypesEnd();

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
    out
      .println(formattingHelper.createNameRow(procedure.getName(),
                                              "[" + procedureTypeDetail + "]"));

    final SchemaTextDetailType schemaTextDetailType = options
      .getSchemaTextDetailType();
    if (schemaTextDetailType != SchemaTextDetailType.BRIEF)
    {

      out.println(formattingHelper.createSeparatorRow());

      final ProcedureColumn[] columns = procedure.getColumns();
      for (int i = 0; i < columns.length; i++)
      {
        final ProcedureColumn column = columns[i];
        String columnTypeName = column.getType().getDatabaseSpecificTypeName();
        if (options.isShowJdbcColumnTypeNames())
        {
          columnTypeName = column.getType().toString();
        }
        String columnType = columnTypeName + column.getWidth();
        if (column.getProcedureColumnType() != null)
        {
          columnType = columnType + ", "
                       + column.getProcedureColumnType().toString();
        }

        String ordinalNumberString = "";
        if (options.isShowOrdinalNumbers())
        {
          ordinalNumberString = String.valueOf(column.getOrdinalPosition() + 1);
        }
        out.println(formattingHelper.createDetailRow(ordinalNumberString,
                                                     column.getName(),
                                                     columnType));
      }
      if (schemaTextDetailType
        .isGreaterThanOrEqualTo(SchemaTextDetailType.VERBOSE))
      {
        printDefinition(procedure.getDefinition());
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
    final String typeBracketed = "["
                                 + table.getType().toString()
                                   .toLowerCase(Locale.ENGLISH) + "]";
    out.println(formattingHelper.createNameRow(table.getName(), typeBracketed));

    final SchemaTextDetailType schemaTextDetailType = options
      .getSchemaTextDetailType();

    if (schemaTextDetailType != SchemaTextDetailType.BRIEF)
    {
      out.println(formattingHelper.createSeparatorRow());
      printColumns(table.getColumns());
    }

    if (schemaTextDetailType.isGreaterThanOrEqualTo(SchemaTextDetailType.BASIC))
    {
      printPrimaryKey(table.getPrimaryKey());
    }

    if (schemaTextDetailType
      .isGreaterThanOrEqualTo(SchemaTextDetailType.VERBOSE))
    {
      printForeignKeys(table.getName(), table.getForeignKeys());
      printIndices(table.getIndices());
      printCheckConstraints(table.getCheckConstraints());
      if (schemaTextDetailType
        .isGreaterThanOrEqualTo(SchemaTextDetailType.MAXIMUM))
      {
        printPrivileges(table.getPrivileges());
        printTriggers(table.getTriggers());
      }
      if (table instanceof View)
      {
        final View view = (View) table;
        printDefinition(view.getDefinition());
      }
    }

    handleTableEnd();

    tableCount = tableCount + 1;

    out.flush();

  }

  abstract String getArrow();

  final boolean getNoFooter()
  {
    return options.getOutputOptions().isNoFooter();
  }

  final boolean getNoHeader()
  {
    return options.getOutputOptions().isNoHeader();
  }

  final SchemaTextDetailType getSchemaTextDetailType()
  {
    return options.getSchemaTextDetailType();
  }

  abstract void handleColumnDataTypeEnd();

  abstract void handleColumnDataTypesEnd();

  abstract void handleColumnDataTypesStart();

  abstract void handleColumnDataTypeStart();

  abstract void handleDatabaseInfo(final DatabaseInfo databaseInfo);

  abstract void handleDatabasePropertiesEnd();

  abstract void handleDatabasePropertiesStart();

  /**
   * Handles the end of output for a procedure.
   */
  abstract void handleProcedureEnd();

  /**
   * Handles the start of output for a procedure.
   */
  abstract void handleProcedureStart();

  /**
   * Handles the end of output for a table.
   */
  abstract void handleTableEnd();

  /**
   * Handles the start of output for a table.
   */
  abstract void handleTableStart();

  private String negate(final boolean positive, final String text)
  {
    String textValue = text;
    if (!positive)
    {
      textValue = "not " + textValue;
    }
    return textValue;
  }

  private void printCheckConstraints(final CheckConstraint[] constraints)
  {

    for (int i = 0; i < constraints.length; i++)
    {
      final CheckConstraint constraint = constraints[i];
      if (constraint != null)
      {
        String constraintName = "";
        if (!options.isHideConstraintNames())
        {
          constraintName = constraint.getName();
        }
        out.println(formattingHelper.createEmptyRow());
        out.println(formattingHelper.createNameRow(constraintName,
                                                   "[check constraint]"));
        out.println(formattingHelper.createDefinitionRow(constraint
          .getDefinition()));
      }
    }
  }

  private void printColumnDataType(final ColumnDataType columnDataType)
  {
    final String databaseSpecificTypeName = columnDataType
      .getDatabaseSpecificTypeName();
    final String typeName = columnDataType.getTypeName();
    final String userDefined = negate(columnDataType.isUserDefined(),
                                      "user defined");
    final String nullable = negate(columnDataType.isNullable(), "nullable");
    final String autoIncrementable = negate(columnDataType
      .isAutoIncrementable(), "auto-incrementable");
    String definedWith = "defined with ";
    if (columnDataType.getCreateParameters() == null)
    {
      definedWith = definedWith + "no parameters";
    }
    else
    {
      definedWith = definedWith + columnDataType.getCreateParameters();
    }
    out.println(formattingHelper.createNameRow(databaseSpecificTypeName,
                                               "[data type]"));
    out.println(formattingHelper.createDefinitionRow("based on " + typeName));
    out.println(formattingHelper.createDefinitionRow(userDefined));
    out.println(formattingHelper.createDefinitionRow(definedWith));
    out.println(formattingHelper.createDefinitionRow(nullable));
    out.println(formattingHelper.createDefinitionRow(autoIncrementable));
    out.println(formattingHelper.createDefinitionRow(columnDataType
      .getSearchable().toString()));

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
      }
      else
      {
        pkColumnName = pkColumn.getFullName();
      }
      if (fkColumn.getParent().getName().equals(tableName))
      {
        fkColumnName = fkColumn.getName();
      }
      else
      {
        fkColumnName = fkColumn.getFullName();
      }
      final int keySequence = columnPair.getKeySequence();
      String keySequenceString = "";
      if (options.isShowOrdinalNumbers())
      {
        keySequenceString = Utilities.padLeft(String.valueOf(keySequence), 2);
      }
      out.println(formattingHelper.createDetailRow(keySequenceString,
                                                   pkColumnName + getArrow()
                                                       + fkColumnName,
                                                   ""));
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
      String columnType = columnTypeName + column.getWidth();
      if (!column.isNullable())
      {
        columnType = columnType + ", not null";
      }

      String ordinalNumberString = "";
      if (options.isShowOrdinalNumbers())
      {
        ordinalNumberString = String.valueOf(i + 1);
      }
      out.println(formattingHelper.createDetailRow(ordinalNumberString,
                                                   columnName,
                                                   columnType));
    }
  }

  private void printDefinition(final String definition)
  {
    out.println(formattingHelper.createEmptyRow());

    if (Utilities.isBlank(definition))
    {
      return;
    }
    out.println(formattingHelper.createNameRow("", "[definition]"));
    out.println(formattingHelper.createDefinitionRow(definition));
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
        out.println(formattingHelper.createEmptyRow());

        String fkName = "";
        if (!options.isHideForeignKeyNames())
        {
          fkName = name;
        }
        final String fkDetails = "[foreign key" + ", on update " + updateRule
                                 + "]";
        out.println(formattingHelper.createNameRow(fkName, fkDetails));
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
        out.println(formattingHelper.createEmptyRow());

        String indexName = "";
        if (!options.isHideIndexNames())
        {
          indexName = index.getName();
        }
        final String indexDetails = "[" + (index.isUnique()? "": "non-")
                                    + "unique "
                                    + index.getSortSequence().toString() + " "
                                    + index.getType().toString() + " "
                                    + "index]";
        out.println(formattingHelper.createNameRow(indexName, indexDetails));
        printColumns(index.getColumns());
      }
    }
  }

  private void printPrimaryKey(final Index primaryKey)
  {
    if (primaryKey != null)
    {
      final String name = primaryKey.getName();
      out.println(formattingHelper.createEmptyRow());

      String pkName = "";
      if (!options.isHidePrimaryKeyNames())
      {
        pkName = name;
      }
      out.println(formattingHelper.createNameRow(pkName, "[primary key]"));
      printColumns(primaryKey.getColumns());
    }
  }

  private void printPrivileges(final Privilege[] privileges)
  {

    for (int i = 0; i < privileges.length; i++)
    {
      final Privilege privilege = privileges[i];
      if (privilege != null)
      {
        String privilegeType = "privilege";
        if (privilege.isGrantable())
        {
          privilegeType = "grantable " + privilegeType;
        }
        final String grantedFrom = privilege.getGrantor() + getArrow()
                                   + privilege.getGrantee();
        out.println(formattingHelper.createEmptyRow());

        String privilegeName = privilege.getName();
        final String privilegeDetails = "[" + privilegeType + "]";
        out.println(formattingHelper.createNameRow(privilegeName,
                                                   privilegeDetails));

        out.println(formattingHelper.createDetailRow("", grantedFrom, ""));
      }
    }
  }

  private void printTriggers(final Trigger[] triggers)
  {

    for (int i = 0; i < triggers.length; i++)
    {
      final Trigger trigger = triggers[i];
      if (trigger != null)
      {
        String triggerType = "[trigger, "
                             + trigger.getConditionTiming().getName() + " "
                             + trigger.getEventManipulationType().getName()
                             + ", per " + trigger.getActionOrientation() + "]";
        triggerType = triggerType.toLowerCase();
        final String actionCondition = trigger.getActionCondition();
        final String actionStatement = trigger.getActionStatement();
        out.println(formattingHelper.createEmptyRow());

        String triggerName = trigger.getName();
        out.println(formattingHelper.createNameRow(triggerName, triggerType));

        if (!Utilities.isBlank(actionCondition))
        {
          out.println(formattingHelper.createDefinitionRow(actionCondition));
        }
        if (!Utilities.isBlank(actionStatement))
        {
          out.println(formattingHelper.createDefinitionRow(actionStatement));
        }
      }
    }
  }

}

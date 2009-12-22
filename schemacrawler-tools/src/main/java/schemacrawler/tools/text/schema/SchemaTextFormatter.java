/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.tools.text.schema;


import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ActionOrientationType;
import schemacrawler.schema.CheckConstraint;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnMap;
import schemacrawler.schema.ConditionTimingType;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.ForeignKeyUpdateRule;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.View;
import schemacrawler.schema.Privilege.Grant;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.CrawlHandler;
import schemacrawler.tools.text.BaseFormatter;
import schemacrawler.tools.text.util.TextFormattingHelper.DocumentHeaderType;
import sf.util.Utility;

/**
 * Text formatting of schema.
 * 
 * @author Sualeh Fatehi
 */
final class SchemaTextFormatter
  extends BaseFormatter<SchemaTextOptions>
  implements CrawlHandler
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaTextFormatter.class.getName());

  private int tableCount;
  private int procedureCount;
  private int columnDataTypeCount;

  /**
   * Text formatting of schema.
   * 
   * @param options
   *        Options for text formatting of schema
   */
  SchemaTextFormatter(final SchemaTextOptions options)
    throws SchemaCrawlerException
  {
    super(options);
  }

  /**
   * {@inheritDoc}
   * 
   * @see CrawlHandler#begin()
   */
  public void begin()
    throws SchemaCrawlerException
  {
    if (!options.getOutputOptions().isNoHeader())
    {
      out.println(formattingHelper.createDocumentStart());
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see CrawlHandler#end()
   */
  public void end()
    throws SchemaCrawlerException
  {
    if (options.getSchemaTextDetailType() == SchemaTextDetailType.brief_schema)
    {
      out.print(formattingHelper.createObjectEnd());
    }

    if (!options.getOutputOptions().isNoFooter())
    {
      out.println(formattingHelper.createDocumentEnd());
    }
    out.flush();
    //
    options.getOutputOptions().closeOutputWriter(out);
    LOGGER.log(Level.FINE, "Wrote output, "
                           + options.getOutputOptions().getOutputFile());
  }

  public void handle(final ColumnDataType columnDataType)
    throws SchemaCrawlerException
  {
    final SchemaTextDetailType schemaTextDetailType = options
      .getSchemaTextDetailType();
    if (schemaTextDetailType == SchemaTextDetailType.brief_schema)
    {
      return;
    }

    if (columnDataTypeCount == 0)
    {
      out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                                "Data Types"));
    }

    out.print(formattingHelper.createObjectStart(""));
    printColumnDataType(columnDataType);
    out.print(formattingHelper.createObjectEnd());

    columnDataTypeCount++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.CrawlHandler#handle(schemacrawler.schema.WeakAssociations)
   */
  public void handle(final ColumnMap[] weakAssociations)
    throws SchemaCrawlerException
  {
    if (weakAssociations == null || weakAssociations.length == 0)
    {
      return;
    }
    final SchemaTextDetailType schemaTextDetailType = options
      .getSchemaTextDetailType();
    if (schemaTextDetailType == SchemaTextDetailType.brief_schema)
    {
      return;
    }

    out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                              "Weak Associations"));

    out.print(formattingHelper.createObjectStart(""));
    printColumnPairs("", weakAssociations);
    out.print(formattingHelper.createObjectEnd());
  }

  /**
   * Provides information on the database schema.
   * 
   * @param procedure
   *        Procedure metadata.
   */
  public void handle(final Procedure procedure)
  {

    if (procedureCount == 0)
    {
      out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                                "Procedures"));
    }

    final SchemaTextDetailType schemaTextDetailType = options
      .getSchemaTextDetailType();
    if (schemaTextDetailType != SchemaTextDetailType.brief_schema)
    {
      out.print(formattingHelper.createObjectStart(""));
    }

    final boolean underscore = schemaTextDetailType != SchemaTextDetailType.brief_schema;
    final String procedureTypeDetail = "procedure, " + procedure.getType();
    out.println(formattingHelper.createNameRow(procedure.getFullName(),
                                               "[" + procedureTypeDetail + "]",
                                               underscore));

    if (schemaTextDetailType != SchemaTextDetailType.brief_schema)
    {
      final ProcedureColumn[] columns = procedure.getColumns();
      for (final ProcedureColumn column: columns)
      {
        final String columnTypeName;
        if (options.isShowStandardColumnTypeNames())
        {
          columnTypeName = column.getType().getTypeName();
        }
        else
        {
          columnTypeName = column.getType().getDatabaseSpecificTypeName();
        }
        final StringBuilder columnType = new StringBuilder();
        columnType.append(columnTypeName).append(column.getWidth());
        if (column.getProcedureColumnType() != null)
        {
          columnType.append(", " + column.getProcedureColumnType().toString());
        }

        String ordinalNumberString = "";
        if (options.isShowOrdinalNumbers())
        {
          ordinalNumberString = String.valueOf(column.getOrdinalPosition() + 1);
        }
        out.println(formattingHelper.createDetailRow(ordinalNumberString,
                                                     column.getName(),
                                                     columnType.toString()));
      }
    }
    if (schemaTextDetailType
      .isGreaterThanOrEqualTo(SchemaTextDetailType.verbose_schema))
    {
      printDefinition(procedure.getDefinition());
    }

    if (schemaTextDetailType != SchemaTextDetailType.brief_schema)
    {
      out.println(formattingHelper.createObjectEnd());
    }

    out.flush();

    procedureCount++;

  }

  /**
   * Provides information on the database schema.
   * 
   * @param table
   *        Table metadata.
   */
  public void handle(final Table table)
  {

    if (tableCount == 0)
    {
      out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                                "Tables"));
    }

    final SchemaTextDetailType schemaTextDetailType = options
      .getSchemaTextDetailType();

    final boolean underscore = schemaTextDetailType != SchemaTextDetailType.brief_schema;
    final String nameRow = formattingHelper.createNameRow(table.getFullName(),
                                                          "["
                                                              + table.getType()
                                                                .name() + "]",
                                                          underscore);

    if (schemaTextDetailType != SchemaTextDetailType.brief_schema
        || schemaTextDetailType == SchemaTextDetailType.brief_schema
        && tableCount == 0)
    {
      out.print(formattingHelper.createObjectStart(""));
    }

    out.println(nameRow);

    if (schemaTextDetailType != SchemaTextDetailType.brief_schema)
    {
      printTableColumns(table.getColumns());
    }

    if (schemaTextDetailType
      .isGreaterThanOrEqualTo(SchemaTextDetailType.basic_schema))
    {
      printPrimaryKey(table.getPrimaryKey());
    }

    if (schemaTextDetailType
      .isGreaterThanOrEqualTo(SchemaTextDetailType.verbose_schema))
    {
      printForeignKeys(table.getName(), table.getForeignKeys());
      printIndices(table.getIndices());
      printCheckConstraints(table.getCheckConstraints());
      if (schemaTextDetailType
        .isGreaterThanOrEqualTo(SchemaTextDetailType.maximum_schema))
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

    if (schemaTextDetailType != SchemaTextDetailType.brief_schema)
    {
      out.println(formattingHelper.createObjectEnd());
    }
    out.flush();

    tableCount++;

  }

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

    for (final CheckConstraint constraint: constraints)
    {
      if (constraint != null)
      {
        String constraintName = "";
        if (!options.isHideConstraintNames())
        {
          constraintName = constraint.getName();
        }
        out.println(formattingHelper.createEmptyRow());
        out.println(formattingHelper.createNameRow(constraintName,
                                                   "[check constraint]",
                                                   false));
        out.println(formattingHelper.createDefinitionRow(constraint
          .getDefinition()));
      }
    }
  }

  private void printColumnDataType(final ColumnDataType columnDataType)
  {
    final String databaseSpecificTypeName = columnDataType.getFullName();
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
                                               "[data type]",
                                               false));
    out.println(formattingHelper.createDetailRow("", "based on", typeName));
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
                                final ColumnMap[] columnPairs)
  {
    for (final ColumnMap columnPair: columnPairs)
    {
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
      String keySequenceString = "";
      if (columnPair instanceof ForeignKeyColumnMap
          && options.isShowOrdinalNumbers())
      {
        final int keySequence = ((ForeignKeyColumnMap) columnPair)
          .getKeySequence();
        keySequenceString = String.format("%2d", keySequence);
      }
      out.println(formattingHelper.createDetailRow(keySequenceString,
                                                   pkColumnName
                                                       + formattingHelper
                                                         .createArrow()
                                                       + fkColumnName,
                                                   ""));
    }
  }

  private void printDefinition(final String definition)
  {
    out.println(formattingHelper.createEmptyRow());

    if (sf.util.Utility.isBlank(definition))
    {
      return;
    }
    out.println(formattingHelper.createNameRow("", "[definition]", false));
    out.println(formattingHelper.createDefinitionRow(definition));
  }

  private void printForeignKeys(final String tableName,
                                final ForeignKey[] foreignKeys)
  {
    for (final ForeignKey foreignKey: foreignKeys)
    {
      if (foreignKey != null)
      {
        final String name = foreignKey.getName();

        String updateRuleString = "";
        final ForeignKeyUpdateRule updateRule = foreignKey.getUpdateRule();
        if (updateRule != null && updateRule != ForeignKeyUpdateRule.unknown)
        {
          updateRuleString = ", on update " + updateRule.toString();
        }

        String deleteRuleString = "";
        final ForeignKeyUpdateRule deleteRule = foreignKey.getDeleteRule();
        if (deleteRule != null && deleteRule != ForeignKeyUpdateRule.unknown)
        {
          deleteRuleString = ", on delete " + deleteRule.toString();
        }

        String ruleString = "";
        if (updateRule == deleteRule)
        {
          ruleString = ", with " + deleteRule.toString();
        }
        else
        {
          ruleString = updateRuleString + deleteRuleString;
        }

        out.println(formattingHelper.createEmptyRow());

        String fkName = "";
        if (!options.isHideForeignKeyNames())
        {
          fkName = name;
        }
        final String fkDetails = "[foreign key" + ruleString + "]";
        out.println(formattingHelper.createNameRow(fkName, fkDetails, false));
        final ForeignKeyColumnMap[] columnPairs = foreignKey.getColumnPairs();
        printColumnPairs(tableName, columnPairs);
      }
    }
  }

  private void printIndices(final Index[] indices)
  {
    for (final Index index: indices)
    {
      if (index != null)
      {
        out.println(formattingHelper.createEmptyRow());

        String indexName = "";
        if (!options.isHideIndexNames())
        {
          indexName = index.getName();
        }
        final IndexType indexType = index.getType();
        String indexTypeString = "";
        if (indexType != IndexType.unknown && indexType != IndexType.other)
        {
          indexTypeString = indexType.toString() + " ";
        }
        final String indexDetails = "[" + (index.isUnique()? "": "non-")
                                    + "unique " + indexTypeString + "index]";
        out.println(formattingHelper.createNameRow(indexName,
                                                   indexDetails,
                                                   false));
        printTableColumns(index.getColumns());
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
      if (Utility.isBlank(pkName))
      {
        pkName = "";
      }
      out.println(formattingHelper
        .createNameRow(pkName, "[primary key]", false));
      printTableColumns(primaryKey.getColumns());
    }
  }

  private void printPrivileges(final Privilege[] privileges)
  {

    for (final Privilege privilege: privileges)
    {
      if (privilege != null)
      {
        out.println(formattingHelper.createEmptyRow());
        out.println(formattingHelper.createNameRow(privilege.getName(),
                                                   "[privilege]",
                                                   false));
        for (final Grant grant: privilege.getGrants())
        {
          final String grantedFrom = grant.getGrantor()
                                     + formattingHelper.createArrow()
                                     + grant.getGrantee()
                                     + (grant.isGrantable()? " (grantable)": "");
          out.println(formattingHelper.createDetailRow("", grantedFrom, ""));
        }
      }
    }
  }

  private void printTableColumns(final Column[] columns)
  {
    for (final Column column: columns)
    {
      final String columnName = column.getName();

      final String columnDetails;
      if (column instanceof IndexColumn)
      {
        columnDetails = ((IndexColumn) column).getSortSequence().name();
      }
      else
      {
        String columnTypeName = column.getType().getDatabaseSpecificTypeName();
        if (options.isShowStandardColumnTypeNames())
        {
          columnTypeName = column.getType().getTypeName();
        }
        final String columnType = columnTypeName + column.getWidth();
        final String nullable = column.isNullable()? "": " not null";
        columnDetails = columnType + nullable;
      }

      String ordinalNumberString = "";
      if (options.isShowOrdinalNumbers())
      {
        ordinalNumberString = String.valueOf(column.getOrdinalPosition());
      }
      out.println(formattingHelper.createDetailRow(ordinalNumberString,
                                                   columnName,
                                                   columnDetails));
    }
  }

  private void printTriggers(final Trigger[] triggers)
  {
    for (final Trigger trigger: triggers)
    {
      if (trigger != null)
      {
        String timing = "";
        final ConditionTimingType conditionTiming = trigger
          .getConditionTiming();
        final EventManipulationType eventManipulationType = trigger
          .getEventManipulationType();
        if (conditionTiming != null
            && conditionTiming != ConditionTimingType.unknown
            && eventManipulationType != null
            && eventManipulationType != EventManipulationType.unknown)
        {
          timing = ", " + conditionTiming + " " + eventManipulationType;
        }
        String orientation = "";
        if (trigger.getActionOrientation() != null
            && trigger.getActionOrientation() != ActionOrientationType.unknown)
        {
          orientation = ", per " + trigger.getActionOrientation();
        }
        String triggerType = "[trigger" + timing + orientation + "]";
        triggerType = triggerType.toLowerCase(Locale.ENGLISH);
        final String actionCondition = trigger.getActionCondition();
        final String actionStatement = trigger.getActionStatement();
        out.println(formattingHelper.createEmptyRow());

        final String triggerName = trigger.getName();
        out.println(formattingHelper.createNameRow(triggerName,
                                                   triggerType,
                                                   false));

        if (!sf.util.Utility.isBlank(actionCondition))
        {
          out.println(formattingHelper.createDefinitionRow(actionCondition));
        }
        if (!sf.util.Utility.isBlank(actionStatement))
        {
          out.println(formattingHelper.createDefinitionRow(actionStatement));
        }
      }
    }
  }

}

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

package schemacrawler.tools.text.schema;


import java.util.Locale;

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
import schemacrawler.tools.analysis.AnalyzedDatabase;
import schemacrawler.tools.analysis.Lint;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.BaseFormatter;
import schemacrawler.tools.text.util.TextFormattingHelper.DocumentHeaderType;
import sf.util.Utility;

/**
 * Text formatting of schema.
 * 
 * @author Sualeh Fatehi
 */
final class SchemaTextFormatter
  extends BaseFormatter<SchemaTextOptions>
{

  private static String negate(final boolean positive, final String text)
  {
    String textValue = text;
    if (!positive)
    {
      textValue = "not " + textValue;
    }
    return textValue;
  }

  private final SchemaTextDetailType schemaTextDetailType;

  /**
   * Text formatting of schema.
   * 
   * @param schemaTextDetailType
   *        Types for text formatting of schema
   * @param options
   *        Options for text formatting of schema
   * @param outputOptions
   *        Options for text formatting of schema
   * @throws SchemaCrawlerException
   *         On an exception
   */
  SchemaTextFormatter(final SchemaTextDetailType schemaTextDetailType,
                      final SchemaTextOptions options,
                      final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    super(options,
          schemaTextDetailType == SchemaTextDetailType.verbose_schema,
          outputOptions);
    this.schemaTextDetailType = schemaTextDetailType;
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
        final String definition = constraint.getDefinition();
        if (!Utility.isBlank(definition))
        {
          out.println(formattingHelper.createDefinitionRow(definition));
        }
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

  private void printColumnPairs(final String tableName,
                                final ColumnMap... columnPairs)
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

        final String ruleString;
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

  private void printLint(final Table table)
  {
    final Lint[] lints = AnalyzedDatabase.getLint(table);
    if (lints != null && lints.length > 0)
    {
      out.println(formattingHelper.createEmptyRow());
      out.println(formattingHelper.createNameRow("", "[lint]", false));
      for (final Lint lint: lints)
      {
        final Object lintValue = lint.getLintValue();
        if (lintValue instanceof Boolean)
        {
          if ((Boolean) lintValue)
          {
            out.println(formattingHelper.createDefinitionRow(lint
              .getDescription()));
          }
        }
        else
        {
          out.println(formattingHelper
            .createDefinitionRow(lint.getDescription() + Utility.NEWLINE
                                 + lint.getLintValueAsString()));
        }
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

  private void printProcedureColumns(final ProcedureColumn[] columns)
  {
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
        columnType.append(", ").append(column.getProcedureColumnType()
          .toString());
      }

      String ordinalNumberString = "";
      if (options.isShowOrdinalNumbers())
      {
        ordinalNumberString = String.valueOf(column.getOrdinalPosition() + 1);
      }
      out.println(formattingHelper.createDetailRow(ordinalNumberString, column
        .getName(), columnType.toString()));
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

  private void printText(final String heading, final String text)
  {
    if (sf.util.Utility.isBlank(text))
    {
      return;
    }
    out.println(formattingHelper.createEmptyRow());
    out.println(formattingHelper.createNameRow("", "[" + heading + "]", false));
    out.println(formattingHelper.createDefinitionRow(text));
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

  private void printWeakAssociations(final Table table)
  {
    final String tableName = table.getName();
    final ColumnMap[] weakAssociations = AnalyzedDatabase
      .getWeakAssociations(table);
    for (final ColumnMap weakAssociation: weakAssociations)
    {
      out.println(formattingHelper.createEmptyRow());
      out.println(formattingHelper.createNameRow("",
                                                 "[weak association]",
                                                 false));
      printColumnPairs(tableName, weakAssociation);
    }
  }

  void begin()
    throws SchemaCrawlerException
  {
    if (!outputOptions.isNoHeader())
    {
      out.println(formattingHelper.createDocumentStart());
    }
  }

  void end()
    throws SchemaCrawlerException
  {
    if (!outputOptions.isNoFooter())
    {
      out.println(formattingHelper.createDocumentEnd());
    }
    out.flush();
    //
    outputOptions.closeOutputWriter(out);
  }

  void handle(final ColumnDataType columnDataType)
    throws SchemaCrawlerException
  {
    if (schemaTextDetailType
      .isGreaterThanOrEqualTo(SchemaTextDetailType.verbose_schema))
    {
      out.print(formattingHelper.createObjectStart(""));
      printColumnDataType(columnDataType);
      out.print(formattingHelper.createObjectEnd());
    }
  }

  /**
   * Provides information on the database schema.
   * 
   * @param procedure
   *        Procedure metadata.
   */
  void handle(final Procedure procedure)
  {
    final boolean underscore = schemaTextDetailType != SchemaTextDetailType.list_objects;
    final String procedureTypeDetail = "procedure, " + procedure.getType();
    final String nameRow = formattingHelper.createNameRow(procedure
      .getFullName(), "[" + procedureTypeDetail + "]", underscore);

    if (schemaTextDetailType != SchemaTextDetailType.list_objects)
    {
      out.print(formattingHelper.createObjectStart(""));
    }

    out.println(nameRow);

    if (schemaTextDetailType != SchemaTextDetailType.list_objects)
    {
      printProcedureColumns(procedure.getColumns());
      printText("definition", procedure.getDefinition());

      if (schemaTextDetailType
        .isGreaterThanOrEqualTo(SchemaTextDetailType.verbose_schema))
      {
        printText("remarks", procedure.getRemarks());
      }

      out.println(formattingHelper.createObjectEnd());
    }

    out.flush();

  }

  /**
   * Provides information on the database schema.
   * 
   * @param table
   *        Table metadata.
   */
  void handle(final Table table)
  {
    final boolean underscore = schemaTextDetailType != SchemaTextDetailType.list_objects;
    final String nameRow = formattingHelper.createNameRow(table.getFullName(),
                                                          "[" + table.getType()
                                                              + "]",
                                                          underscore);

    if (schemaTextDetailType != SchemaTextDetailType.list_objects)
    {
      out.print(formattingHelper.createObjectStart(""));
    }

    out.println(nameRow);

    if (schemaTextDetailType != SchemaTextDetailType.list_objects)
    {
      printTableColumns(table.getColumns());

      printPrimaryKey(table.getPrimaryKey());
      printForeignKeys(table.getName(), table.getForeignKeys());
      if (schemaTextDetailType
        .isGreaterThanOrEqualTo(SchemaTextDetailType.verbose_schema))
      {
        printWeakAssociations(table);
      }
      printIndices(table.getIndices());
      if (schemaTextDetailType
        .isGreaterThanOrEqualTo(SchemaTextDetailType.verbose_schema))
      {
        printCheckConstraints(table.getCheckConstraints());
        printPrivileges(table.getPrivileges());
        printTriggers(table.getTriggers());
      }
      if (table instanceof View)
      {
        final View view = (View) table;
        printText("definition", view.getDefinition());
      }
      if (schemaTextDetailType
        .isGreaterThanOrEqualTo(SchemaTextDetailType.verbose_schema))
      {
        printText("remarks", table.getRemarks());
        printLint(table);
      }
      out.println(formattingHelper.createObjectEnd());
    }
    out.flush();
  }

  void handleColumnDataTypesEnd()
  {
  }

  void handleColumnDataTypesStart()
  {
    if (schemaTextDetailType
      .isGreaterThanOrEqualTo(SchemaTextDetailType.verbose_schema))
    {
      out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                                "Data Types"));
    }
  }

  void handleProceduresEnd()
    throws SchemaCrawlerException
  {
    if (schemaTextDetailType == SchemaTextDetailType.list_objects)
    {
      out.print(formattingHelper.createObjectEnd());
    }
  }

  void handleProceduresStart()
    throws SchemaCrawlerException
  {
    out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                              "Procedures"));

    if (schemaTextDetailType == SchemaTextDetailType.list_objects)
    {
      out.print(formattingHelper.createObjectStart(""));
    }
  }

  void handleTablesEnd()
    throws SchemaCrawlerException
  {
    if (schemaTextDetailType == SchemaTextDetailType.list_objects)
    {
      out.print(formattingHelper.createObjectEnd());
    }
  }

  void handleTablesStart()
    throws SchemaCrawlerException
  {
    out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                              "Tables"));

    if (schemaTextDetailType == SchemaTextDetailType.list_objects)
    {
      out.print(formattingHelper.createObjectStart(""));
    }
  }

}

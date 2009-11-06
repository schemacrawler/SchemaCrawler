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

package schemacrawler.tools.schematext;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ActionOrientationType;
import schemacrawler.schema.CheckConstraint;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnMap;
import schemacrawler.schema.ConditionTimingType;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.ForeignKeyUpdateRule;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.JdbcDriverProperty;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.View;
import schemacrawler.schema.Privilege.Grant;
import schemacrawler.schemacrawler.CrawlHandler;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.OutputFormat;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.util.HtmlFormattingHelper;
import schemacrawler.tools.util.PlainTextFormattingHelper;
import schemacrawler.tools.util.TextFormattingHelper;
import schemacrawler.tools.util.TextFormattingHelper.DocumentHeaderType;
import schemacrawler.utility.ObjectToString;
import schemacrawler.utility.Utility;

/**
 * Text formatting of schema.
 * 
 * @author Sualeh Fatehi
 */
final class SchemaTextFormatter
  implements CrawlHandler
{

  private enum CrawlPhase
  {
    none, databaseInfo, columnDataTypes, tables, procedures, weakAssociations,
  }

  private static final Logger LOGGER = Logger
    .getLogger(SchemaTextFormatter.class.getName());

  private final SchemaTextOptions options;
  private final PrintWriter out;
  private final TextFormattingHelper formattingHelper;
  private CrawlPhase crawlPhase;
  private int tableCount;

  /**
   * Text formatting of schema.
   * 
   * @param options
   *        Options for text formatting of schema
   */
  SchemaTextFormatter(final SchemaTextOptions options)
    throws SchemaCrawlerException
  {
    if (options == null)
    {
      throw new IllegalArgumentException("Options not provided");
    }
    this.options = options;

    final OutputOptions outputOptions = options.getOutputOptions();
    final OutputFormat outputFormat = outputOptions.getOutputFormat();
    if (outputFormat == OutputFormat.html)
    {
      formattingHelper = new HtmlFormattingHelper(outputFormat);
    }
    else
    {
      formattingHelper = new PlainTextFormattingHelper(outputFormat);
    }

    try
    {
      out = outputOptions.openOutputWriter();
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException("Could not obtain output writer", e);
    }

    crawlPhase = CrawlPhase.none;
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
    if (crawlPhase != CrawlPhase.columnDataTypes)
    {
      out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                                "Data Types"));
      crawlPhase = CrawlPhase.columnDataTypes;
    }

    out.print(formattingHelper.createObjectStart(""));
    printColumnDataType(columnDataType);
    out.print(formattingHelper.createObjectEnd());
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
    if (crawlPhase != CrawlPhase.weakAssociations)
    {
      out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                                "Weak Associations"));
      crawlPhase = CrawlPhase.weakAssociations;
    }

    out.print(formattingHelper.createObjectStart(""));
    out
      .println(formattingHelper.createNameRow("", "[weak associations]", true));
    printColumnPairs("", weakAssociations);
    out.print(formattingHelper.createObjectEnd());
  }

  /**
   * {@inheritDoc}
   * 
   * @see CrawlHandler#handle(Database)
   */
  public void handle(final DatabaseInfo databaseInfo)
  {
    if (crawlPhase != CrawlPhase.databaseInfo)
    {
      crawlPhase = CrawlPhase.databaseInfo;
    }

    printDatabaseInfo(databaseInfo);

  }

  /**
   * Provides information on the database schema.
   * 
   * @param procedure
   *        Procedure metadata.
   */
  public void handle(final Procedure procedure)
  {

    if (crawlPhase != CrawlPhase.procedures)
    {
      out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                                "Procedures"));
      crawlPhase = CrawlPhase.procedures;
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

  }

  /**
   * Provides information on the database schema.
   * 
   * @param table
   *        Table metadata.
   */
  public void handle(final Table table)
  {

    if (crawlPhase != CrawlPhase.tables)
    {
      out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                                "Tables"));
      crawlPhase = CrawlPhase.tables;
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

    tableCount = tableCount + 1;
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

  private void printDatabaseInfo(final DatabaseInfo dbInfo)
  {
    if (dbInfo == null)
    {
      return;
    }

    final SchemaTextDetailType schemaTextDetailType = options
      .getSchemaTextDetailType();

    out.println(formattingHelper
      .createHeader(DocumentHeaderType.subTitle,
                    "Database and JDBC Driver Information"));

    out.println(formattingHelper.createHeader(DocumentHeaderType.section,
                                              "Database Information"));

    out.print(formattingHelper.createObjectStart(""));
    out.println(formattingHelper.createNameValueRow("database", dbInfo
      .getProductName()));
    out.println(formattingHelper.createNameValueRow("database version", dbInfo
      .getProductVersion()));
    out.print(formattingHelper.createObjectEnd());

    if (schemaTextDetailType == SchemaTextDetailType.maximum_schema)
    {
      out.println(formattingHelper.createHeader(DocumentHeaderType.section,
                                                "Database Characteristics"));
      final Set<Map.Entry<String, Object>> propertySet = dbInfo.getProperties()
        .entrySet();
      if (propertySet.size() > 0)
      {
        out.print(formattingHelper.createObjectStart(""));
        for (final Map.Entry<String, Object> property: propertySet)
        {
          final String key = property.getKey();
          Object value = property.getValue();
          if (value == null)
          {
            value = "";
          }
          out.println(formattingHelper.createNameValueRow(key, ObjectToString
            .toString(value)));
        }
        out.print(formattingHelper.createObjectEnd());
      }
    }

    final JdbcDriverInfo driverInfo = dbInfo.getJdbcDriverInfo();
    if (driverInfo == null)
    {
      return;
    }

    out.println(formattingHelper.createHeader(DocumentHeaderType.section,
                                              "JDBC Driver Information"));

    out.print(formattingHelper.createObjectStart(""));
    out.println(formattingHelper.createNameValueRow("driver", driverInfo
      .getDriverName()));
    out.println(formattingHelper.createNameValueRow("driver version",
                                                    driverInfo
                                                      .getDriverVersion()));
    out.println(formattingHelper.createNameValueRow("is JDBC compliant",
                                                    Boolean.toString(driverInfo
                                                      .isJdbcCompliant())));
    out.println(formattingHelper.createNameValueRow("url", driverInfo
      .getConnectionUrl()));
    out.print(formattingHelper.createObjectEnd());

    if (schemaTextDetailType == SchemaTextDetailType.maximum_schema)
    {
      out.println(formattingHelper.createHeader(DocumentHeaderType.section,
                                                "JDBC Driver Properties"));

      final JdbcDriverProperty[] jdbcDriverProperties = driverInfo
        .getDriverProperties();
      if (jdbcDriverProperties.length > 0)
      {
        for (final JdbcDriverProperty driverProperty: jdbcDriverProperties)
        {
          out.print(formattingHelper.createObjectStart(""));
          printJdbcDriverProperty(driverProperty);
          out.print(formattingHelper.createObjectEnd());
        }
      }
    }

    out.flush();
  }

  private void printDefinition(final String definition)
  {
    out.println(formattingHelper.createEmptyRow());

    if (schemacrawler.utility.Utility.isBlank(definition))
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

  private void printJdbcDriverProperty(final JdbcDriverProperty driverProperty)
  {
    final String choices = Arrays.asList(driverProperty.getChoices())
      .toString();
    final String required = (driverProperty.isRequired()? "": "not ")
                            + "required";
    String details = required;
    if (driverProperty.getChoices() != null
        && driverProperty.getChoices().length > 0)
    {
      details = details + "; choices " + choices;
    }
    final String value = driverProperty.getValue();

    out.println(formattingHelper.createNameRow(driverProperty.getName(),
                                               "[driver property]",
                                               false));
    out.println(formattingHelper.createDefinitionRow(driverProperty
      .getDescription()));
    out.println(formattingHelper.createDefinitionRow(details));
    out.println(formattingHelper.createDetailRow("", "value", value));
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
    for (int i = 0; i < columns.length; i++)
    {
      final Column column = columns[i];
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
        ordinalNumberString = String.valueOf(i + 1);
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

        if (!schemacrawler.utility.Utility.isBlank(actionCondition))
        {
          out.println(formattingHelper.createDefinitionRow(actionCondition));
        }
        if (!schemacrawler.utility.Utility.isBlank(actionStatement))
        {
          out.println(formattingHelper.createDefinitionRow(actionStatement));
        }
      }
    }
  }

}

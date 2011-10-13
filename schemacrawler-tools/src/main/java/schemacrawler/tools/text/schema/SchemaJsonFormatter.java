/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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


import java.util.logging.Level;


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
import schemacrawler.schema.Privilege.Grant;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.analysis.AnalyzedDatabase;
import schemacrawler.tools.analysis.Lint;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.BaseJsonFormatter;
import schemacrawler.utililty.org.json.JSONArray;
import schemacrawler.utililty.org.json.JSONException;
import schemacrawler.utililty.org.json.JSONObject;

/**
 * Text formatting of schema.
 * 
 * @author Sualeh Fatehi
 */
final class SchemaJsonFormatter
  extends BaseJsonFormatter<SchemaTextOptions>
  implements SchemaFormatter
{

  private final boolean isVerbose;
  private final boolean isNotList;

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
  SchemaJsonFormatter(final SchemaTextDetailType schemaTextDetailType,
                      final SchemaTextOptions options,
                      final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    super(options,
          schemaTextDetailType == SchemaTextDetailType.details,
          outputOptions);
    isVerbose = schemaTextDetailType
      .isGreaterThanOrEqualTo(SchemaTextDetailType.details);
    isNotList = schemaTextDetailType != SchemaTextDetailType.list;
  }

  public void handle(final ColumnDataType columnDataType)
    throws SchemaCrawlerException
  {
    if (printVerboseDatabaseInfo && isVerbose)
    {
      try
      {
        final JSONObject jsonColumnDataType = new JSONObject();
        jsonDatabase.accumulate("columnDataypes", jsonColumnDataType);

        jsonColumnDataType.put("databaseSpecificTypeName",
                               columnDataType.getDatabaseSpecificTypeName());
        // jsonColumnDataType.put("basedOn",
        // columnDataType.getBaseType()
        // .getName());
        jsonColumnDataType.put("userDefined", columnDataType.isUserDefined());
        jsonColumnDataType.put("createParameters",
                               columnDataType.getCreateParameters());
        jsonColumnDataType.put("nullable", columnDataType.isNullable());
        jsonColumnDataType.put("autoIncrementable",
                               columnDataType.isAutoIncrementable());
        jsonColumnDataType.put("searchable", columnDataType.getSearchable()
          .toString());
      }
      catch (final JSONException e)
      {
        LOGGER.log(Level.FINER,
                   "Error outputting ColumnDataType: " + e.getMessage(),
                   e);
      }
    }
  }

  /**
   * Provides information on the database schema.
   * 
   * @param procedure
   *        Procedure metadata.
   */
  public void handle(final Procedure procedure)
  {
    try
    {
      final JSONObject jsonProcedure = new JSONObject();
      jsonDatabase.accumulate("procedures", jsonProcedure);

      jsonProcedure.put("name", procedure.getFullName());
      jsonProcedure.put("procedure", procedure.getType());

      if (isNotList)
      {
        final JSONArray jsonParameters = new JSONArray();
        jsonProcedure.put("parameters", jsonParameters);
        for (final ProcedureColumn column: procedure.getColumns())
        {
          jsonParameters.put(handleProcedureColumn(column));
        }

        jsonProcedure.put("definition", procedure.getDefinition());

        if (isVerbose)
        {
          jsonProcedure.put("remarks", procedure.getRemarks());
        }
      }
    }
    catch (final JSONException e)
    {
      LOGGER.log(Level.FINER,
                 "Error outputting Procedure: " + e.getMessage(),
                 e);
    }

  }

  /**
   * Provides information on the database schema.
   * 
   * @param table
   *        Table metadata.
   */
  public void handle(final Table table)
  {
    final JSONObject jsonTable = new JSONObject();

    try
    {
      jsonDatabase.accumulate("tables", jsonTable);

      jsonTable.put("name", table.getFullName());
      jsonTable.put("table", table.getType());

      if (isNotList)
      {
        final JSONArray jsonColumns = new JSONArray();
        jsonTable.put("columns", jsonColumns);
        for (final Column column: table.getColumns())
        {
          jsonColumns.put(handleTableColumn(column));
        }

        jsonTable.put("primaryKey", handleIndex(table.getPrimaryKey()));
        jsonTable.put("foreignKeys", handleForeignKeys(table.getForeignKeys()));

        if (isVerbose)
        {
          final ColumnMap[] weakAssociations = AnalyzedDatabase
            .getWeakAssociations(table);
          jsonTable
            .put("weakAssociations", handleColumnPairs(weakAssociations));
        }

        final JSONArray jsonIndices = new JSONArray();
        jsonTable.put("indices", jsonIndices);
        for (final Index index: table.getIndices())
        {
          jsonIndices.put(handleIndex(index));
        }

        if (isVerbose)
        {
          for (final CheckConstraint constraint: table.getCheckConstraints())
          {
            if (constraint != null)
            {
              final JSONObject jsonConsraint = new JSONObject();
              jsonTable.accumulate("constraints", jsonConsraint);
              jsonConsraint.put("name", constraint.getName());
              jsonConsraint.put("definition", constraint.getDefinition());
            }
          }

          for (final Privilege privilege: table.getPrivileges())
          {
            if (privilege != null)
            {
              final JSONObject jsonPrivilege = new JSONObject();
              jsonTable.accumulate("privileges", jsonPrivilege);
              jsonPrivilege.put("name", privilege.getName());
              for (final Grant grant: privilege.getGrants())
              {
                final JSONObject jsonGrant = new JSONObject();
                jsonPrivilege.accumulate("grants", jsonGrant);

                jsonGrant.put("grantor", grant.getGrantor());
                jsonGrant.put("grantee", grant.getGrantee());
                jsonGrant.put("grantable", grant.isGrantable());
              }
            }
          }

          final Trigger[] triggers = table.getTriggers();
          jsonTable.put("triggers", handleTriggers(triggers));
        }
        if (table instanceof View)
        {
          final View view = (View) table;
          jsonTable.put("definition", view.getDefinition());
        }
        if (isVerbose)
        {
          jsonTable.put("remarks", table.getRemarks());
          final Lint[] lints = AnalyzedDatabase.getLint(table);
          jsonTable.put("lints", handleLint(lints));
        }
      }
    }
    catch (final JSONException e)
    {
      LOGGER.log(Level.FINER, "Error outputting Table: " + e.getMessage(), e);
    }
  }

  public void handleColumnDataTypesEnd()
  {
  }

  public void handleColumnDataTypesStart()
  {
  }

  public void handleProceduresEnd()
    throws SchemaCrawlerException
  {
  }

  public void handleProceduresStart()
    throws SchemaCrawlerException
  {
  }

  public void handleTablesEnd()
    throws SchemaCrawlerException
  {
  }

  public void handleTablesStart()
    throws SchemaCrawlerException
  {
  }

  private JSONArray handleColumnPairs(final ColumnMap... columnPairs)
  {
    final JSONArray jsonColumnPairs = new JSONArray();
    for (final ColumnMap columnPair: columnPairs)
    {
      try
      {
        final JSONObject jsonColumnPair = new JSONObject();
        jsonColumnPair.put("pkColumn", columnPair.getPrimaryKeyColumn());
        jsonColumnPair.put("fkColumn", columnPair.getForeignKeyColumn());
        if (columnPair instanceof ForeignKeyColumnMap
            && options.isShowOrdinalNumbers())
        {
          final int keySequence = ((ForeignKeyColumnMap) columnPair)
            .getKeySequence();
          jsonColumnPair.put("keySequence", keySequence);
        }
        jsonColumnPairs.put(jsonColumnPair);
      }
      catch (final JSONException e)
      {
        LOGGER.log(Level.FINER,
                   "Error outputting ColumnMap: " + e.getMessage(),
                   e);
      }
    }
    return jsonColumnPairs;
  }

  private JSONArray handleForeignKeys(final ForeignKey[] foreignKeys)
  {
    final JSONArray jsonFks = new JSONArray();

    for (final ForeignKey foreignKey: foreignKeys)
    {
      if (foreignKey != null)
      {
        try
        {
          final JSONObject jsonFk = new JSONObject();
          jsonFks.put(jsonFk);
          jsonFk.put("name", foreignKey.getName());

          final ForeignKeyUpdateRule updateRule = foreignKey.getUpdateRule();
          if (updateRule != null && updateRule != ForeignKeyUpdateRule.unknown)
          {
            jsonFk.put("updateRule", updateRule.toString());
          }

          final ForeignKeyUpdateRule deleteRule = foreignKey.getDeleteRule();
          if (deleteRule != null && deleteRule != ForeignKeyUpdateRule.unknown)
          {
            jsonFk.put("deleteRule", deleteRule.toString());
          }

          final ForeignKeyColumnMap[] columnPairs = foreignKey.getColumnPairs();
          jsonFk.put("columnPairs", handleColumnPairs(columnPairs));
        }
        catch (final JSONException e)
        {
          LOGGER.log(Level.FINER,
                     "Error outputting ForeignKey: " + e.getMessage(),
                     e);
        }
      }
    }

    return jsonFks;
  }

  private JSONObject handleIndex(final Index index)
  {

    final JSONObject jsonIndex = new JSONObject();

    if (index == null)
    {
      return jsonIndex;
    }

    try
    {
      jsonIndex.put("name", index.getName());
      final IndexType indexType = index.getType();
      if (indexType != IndexType.unknown && indexType != IndexType.other)
      {
        jsonIndex.put("type", indexType.toString());
      }
      jsonIndex.put("unique", index.isUnique());

      final IndexColumn[] columns = index.getColumns();
      for (final IndexColumn indexColumn: columns)
      {
        jsonIndex.accumulate("columns", handleTableColumn(indexColumn));
      }
    }
    catch (final JSONException e)
    {
      LOGGER.log(Level.FINER, "Error outputting Index: " + e.getMessage(), e);
    }

    return jsonIndex;
  }

  private JSONArray handleLint(final Lint[] lints)
  {
    final JSONArray jsonLints = new JSONArray();
    if (lints != null && lints.length > 0)
    {
      for (final Lint lint: lints)
      {
        try
        {
          final JSONObject jsonLint = new JSONObject();
          jsonLints.put(jsonLint);
          jsonLint.put("description", lint.getDescription());
          jsonLint.put("value", lint.getLintValue());
        }
        catch (final JSONException e)
        {
          LOGGER
            .log(Level.FINER, "Error outputting Lint: " + e.getMessage(), e);
        }
      }
    }
    return jsonLints;
  }

  private JSONObject handleProcedureColumn(final ProcedureColumn column)
  {
    final JSONObject jsonColumn = new JSONObject();

    try
    {
      jsonColumn.put("dataType", column.getType().getTypeName());
      jsonColumn.put("databaseSpecificType", column.getType()
        .getDatabaseSpecificTypeName());
      jsonColumn.put("width", column.getWidth());
      jsonColumn.put("type", column.getProcedureColumnType().toString());
      jsonColumn.put("ordinal", column.getOrdinalPosition() + 1);
    }
    catch (final JSONException e)
    {
      LOGGER.log(Level.FINER,
                 "Error outputting ProcedureColumn: " + e.getMessage(),
                 e);
    }

    return jsonColumn;
  }

  private JSONObject handleTableColumn(final Column column)
  {
    final JSONObject jsonColumn = new JSONObject();
    try
    {
      jsonColumn.put("name", column.getName());

      if (column instanceof IndexColumn)
      {
        jsonColumn.put("sortSequence", ((IndexColumn) column).getSortSequence()
          .name());
      }
      else
      {
        jsonColumn.put("dataType", column.getType().getTypeName());
        jsonColumn.put("databaseSpecificType", column.getType()
          .getDatabaseSpecificTypeName());
        jsonColumn.put("width", column.getWidth());
        jsonColumn.put("nullable", column.isNullable());
      }

      jsonColumn.put("ordinal", column.getOrdinalPosition());
      if (isVerbose)
      {
        jsonColumn.put("remarks", column.getRemarks());
      }
    }
    catch (final JSONException e)
    {
      LOGGER.log(Level.FINER, "Error outputting Column: " + e.getMessage(), e);
    }

    return jsonColumn;
  }

  private JSONArray handleTriggers(final Trigger[] triggers)
  {
    final JSONArray jsonTriggers = new JSONArray();
    for (final Trigger trigger: triggers)
    {
      if (trigger != null)
      {
        try
        {
          final JSONObject jsonTrigger = new JSONObject();
          jsonTriggers.put(jsonTrigger);

          final ConditionTimingType conditionTiming = trigger
            .getConditionTiming();
          final EventManipulationType eventManipulationType = trigger
            .getEventManipulationType();
          if (conditionTiming != null
              && conditionTiming != ConditionTimingType.unknown
              && eventManipulationType != null
              && eventManipulationType != EventManipulationType.unknown)
          {
            jsonTrigger.put("conditionTiming", conditionTiming);
            jsonTrigger.put("eventManipulationType", eventManipulationType);
          }
          jsonTrigger.put("actionOrientation", trigger.getActionOrientation());
          jsonTrigger.put("actionCondition", trigger.getActionCondition());
          jsonTrigger.put("actionStatement", trigger.getActionStatement());
        }
        catch (final JSONException e)
        {
          LOGGER.log(Level.FINER,
                     "Error outputting Trigger: " + e.getMessage(),
                     e);
        }
      }
    }
    return jsonTriggers;
  }

}

/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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


import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import schemacrawler.schema.CheckConstraint;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ConditionTimingType;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
import schemacrawler.schema.ForeignKeyUpdateRule;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Privilege.Grant;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineColumn;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.analysis.associations.DatabaseWithAssociations;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.BaseJsonFormatter;
import schemacrawler.tools.text.utility.org.json.JSONArray;
import schemacrawler.tools.text.utility.org.json.JSONException;
import schemacrawler.tools.text.utility.org.json.JSONObject;
import schemacrawler.tools.traversal.SchemaTraversalHandler;

/**
 * JSON formatting of schema.
 * 
 * @author Sualeh Fatehi
 */
final class SchemaJsonFormatter
  extends BaseJsonFormatter<SchemaTextOptions>
  implements SchemaTraversalHandler
{

  private final boolean isVerbose;
  private final boolean isList;

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
    isVerbose = schemaTextDetailType == SchemaTextDetailType.details;
    isList = schemaTextDetailType == SchemaTextDetailType.list;
  }

  @Override
  public void handle(final ColumnDataType columnDataType)
    throws SchemaCrawlerException
  {
    if (printVerboseDatabaseInfo && isVerbose)
    {
      try
      {
        final JSONObject jsonColumnDataType = new JSONObject();
        jsonRoot.accumulate("columnDataypes", jsonColumnDataType);

        final String databaseSpecificTypeName;
        if (options.isShowUnqualifiedNames())
        {
          databaseSpecificTypeName = columnDataType.getName();
        }
        else
        {
          databaseSpecificTypeName = columnDataType.getFullName();
        }
        jsonColumnDataType.put("databaseSpecificTypeName",
                               databaseSpecificTypeName);
        jsonColumnDataType
          .put("basedOn", columnDataType.getBaseType() == null? ""
                                                              : columnDataType
                                                                .getBaseType()
                                                                .getName());
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
   * @param routine
   *        Routine metadata.
   */
  @Override
  public void handle(final Routine routine)
  {
    try
    {
      final JSONObject jsonRoutine = new JSONObject();
      jsonRoot.accumulate("routines", jsonRoutine);

      jsonRoutine.put("name", routine.getName());
      if (!options.isShowUnqualifiedNames())
      {
        jsonRoutine.put("fullName", routine.getFullName());
      }
      jsonRoutine.put("type", routine.getRoutineType());
      jsonRoutine.put("returnType", routine.getReturnType());

      if (!isList)
      {
        final JSONArray jsonParameters = new JSONArray();
        jsonRoutine.put("parameters", jsonParameters);
        for (final RoutineColumn<?> column: routine.getColumns())
        {
          jsonParameters.put(handleRoutineColumn(column));
        }
        jsonRoutine.put("definition", routine.getDefinition());

        if (isVerbose)
        {
          jsonRoutine.put("specificName", routine.getSpecificName());
          jsonRoutine.put("remarks", routine.getRemarks());
        }
      }
    }
    catch (final JSONException e)
    {
      LOGGER.log(Level.FINER, "Error outputting Routine: " + e.getMessage(), e);
    }

  }

  /**
   * Provides information on the database schema.
   * 
   * @param synonym
   *        Synonym metadata.
   */
  @Override
  public void handle(final Synonym synonym)
  {
    try
    {
      final JSONObject jsonSynonym = new JSONObject();
      jsonRoot.accumulate("synonyms", jsonSynonym);

      jsonSynonym.put("name", synonym.getName());
      if (!options.isShowUnqualifiedNames())
      {
        jsonSynonym.put("fullName", synonym.getFullName());
      }
      final String referencedObjectName;
      if (options.isShowUnqualifiedNames())
      {
        referencedObjectName = synonym.getReferencedObject().getName();
      }
      else
      {
        referencedObjectName = synonym.getReferencedObject().getFullName();
      }
      jsonSynonym.put("referencedObject", referencedObjectName);

      if (isVerbose)
      {
        jsonSynonym.put("remarks", synonym.getRemarks());
      }
    }
    catch (final JSONException e)
    {
      LOGGER.log(Level.FINER, "Error outputting Synonym: " + e.getMessage(), e);
    }

  }

  /**
   * Provides information on the database schema.
   * 
   * @param table
   *        Table metadata.
   */
  @Override
  public void handle(final Table table)
  {
    final JSONObject jsonTable = new JSONObject();

    try
    {
      jsonRoot.accumulate("tables", jsonTable);

      jsonTable.put("name", table.getName());
      if (!options.isShowUnqualifiedNames())
      {
        jsonTable.put("fullName", table.getFullName());
      }
      jsonTable.put("type", table.getTableType());

      if (!isList)
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
          final List<ColumnReference> weakAssociations = DatabaseWithAssociations
            .getWeakAssociations(table);
          jsonTable.put("weakAssociations",
                        handleColumnReferences(weakAssociations));
        }

        final JSONArray jsonIndices = new JSONArray();
        jsonTable.put("indices", jsonIndices);
        for (final Index index: table.getIndices())
        {
          jsonIndices.put(handleIndex(index));
        }

        if (table instanceof View)
        {
          final View view = (View) table;
          jsonTable.put("definition", view.getDefinition());
        }

        jsonTable.put("triggers", handleTriggers(table.getTriggers()));

        for (final CheckConstraint constraint: table.getCheckConstraints())
        {
          if (constraint != null)
          {
            final JSONObject jsonConsraint = new JSONObject();
            jsonTable.accumulate("constraints", jsonConsraint);
            if (!options.isHideConstraintNames())
            {
              jsonConsraint.put("name", constraint.getName());
            }
            jsonConsraint.put("definition", constraint.getDefinition());
          }
        }

        if (isVerbose)
        {
          for (final Privilege<Table> privilege: table.getPrivileges())
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

          jsonTable.put("remarks", table.getRemarks());
        }
      }
    }
    catch (final JSONException e)
    {
      LOGGER.log(Level.FINER, "Error outputting Table: " + e.getMessage(), e);
    }
  }

  @Override
  public void handleColumnDataTypesEnd()
  {
  }

  @Override
  public void handleColumnDataTypesStart()
  {
  }

  @Override
  public void handleRoutinesEnd()
    throws SchemaCrawlerException
  {
  }

  @Override
  public void handleRoutinesStart()
    throws SchemaCrawlerException
  {
  }

  @Override
  public void handleSynonymsEnd()
    throws SchemaCrawlerException
  {
  }

  @Override
  public void handleSynonymsStart()
    throws SchemaCrawlerException
  {
  }

  @Override
  public void handleTablesEnd()
    throws SchemaCrawlerException
  {
  }

  @Override
  public void handleTablesStart()
    throws SchemaCrawlerException
  {
  }

  private JSONArray handleColumnReferences(final List<? extends ColumnReference> columnReferences)
  {
    final JSONArray jsonColumnReferences = new JSONArray();
    for (final ColumnReference columnReference: columnReferences)
    {
      try
      {
        final JSONObject jsonColumnReference = new JSONObject();

        final String pkColumnName;
        if (options.isShowUnqualifiedNames())
        {
          pkColumnName = columnReference.getPrimaryKeyColumn().getShortName();
        }
        else
        {
          pkColumnName = columnReference.getPrimaryKeyColumn().getFullName();
        }
        jsonColumnReference.put("pkColumn", pkColumnName);

        final String fkColumnName;
        if (options.isShowUnqualifiedNames())
        {
          fkColumnName = columnReference.getForeignKeyColumn().getShortName();
        }
        else
        {
          fkColumnName = columnReference.getForeignKeyColumn().getFullName();
        }
        jsonColumnReference.put("fkColumn", fkColumnName);

        if (columnReference instanceof ForeignKeyColumnReference
            && options.isShowOrdinalNumbers())
        {
          final int keySequence = ((ForeignKeyColumnReference) columnReference)
            .getKeySequence();
          jsonColumnReference.put("keySequence", keySequence);
        }
        jsonColumnReferences.put(jsonColumnReference);
      }
      catch (final JSONException e)
      {
        LOGGER.log(Level.FINER,
                   "Error outputting ColumnReference: " + e.getMessage(),
                   e);
      }
    }
    return jsonColumnReferences;
  }

  private JSONArray handleForeignKeys(final Collection<ForeignKey> foreignKeys)
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
          if (!options.isHideForeignKeyNames())
          {
            jsonFk.put("name", foreignKey.getName());
          }

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

          final List<ForeignKeyColumnReference> columnReferences = foreignKey
            .getColumnReferences();
          jsonFk.put("columnReferences",
                     handleColumnReferences(columnReferences));
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
      if (index instanceof PrimaryKey && !options.isHidePrimaryKeyNames())
      {
        jsonIndex.put("name", index.getName());
      }
      else if (!options.isHideIndexNames())
      {
        jsonIndex.put("name", index.getName());
      }

      final IndexType indexType = index.getIndexType();
      if (indexType != IndexType.unknown && indexType != IndexType.other)
      {
        jsonIndex.put("type", indexType.toString());
      }
      jsonIndex.put("unique", index.isUnique());

      for (final IndexColumn indexColumn: index.getColumns())
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

  private JSONObject handleRoutineColumn(final RoutineColumn<?> column)
  {
    final JSONObject jsonColumn = new JSONObject();

    try
    {
      jsonColumn.put("dataType", column.getColumnDataType().getTypeName());
      jsonColumn.put("databaseSpecificType", column.getColumnDataType()
        .getDatabaseSpecificTypeName());
      jsonColumn.put("width", column.getWidth());
      jsonColumn.put("type", column.getColumnType().toString());
      if (options.isShowOrdinalNumbers())
      {
        jsonColumn.put("ordinal", column.getOrdinalPosition() + 1);
      }
    }
    catch (final JSONException e)
    {
      LOGGER.log(Level.FINER,
                 "Error outputting routine column: " + e.getMessage(),
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
        jsonColumn.put("dataType", column.getColumnDataType().getTypeName());
        jsonColumn.put("databaseSpecificType", column.getColumnDataType()
          .getDatabaseSpecificTypeName());
        jsonColumn.put("width", column.getWidth());
        jsonColumn.put("size", column.getSize());
        jsonColumn.put("decimalDigits", column.getDecimalDigits());
        jsonColumn.put("nullable", column.isNullable());
      }

      if (options.isShowOrdinalNumbers())
      {
        jsonColumn.put("ordinal", column.getOrdinalPosition());
      }
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

  private JSONArray handleTriggers(final Collection<Trigger> triggers)
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

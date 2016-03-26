/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.text.schema;


import static schemacrawler.tools.analysis.counts.CountsUtility.getRowCountMessage;
import static schemacrawler.tools.analysis.counts.CountsUtility.hasRowCount;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import schemacrawler.schema.BaseForeignKey;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ConditionTimingType;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.DefinedObject;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
import schemacrawler.schema.ForeignKeyUpdateRule;
import schemacrawler.schema.Grant;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineColumn;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintColumn;
import schemacrawler.schema.TableConstraintType;
import schemacrawler.schema.Trigger;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.analysis.associations.WeakAssociationForeignKey;
import schemacrawler.tools.analysis.associations.WeakAssociationsUtility;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.BaseJsonFormatter;
import schemacrawler.tools.text.utility.org.json.JSONArray;
import schemacrawler.tools.text.utility.org.json.JSONException;
import schemacrawler.tools.text.utility.org.json.JSONObject;
import schemacrawler.tools.traversal.SchemaTraversalHandler;
import schemacrawler.utility.NamedObjectSort;
import sf.util.StringFormat;

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
  private final boolean isBrief;

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
    isBrief = schemaTextDetailType == SchemaTextDetailType.brief;
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
          .put("basedOn",
               columnDataType.getBaseType() == null? "": columnDataType
                 .getBaseType().getName());
        jsonColumnDataType.put("userDefined", columnDataType.isUserDefined());
        jsonColumnDataType.put("createParameters",
                               columnDataType.getCreateParameters());
        jsonColumnDataType.put("nullable", columnDataType.isNullable());
        jsonColumnDataType.put("autoIncrementable",
                               columnDataType.isAutoIncrementable());
        jsonColumnDataType.put("searchable",
                               columnDataType.getSearchable().toString());
      }
      catch (final JSONException e)
      {
        LOGGER.log(Level.FINER,
                   e,
                   new StringFormat("Error outputting ColumnDataType: %s",
                                    e.getMessage()));
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
      printRemarks(routine, jsonRoutine);

      if (!isBrief)
      {
        final JSONArray jsonParameters = new JSONArray();
        jsonRoutine.put("parameters", jsonParameters);

        final List<? extends RoutineColumn<? extends Routine>> columns = routine
          .getColumns();
        Collections.sort(columns,
                         NamedObjectSort.getNamedObjectSort(options
                           .isAlphabeticalSortForRoutineColumns()));
        for (final RoutineColumn<?> column: columns)
        {
          jsonParameters.put(handleRoutineColumn(column));
        }
        printDefinition(routine, jsonRoutine);

        if (isVerbose)
        {
          if (!options.isHideRoutineSpecificNames())
          {
            jsonRoutine.put("specificName", routine.getSpecificName());
          }
        }
      }
    }
    catch (final JSONException e)
    {
      LOGGER
        .log(Level.FINER,
             e,
             new StringFormat("Error outputting Routine: %s", e.getMessage()));
    }

  }

  /**
   * Provides information on the database schema.
   *
   * @param sequence
   *        Sequence metadata.
   */
  @Override
  public void handle(final Sequence sequence)
  {
    try
    {
      final JSONObject jsonSequence = new JSONObject();
      jsonRoot.accumulate("sequences", jsonSequence);

      jsonSequence.put("name", sequence.getName());
      if (!options.isShowUnqualifiedNames())
      {
        jsonSequence.put("fullName", sequence.getFullName());
      }
      printRemarks(sequence, jsonSequence);

      if (!isBrief)
      {
        jsonSequence.put("increment", sequence.getIncrement());
        jsonSequence.put("minimumValue", sequence.getMinimumValue());
        jsonSequence.put("maximumValue", sequence.getMaximumValue());
        jsonSequence.put("cycle", sequence.isCycle());
      }
    }
    catch (final JSONException e)
    {
      LOGGER
        .log(Level.FINER,
             e,
             new StringFormat("Error outputting sequence: %s", e.getMessage()));
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
      printRemarks(synonym, jsonSynonym);

      if (!isBrief)
      {
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
      }
    }
    catch (final JSONException e)
    {
      LOGGER
        .log(Level.FINER,
             e,
             new StringFormat("Error outputting synonym: %s", e.getMessage()));
    }

  }

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
      printRemarks(table, jsonTable);

      final JSONArray jsonColumns = new JSONArray();
      jsonTable.put("columns", jsonColumns);
      final List<Column> columns = table.getColumns();
      Collections.sort(columns,
                       NamedObjectSort.getNamedObjectSort(options
                         .isAlphabeticalSortForTableColumns()));
      for (final Column column: columns)
      {
        if (column.isHidden())
        {
          continue;
        }
        if (isBrief && !isColumnSignificant(column))
        {
          continue;
        }
        jsonColumns.put(handleTableColumn(column));
      }

      jsonTable.put("primaryKey", handleIndex(table.getPrimaryKey()));
      jsonTable.put("foreignKeys", handleForeignKeys(table.getForeignKeys()));
      if (!isBrief)
      {
        if (isVerbose)
        {
          final Collection<WeakAssociationForeignKey> weakAssociationsCollection = WeakAssociationsUtility
            .getWeakAssociations(table);
          final List<WeakAssociationForeignKey> weakAssociations = new ArrayList<>(weakAssociationsCollection);
          Collections.sort(weakAssociations);

          jsonTable.put("weakAssociations",
                        handleWeakAssociations(weakAssociations));
        }

        final JSONArray jsonIndexes = new JSONArray();
        jsonTable.put("indexes", jsonIndexes);
        final Collection<Index> indexesCollection = table.getIndexes();
        final List<Index> indexes = new ArrayList<>(indexesCollection);
        Collections
          .sort(indexes,
                NamedObjectSort
                  .getNamedObjectSort(options.isAlphabeticalSortForIndexes()));
        for (final Index index: indexes)
        {
          jsonIndexes.put(handleIndex(index));
        }
        printDefinition(table, jsonTable);

        jsonTable.put("triggers", handleTriggers(table.getTriggers()));

        final JSONArray jsonTableConstraints = new JSONArray();
        jsonTable.put("tableConstraints", jsonTableConstraints);
        final Collection<TableConstraint> tableConstraintsCollection = table
          .getTableConstraints();
        final List<TableConstraint> tableConstraints = new ArrayList<>(tableConstraintsCollection);
        Collections
          .sort(tableConstraints,
                NamedObjectSort
                  .getNamedObjectSort(options.isAlphabeticalSortForIndexes()));
        for (final TableConstraint tableConstraint: tableConstraints)
        {
          jsonTableConstraints.put(handleTableConstraint(tableConstraint));
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
              for (final Grant<?> grant: privilege.getGrants())
              {
                final JSONObject jsonGrant = new JSONObject();
                jsonPrivilege.accumulate("grants", jsonGrant);

                jsonGrant.put("grantor", grant.getGrantor());
                jsonGrant.put("grantee", grant.getGrantee());
                jsonGrant.put("grantable", grant.isGrantable());
              }
            }
          }
        }

        final JSONObject jsonAdditionalInformation = new JSONObject();
        printTableRowCount(table, jsonAdditionalInformation);
        if (jsonAdditionalInformation.length() > 0)
        {
          jsonTable.put("additionalInformation", jsonAdditionalInformation);
        }
      }
    }
    catch (final JSONException e)
    {
      LOGGER
        .log(Level.FINER,
             e,
             new StringFormat("Error outputting table: %s", e.getMessage()));
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
  public void handleSequencesEnd()
    throws SchemaCrawlerException
  {
  }

  @Override
  public void handleSequencesStart()
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

  private JSONArray handleColumnReferences(final BaseForeignKey<? extends ColumnReference> foreignKey)
  {
    final JSONArray jsonColumnReferences = new JSONArray();
    for (final ColumnReference columnReference: foreignKey)
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
                   e,
                   new StringFormat("Error outputting column reference: %s",
                                    e.getMessage()));
      }
    }
    return jsonColumnReferences;
  }

  private JSONArray handleForeignKeys(final Collection<ForeignKey> foreignKeysCollection)
  {
    final JSONArray jsonFks = new JSONArray();
    final List<ForeignKey> foreignKeys = new ArrayList<>(foreignKeysCollection);
    Collections
      .sort(foreignKeys,
            NamedObjectSort
              .getNamedObjectSort(options.isAlphabeticalSortForForeignKeys()));
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

          jsonFk.put("columnReferences", handleColumnReferences(foreignKey));
        }
        catch (final JSONException e)
        {
          LOGGER.log(Level.FINER,
                     e,
                     new StringFormat("Error outputting foreign key: %s",
                                      e.getMessage()));
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

      for (final IndexColumn indexColumn: index)
      {
        jsonIndex.accumulate("columns", handleTableColumn(indexColumn));
      }
      printDefinition(index, jsonIndex);
    }
    catch (final JSONException e)
    {
      LOGGER
        .log(Level.FINER,
             e,
             new StringFormat("Error outputting index: %s", e.getMessage()));
    }

    return jsonIndex;
  }

  private JSONObject handleRoutineColumn(final RoutineColumn<?> column)
  {
    final JSONObject jsonColumn = new JSONObject();

    try
    {
      jsonColumn.put("name", column.getName());
      jsonColumn
        .put("dataType",
             column.getColumnDataType().getJavaSqlType().getJavaSqlTypeName());
      jsonColumn.put("databaseSpecificType",
                     column.getColumnDataType().getDatabaseSpecificTypeName());
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
                 e,
                 new StringFormat("Error outputting routine column: %s",
                                  e.getMessage()));
    }

    return jsonColumn;
  }

  private JSONObject handleTableColumn(final Column column)
  {
    final JSONObject jsonColumn = new JSONObject();
    try
    {
      jsonColumn.put("name", column.getName());
      printRemarks(column, jsonColumn);

      if (column instanceof IndexColumn)
      {
        jsonColumn.put("sortSequence",
                       ((IndexColumn) column).getSortSequence().name());
      }
      else
      {
        jsonColumn.put("dataType",
                       column.getColumnDataType().getJavaSqlType()
                         .getJavaSqlTypeName());
        jsonColumn
          .put("databaseSpecificType",
               column.getColumnDataType().getDatabaseSpecificTypeName());
        jsonColumn.put("width", column.getWidth());
        jsonColumn.put("size", column.getSize());
        jsonColumn.put("decimalDigits", column.getDecimalDigits());
        jsonColumn.put("nullable", column.isNullable());
        jsonColumn.put("autoIncremented", column.isAutoIncremented());
      }

      if (options.isShowOrdinalNumbers())
      {
        jsonColumn.put("ordinal", column.getOrdinalPosition());
      }
    }
    catch (final JSONException e)
    {
      LOGGER
        .log(Level.FINER,
             e,
             new StringFormat("Error outputting column: %s", e.getMessage()));
    }

    return jsonColumn;
  }

  private JSONObject handleTableConstraint(final TableConstraint tableConstraint)
  {

    final JSONObject jsonTableConstraint = new JSONObject();

    if (tableConstraint == null)
    {
      return jsonTableConstraint;
    }

    try
    {
      if (!options.isHideTableConstraintNames())
      {
        jsonTableConstraint.put("name", tableConstraint.getName());
      }

      final TableConstraintType tableConstraintType = tableConstraint
        .getTableConstraintType();
      if (tableConstraintType != TableConstraintType.unknown)
      {
        jsonTableConstraint.put("type", tableConstraintType.toString());
      }

      for (final TableConstraintColumn tableConstraintColumn: tableConstraint
        .getColumns())
      {
        jsonTableConstraint
          .accumulate("columns", handleTableColumn(tableConstraintColumn));
      }
      printDefinition(tableConstraint, jsonTableConstraint);
    }
    catch (final JSONException e)
    {
      LOGGER.log(Level.FINER,
                 e,
                 new StringFormat("Error outputting table constraint: %s",
                                  e.getMessage()));
    }

    return jsonTableConstraint;
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

          if (!options.isHideTriggerNames())
          {
            jsonTrigger.put("name", trigger.getName());
          }

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
                     e,
                     new StringFormat("Error outputting trigger: %s",
                                      e.getMessage()));
        }
      }
    }
    return jsonTriggers;
  }

  private JSONArray handleWeakAssociations(final Collection<WeakAssociationForeignKey> weakAssociationsCollection)
    throws JSONException
  {
    final JSONArray jsonFks = new JSONArray();

    final List<WeakAssociationForeignKey> weakAssociations = new ArrayList<>(weakAssociationsCollection);
    Collections.sort(weakAssociations);
    for (final WeakAssociationForeignKey weakFk: weakAssociations)
    {
      if (weakFk != null)
      {
        try
        {
          final JSONObject jsonFk = new JSONObject();
          jsonFks.put(jsonFk);

          jsonFk.put("columnReferences", handleColumnReferences(weakFk));
        }
        catch (final JSONException e)
        {
          LOGGER.log(Level.FINER,
                     e,
                     new StringFormat("Error outputting weak association: ",
                                      e.getMessage()));
        }
      }
    }

    return jsonFks;
  }

  private void printDefinition(final DefinedObject definedObject,
                               final JSONObject jsonObject)
                                 throws JSONException
  {
    if (!isVerbose)
    {
      return;
    }
    // Print empty string, if value is not available
    jsonObject.put("definition", definedObject.getDefinition());
  }

  private void printRemarks(final DatabaseObject object,
                            final JSONObject jsonObject)
                              throws JSONException
  {
    if (object == null || options.isHideRemarks())
    {
      return;
    }
    // Print empty string, if value is not available
    jsonObject.put("remarks", object.getRemarks());
  }

  private void printTableRowCount(final Table table,
                                  final JSONObject jsonObject)
                                    throws JSONException
  {
    if (!options.isShowRowCounts() || table == null || !hasRowCount(table))
    {
      return;
    }
    jsonObject.put("rowCount", getRowCountMessage(table));
  }

}

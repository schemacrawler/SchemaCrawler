/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package schemacrawler.tools.text.schema;


import static sf.util.Utility.isBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import schemacrawler.schema.ActionOrientationType;
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
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Privilege.Grant;
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
import schemacrawler.tools.analysis.associations.CatalogWithAssociations;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.BaseTabularFormatter;
import schemacrawler.tools.text.utility.TextFormattingHelper.DocumentHeaderType;
import schemacrawler.tools.traversal.SchemaTraversalHandler;
import schemacrawler.utility.NamedObjectSort;

/**
 * Text formatting of schema.
 *
 * @author Sualeh Fatehi
 */
final class SchemaTextFormatter
  extends BaseTabularFormatter<SchemaTextOptions>
  implements SchemaTraversalHandler
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
  SchemaTextFormatter(final SchemaTextDetailType schemaTextDetailType,
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

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handle(schemacrawler.schema.ColumnDataType)
   */
  @Override
  public void handle(final ColumnDataType columnDataType)
    throws SchemaCrawlerException
  {
    if (printVerboseDatabaseInfo && isVerbose)
    {
      out.append(formattingHelper.createObjectStart(""));
      printColumnDataType(columnDataType);
      out.append(formattingHelper.createObjectEnd());
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handle(Routine)
   */
  @Override
  public void handle(final Routine routine)
  {
    final String routineTypeDetail = String.format("%s, %s",
                                                   routine.getRoutineType(),
                                                   routine.getReturnType());
    final String routineName;
    if (options.isShowUnqualifiedNames())
    {
      routineName = routine.getName();
    }
    else
    {
      routineName = routine.getFullName();
    }
    final String routineType = "[" + routineTypeDetail + "]";

    out.println(formattingHelper.createObjectStart(routineName));
    out.println(formattingHelper.createNameRow("", routineType));
    printRemarks(routine);

    if (!isBrief)
    {
      printRoutineColumns(routine.getColumns());
    }

    if (isVerbose)
    {
      if (!options.isHideRoutineSpecificNames())
      {
        printDefinition("specific name", "", routine.getSpecificName());
      }
      printDefinition(routine);
    }

    out.println(formattingHelper.createObjectEnd());

    out.flush();

  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handle(schemacrawler.schema.Sequence)
   */
  @Override
  public void handle(final Sequence sequence)
  {
    final String sequenceName;
    if (options.isShowUnqualifiedNames())
    {
      sequenceName = sequence.getName();
    }
    else
    {
      sequenceName = sequence.getFullName();
    }
    final String sequenceType = "[sequence]";

    out.println(formattingHelper.createObjectStart(""));
    out.println(formattingHelper.createNameRow(sequenceName, sequenceType));
    printRemarks(sequence);

    if (!isBrief)
    {
      out.println(formattingHelper.createDetailRow("", "increment", String
        .valueOf(sequence.getIncrement())));
      out.println(formattingHelper.createDetailRow("", "minimum value", String
        .valueOf(sequence.getMinimumValue())));
      out.println(formattingHelper.createDetailRow("", "maximum value", String
        .valueOf(sequence.getMaximumValue())));
      out.println(formattingHelper.createDetailRow("", "cycle", String
        .valueOf(sequence.isCycle())));
    }

    out.println(formattingHelper.createObjectEnd());

    out.flush();

  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handle(schemacrawler.schema.Synonym)
   */
  @Override
  public void handle(final Synonym synonym)
  {
    final String synonymName;
    if (options.isShowUnqualifiedNames())
    {
      synonymName = synonym.getName();
    }
    else
    {
      synonymName = synonym.getFullName();
    }
    final String synonymType = "[synonym]";

    out.println(formattingHelper.createObjectStart(synonymName));
    out.println(formattingHelper.createNameRow("", synonymType));
    printRemarks(synonym);

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
      out.println(formattingHelper.createDetailRow("", String
        .format("%s %s %s",
                synonym.getName(),
                formattingHelper.createRightArrow(),
                referencedObjectName), ""));
    }

    out.println(formattingHelper.createObjectEnd());

    out.flush();

  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handle(schemacrawler.schema.Table)
   */
  @Override
  public void handle(final Table table)
  {
    final String tableName;
    if (options.isShowUnqualifiedNames())
    {
      tableName = table.getName();
    }
    else
    {
      tableName = table.getFullName();
    }
    final String tableType = "[" + table.getTableType() + "]";

    out.println(formattingHelper.createObjectStart(tableName));
    out.println(formattingHelper.createNameRow("", tableType));
    printRemarks(table);

    final List<Column> columns = table.getColumns();
    printTableColumns(columns);

    printPrimaryKey(table.getPrimaryKey());
    printForeignKeys(table);

    if (!isBrief)
    {
      if (isVerbose)
      {
        printWeakAssociations(table);
      }
      printIndices(table.getIndices());
      printTriggers(table.getTriggers());
      printTableConstraints(table.getTableConstraints());
      if (isVerbose)
      {
        printPrivileges(table.getPrivileges());
        printDefinition(table);
      }
    }

    out.println(formattingHelper.createObjectEnd());

    out.flush();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleColumnDataTypesEnd()
   */
  @Override
  public void handleColumnDataTypesEnd()
  {
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleColumnDataTypesStart()
   */
  @Override
  public void handleColumnDataTypesStart()
  {
    if (printVerboseDatabaseInfo && isVerbose)
    {
      out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                                "Data Types"));
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleRoutinesEnd()
   */
  @Override
  public void handleRoutinesEnd()
    throws SchemaCrawlerException
  {
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleRoutinesStart()
   */
  @Override
  public void handleRoutinesStart()
    throws SchemaCrawlerException
  {
    out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                              "Routines"));
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleSequencesEnd()
   */
  @Override
  public void handleSequencesEnd()
    throws SchemaCrawlerException
  {
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleSequencesStart()
   */
  @Override
  public void handleSequencesStart()
    throws SchemaCrawlerException
  {
    out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                              "Sequences"));
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleSynonymsEnd()
   */
  @Override
  public void handleSynonymsEnd()
    throws SchemaCrawlerException
  {
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleSynonymsStart()
   */
  @Override
  public void handleSynonymsStart()
    throws SchemaCrawlerException
  {
    out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                              "Synonyms"));
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleTablesEnd()
   */
  @Override
  public void handleTablesEnd()
    throws SchemaCrawlerException
  {
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleTablesStart()
   */
  @Override
  public void handleTablesStart()
    throws SchemaCrawlerException
  {
    out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                              "Tables"));
  }

  private void printColumnDataType(final ColumnDataType columnDataType)
  {
    final String databaseSpecificTypeName;
    if (options.isShowUnqualifiedNames())
    {
      databaseSpecificTypeName = columnDataType.getName();
    }
    else
    {
      databaseSpecificTypeName = columnDataType.getFullName();
    }
    final String typeName = columnDataType.getJavaSqlType()
      .getJavaSqlTypeName();
    final String userDefined = negate(columnDataType.isUserDefined(),
                                      "user defined");
    final String nullable = negate(columnDataType.isNullable(), "nullable");
    final String autoIncrementable = negate(columnDataType.isAutoIncrementable(),
                                            "auto-incrementable");
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
    out.println(formattingHelper.createDetailRow("", "based on", typeName));
    out.println(formattingHelper.createDescriptionRow(userDefined));
    out.println(formattingHelper.createDescriptionRow(definedWith));
    out.println(formattingHelper.createDescriptionRow(nullable));
    out.println(formattingHelper.createDescriptionRow(autoIncrementable));
    out.println(formattingHelper.createDescriptionRow(columnDataType
      .getSearchable().toString()));
  }

  private void printColumnReferences(final boolean isForeignKey,
                                     final String tableName,
                                     final ColumnReference... columnReferences)
  {
    for (final ColumnReference columnReference: columnReferences)
    {
      final Column pkColumn;
      final Column fkColumn;
      final String pkColumnName;
      final String fkColumnName;
      pkColumn = columnReference.getPrimaryKeyColumn();
      fkColumn = columnReference.getForeignKeyColumn();

      boolean isIncoming = false;
      if (pkColumn.getParent().getName().equals(tableName))
      {
        pkColumnName = pkColumn.getName();
        isIncoming = true;
      }
      else if (options.isShowUnqualifiedNames())
      {
        pkColumnName = pkColumn.getShortName();
      }
      else
      {
        pkColumnName = pkColumn.getFullName();
      }
      if (fkColumn.getParent().getName().equals(tableName))
      {
        fkColumnName = fkColumn.getName();
      }
      else if (options.isShowUnqualifiedNames())
      {
        fkColumnName = fkColumn.getShortName();
      }
      else
      {
        fkColumnName = fkColumn.getFullName();
      }
      String keySequenceString = "";
      if (columnReference instanceof ForeignKeyColumnReference
          && options.isShowOrdinalNumbers())
      {
        final int keySequence = ((ForeignKeyColumnReference) columnReference)
          .getKeySequence();
        keySequenceString = String.format("%2d", keySequence);
      }

      final String relationship;
      if (isIncoming)
      {
        relationship = String.format("%s %s %s",
                                     pkColumnName,
                                     isForeignKey? formattingHelper
                                       .createLeftArrow(): formattingHelper
                                       .createWeakLeftArrow(),
                                     fkColumnName);
      }
      else
      {
        relationship = String.format("%s %s %s",
                                     fkColumnName,
                                     isForeignKey? formattingHelper
                                       .createRightArrow(): formattingHelper
                                       .createWeakRightArrow(),
                                     pkColumnName);
      }
      out.println(formattingHelper.createDetailRow(keySequenceString,
                                                   relationship,
                                                   ""));
    }
  }

  private void printDefinition(final DefinedObject definedObject)
  {
    if (definedObject == null || !definedObject.hasDefinition())
    {
      return;
    }

    out.println(formattingHelper.createEmptyRow());
    out.println(formattingHelper.createNameRow("", "[definition]"));
    out.println(formattingHelper.createDefinitionRow(definedObject
      .getDefinition()));
  }

  private void printDefinition(final String heading,
                               final String name,
                               final String definition)
  {
    if (isBlank(definition))
    {
      return;
    }
    final String definitionName;
    if (isBlank(name))
    {
      definitionName = "";
    }
    else
    {
      definitionName = name;
    }
    out.println(formattingHelper.createEmptyRow());
    out.println(formattingHelper.createNameRow(definitionName, "[" + heading
                                                               + "]"));
    out.println(formattingHelper.createDefinitionRow(definition));
  }

  private void printForeignKeys(final Table table)
  {
    final String tableName = table.getName();
    final Collection<ForeignKey> foreignKeysCollection = table.getForeignKeys();
    final List<ForeignKey> foreignKeys = new ArrayList<>(foreignKeysCollection);
    Collections.sort(foreignKeys, NamedObjectSort.getNamedObjectSort(options
      .isAlphabeticalSortForForeignKeys()));

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
        if (updateRule == deleteRule
            && updateRule != ForeignKeyUpdateRule.unknown)
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
        out.println(formattingHelper.createNameRow(fkName, fkDetails));
        printColumnReferences(true, tableName, foreignKey.getColumnReferences()
          .toArray(new ColumnReference[0]));
      }
    }
  }

  private void printIndices(final Collection<Index> indicesCollection)
  {
    final List<Index> indices = new ArrayList<>(indicesCollection);
    Collections.sort(indices, NamedObjectSort.getNamedObjectSort(options
      .isAlphabeticalSortForIndexes()));

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
        final IndexType indexType = index.getIndexType();
        String indexTypeString = "";
        if (indexType != IndexType.unknown && indexType != IndexType.other)
        {
          indexTypeString = indexType.toString() + " ";
        }
        final String indexDetails = "[" + (index.isUnique()? "": "non-")
                                    + "unique " + indexTypeString + "index]";
        out.println(formattingHelper.createNameRow(indexName, indexDetails));

        if (!isBrief)
        {
          printTableColumns(index.getColumns());
        }

        if (isVerbose)
        {
          if (index.hasDefinition())
          {
            out.println(formattingHelper.createDefinitionRow(index
              .getDefinition()));
          }
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
      if (isBlank(pkName))
      {
        pkName = "";
      }
      out.println(formattingHelper.createNameRow(pkName, "[primary key]"));
      printTableColumns(primaryKey.getColumns());
    }
  }

  private void printPrivileges(final Collection<Privilege<Table>> privileges)
  {

    for (final Privilege<Table> privilege: privileges)
    {
      if (privilege != null)
      {
        out.println(formattingHelper.createEmptyRow());
        out.println(formattingHelper.createNameRow(privilege.getName(),
                                                   "[privilege]"));
        for (final Grant grant: privilege.getGrants())
        {
          final String grantedFrom = String.format("%s %s %s%s", grant
            .getGrantor(), formattingHelper.createRightArrow(), grant
            .getGrantee(), grant.isGrantable()? " (grantable)": "");
          out.println(formattingHelper.createDetailRow("", grantedFrom, ""));
        }
      }
    }
  }

  private void printRemarks(final DatabaseObject object)
  {
    if (object == null || !object.hasRemarks() || options.isHideRemarks())
    {
      return;
    }
    out.println(formattingHelper.createDefinitionRow(object.getRemarks()));
  }

  private void printRoutineColumns(final List<? extends RoutineColumn<?>> columns)
  {

    Collections.sort(columns, NamedObjectSort.getNamedObjectSort(options
      .isAlphabeticalSortForRoutineColumns()));

    for (final RoutineColumn<?> column: columns)
    {
      final String columnTypeName;
      if (options.isShowStandardColumnTypeNames())
      {
        columnTypeName = column.getColumnDataType().getJavaSqlType()
          .getJavaSqlTypeName();
      }
      else
      {
        columnTypeName = column.getColumnDataType()
          .getDatabaseSpecificTypeName();
      }
      final StringBuilder columnType = new StringBuilder();
      columnType.append(columnTypeName).append(column.getWidth());
      if (column.getColumnType() != null)
      {
        columnType.append(", ").append(column.getColumnType().toString());
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

  private void printTableColumnAutoIncremented(final Column column)
  {
    if (column == null || !column.isAutoIncremented())
    {
      return;
    }
    out.println(formattingHelper.createDetailRow("", "", "auto-incremented"));
  }

  private void printTableColumnRemarks(final Column column)
  {
    if (column == null || !column.hasRemarks() || options.isHideRemarks())
    {
      return;
    }
    out.println(formattingHelper.createDetailRow("", "", column.getRemarks()));
  }

  private void printTableColumns(final List<? extends Column> columns)
  {

    Collections.sort(columns, NamedObjectSort.getNamedObjectSort(options
      .isAlphabeticalSortForTableColumns()));

    for (final Column column: columns)
    {
      if (isBrief && !isColumnSignificant(column))
      {
        continue;
      }

      final String columnName = column.getName();

      final String columnDetails;

      boolean emphasize = false;
      if (column instanceof IndexColumn)
      {
        columnDetails = ((IndexColumn) column).getSortSequence().name();
      }
      else if (column instanceof TableConstraintColumn)
      {
        columnDetails = "";
      }
      else
      {
        final String columnTypeName;
        if (options.isShowStandardColumnTypeNames())
        {
          columnTypeName = column.getColumnDataType().getJavaSqlType()
            .getJavaSqlTypeName();
        }
        else
        {
          columnTypeName = column.getColumnDataType()
            .getDatabaseSpecificTypeName();
        }
        final String columnType = columnTypeName + column.getWidth();
        final String nullable = columnNullable(columnTypeName,
                                               column.isNullable());
        columnDetails = columnType + nullable;
        emphasize = column.isPartOfPrimaryKey();
      }

      String ordinalNumberString = "";
      if (options.isShowOrdinalNumbers())
      {
        ordinalNumberString = String.valueOf(column.getOrdinalPosition());
      }
      out.println(formattingHelper.createDetailRow(ordinalNumberString,
                                                   columnName,
                                                   columnDetails,
                                                   emphasize));

      printTableColumnAutoIncremented(column);
      printTableColumnRemarks(column);

    }
  }

  private void printTableConstraints(final Collection<TableConstraint> constraintsCollection)
  {
    final List<TableConstraint> constraints = new ArrayList<>(constraintsCollection);
    Collections.sort(constraints, NamedObjectSort.getNamedObjectSort(options
      .isAlphabeticalSortForIndexes()));

    for (final TableConstraint constraint: constraints)
    {
      if (constraint != null)
      {
        String constraintName = "";
        if (!options.isHideTableConstraintNames())
        {
          constraintName = constraint.getName();
        }
        final String constraintType = constraint.getTableConstraintType()
          .getValue().toLowerCase();

        // Show only check or unique constraints, or any constraint that
        // has a definition
        if (!(EnumSet.of(TableConstraintType.check, TableConstraintType.unique)
          .contains(constraint.getTableConstraintType()) || constraint
          .hasDefinition()))
        {
          continue;
        }
        final String constraintDetails = "[" + constraintType + " constraint]";
        out.println(formattingHelper.createEmptyRow());
        out.println(formattingHelper.createNameRow(constraintName,
                                                   constraintDetails));

        if (!isBrief)
        {
          printTableColumns(constraint.getColumns());
        }

        if (isVerbose)
        {
          if (constraint.hasDefinition())
          {
            out.println(formattingHelper.createDefinitionRow(constraint
              .getDefinition()));
          }
        }

      }
    }
  }

  private void printTriggers(final Collection<Trigger> triggers)
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

        final String triggerName;
        if (options.isHideTriggerNames())
        {
          triggerName = "";
        }
        else
        {
          triggerName = trigger.getName();
        }

        out.println(formattingHelper.createNameRow(triggerName, triggerType));

        if (!isBlank(actionCondition))
        {
          out.println(formattingHelper.createDescriptionRow(actionCondition));
        }
        if (!isBlank(actionStatement))
        {
          out.println(formattingHelper.createDescriptionRow(actionStatement));
        }
      }
    }
  }

  private void printWeakAssociations(final Table table)
  {
    final String tableName = table.getName();
    final Collection<ColumnReference> weakAssociationsCollection = CatalogWithAssociations
      .getWeakAssociations(table);
    final List<ColumnReference> weakAssociations = new ArrayList<>(weakAssociationsCollection);
    Collections.sort(weakAssociations);
    for (final ColumnReference weakAssociation: weakAssociations)
    {
      out.println(formattingHelper.createEmptyRow());
      out.println(formattingHelper.createNameRow("", "[weak association]"));
      printColumnReferences(false, tableName, weakAssociation);
    }
  }

}

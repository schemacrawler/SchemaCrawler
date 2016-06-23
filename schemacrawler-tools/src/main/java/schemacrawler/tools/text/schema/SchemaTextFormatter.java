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
import static sf.util.Utility.isBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import schemacrawler.crawl.NotLoadedException;
import schemacrawler.schema.ActionOrientationType;
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
import schemacrawler.tools.text.base.BaseTabularFormatter;
import schemacrawler.tools.text.utility.TextFormattingHelper.DocumentHeaderType;
import schemacrawler.tools.traversal.SchemaTraversalHandler;
import schemacrawler.utility.MetaDataUtility;
import schemacrawler.utility.MetaDataUtility.ForeignKeyCardinality;
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

  private static final String SPACE = " ";

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
      formattingHelper.writeObjectStart();
      printColumnDataType(columnDataType);
      formattingHelper.writeObjectEnd();
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
    final String routineTypeDetail = String
      .format("%s, %s", routine.getRoutineType(), routine.getReturnType());
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

    formattingHelper.println();
    formattingHelper.println();
    formattingHelper.writeObjectStart();
    formattingHelper
      .writeObjectNameRow(nodeId(routine),
                          routineName,
                          routineType,
                          colorMap.getColor(routine));
    printRemarks(routine);

    if (!isBrief)
    {
      printRoutineColumns(routine.getColumns());
    }

    if (isVerbose)
    {
      if (!options.isHideRoutineSpecificNames())
      {
        formattingHelper.writeEmptyRow();
        formattingHelper.writeNameRow("", "[specific name]");
        formattingHelper.writeWideRow(routine.getSpecificName(), "");
      }
      printDefinition(routine);
    }

    formattingHelper.writeObjectEnd();
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

    formattingHelper.println();
    formattingHelper.println();
    formattingHelper.writeObjectStart();
    formattingHelper.writeObjectNameRow(nodeId(sequence),
                                        sequenceName,
                                        sequenceType,
                                        colorMap.getColor(sequence));
    printRemarks(sequence);

    if (!isBrief)
    {
      formattingHelper.writeDetailRow("",
                                      "increment",
                                      String.valueOf(sequence.getIncrement()));
      formattingHelper
        .writeDetailRow("",
                        "minimum value",
                        String.valueOf(sequence.getMinimumValue()));
      formattingHelper
        .writeDetailRow("",
                        "maximum value",
                        String.valueOf(sequence.getMaximumValue()));
      formattingHelper
        .writeDetailRow("", "cycle", String.valueOf(sequence.isCycle()));
    }

    formattingHelper.writeObjectEnd();
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

    formattingHelper.println();
    formattingHelper.println();
    formattingHelper.writeObjectStart();
    formattingHelper
      .writeObjectNameRow(nodeId(synonym),
                          synonymName,
                          synonymType,
                          colorMap.getColor(synonym));
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
      formattingHelper.writeDetailRow("",
                                      String.format("%s %s %s",
                                                    synonym.getName(),
                                                    formattingHelper
                                                      .createRightArrow(),
                                                    referencedObjectName),
                                      "");
    }

    formattingHelper.writeObjectEnd();
  }

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

    formattingHelper.println();
    formattingHelper.println();
    formattingHelper.writeObjectStart();
    formattingHelper.writeObjectNameRow(nodeId(table),
                                        tableName,
                                        tableType,
                                        colorMap.getColor(table));
    printRemarks(table);

    final List<Column> columns = table.getColumns();
    printTableColumns(columns, false);
    printPrimaryKey(table.getPrimaryKey());
    printForeignKeys(table);
    if (!isBrief)
    {
      if (isVerbose && !options.isHideWeakAssociations())
      {
        printWeakAssociations(table);
      }
      printIndexes(table.getIndexes());
      printTriggers(table.getTriggers());
      printTableConstraints(table.getTableConstraints());

      if (isVerbose)
      {
        printPrivileges(table.getPrivileges());
        printDefinition(table);
      }

      printTableRowCount(table);
    }

    formattingHelper.writeObjectEnd();
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
      formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Data Types");
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
    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Routines");
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
    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Sequences");
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
    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Synonyms");
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
    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Tables");
  }

  private void printColumnDataType(final ColumnDataType columnDataType)
  {
    final String typeName;
    if (options.isShowUnqualifiedNames())
    {
      typeName = columnDataType.getName();
    }
    else
    {
      typeName = columnDataType.getFullName();
    }

    final String baseTypeName;
    final ColumnDataType baseColumnDataType = columnDataType.getBaseType();
    if (baseColumnDataType == null)
    {
      baseTypeName = "";
    }
    else
    {
      if (options.isShowUnqualifiedNames())
      {
        baseTypeName = baseColumnDataType.getName();
      }
      else
      {
        baseTypeName = baseColumnDataType.getFullName();
      }
    }

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
    formattingHelper.writeNameRow(typeName, "[data type]");
    formattingHelper.writeDetailRow("", "based on", baseTypeName);
    formattingHelper.writeDescriptionRow(userDefined);
    formattingHelper.writeDescriptionRow(definedWith);
    formattingHelper.writeDescriptionRow(nullable);
    formattingHelper.writeDescriptionRow(autoIncrementable);
    formattingHelper
      .writeDescriptionRow(columnDataType.getSearchable().toString());
  }

  private void printColumnReferences(final boolean isForeignKey,
                                     final Table table,
                                     final BaseForeignKey<? extends ColumnReference> foreignKey)
  {
    final ForeignKeyCardinality fkCardinality = MetaDataUtility
      .findForeignKeyCardinality(foreignKey);
    for (final ColumnReference columnReference: foreignKey)
    {
      final Column pkColumn;
      final Column fkColumn;
      final String pkColumnName;
      final String fkColumnName;
      pkColumn = columnReference.getPrimaryKeyColumn();
      fkColumn = columnReference.getForeignKeyColumn();

      boolean isIncoming = false;
      if (pkColumn.getParent().equals(table))
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
      if (fkColumn.getParent().equals(table))
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
        final String fkHyperlink = formattingHelper
          .createAnchor(fkColumnName, "#" + nodeId(fkColumn.getParent()));
        final String arrow = isForeignKey? formattingHelper
          .createLeftArrow(): formattingHelper.createWeakLeftArrow();
        relationship = String
          .format("%s %s%s %s",
                  pkColumnName,
                  arrow,
                  fkCardinality.toString(),
                  fkHyperlink);
      }
      else
      {
        final String pkHyperlink = formattingHelper
          .createAnchor(pkColumnName, "#" + nodeId(pkColumn.getParent()));
        final String arrow = isForeignKey? formattingHelper
          .createRightArrow(): formattingHelper.createWeakRightArrow();
        relationship = String
          .format("%s %s%s %s",
                  fkColumnName,
                  fkCardinality.toString(),
                  arrow,
                  pkHyperlink);
      }
      formattingHelper.writeDetailRow(keySequenceString,
                                      relationship,
                                      false,
                                      "",
                                      false);
    }
  }

  private void printDefinition(final DefinedObject definedObject)
  {
    if (definedObject == null || !definedObject.hasDefinition())
    {
      return;
    }
    if (!isVerbose)
    {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Definition", "section");

    formattingHelper.writeNameRow("", "[definition]");
    formattingHelper.writeWideRow(definedObject.getDefinition(), "definition");
  }

  private void printDependantObjectDefinition(final DefinedObject definedObject)
  {
    if (definedObject == null || !definedObject.hasDefinition())
    {
      return;
    }
    if (!isVerbose)
    {
      return;
    }

    formattingHelper.writeWideRow(definedObject.getDefinition(), "definition");
  }

  private void printForeignKeys(final Table table)
  {
    final Collection<ForeignKey> foreignKeysCollection = table.getForeignKeys();
    if (foreignKeysCollection.isEmpty())
    {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Foreign Keys", "section");

    final List<ForeignKey> foreignKeys = new ArrayList<>(foreignKeysCollection);
    Collections
      .sort(foreignKeys,
            NamedObjectSort
              .getNamedObjectSort(options.isAlphabeticalSortForForeignKeys()));

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

        formattingHelper.writeEmptyRow();

        String fkName = "";
        if (!options.isHideForeignKeyNames())
        {
          fkName = name;
        }
        final String fkDetails = "[foreign key" + ruleString + "]";
        formattingHelper.writeNameRow(fkName, fkDetails);
        printColumnReferences(true, table, foreignKey);
        printDependantObjectDefinition(foreignKey);
      }
    }
  }

  private void printIndexes(final Collection<Index> indexesCollection)
  {
    if (indexesCollection.isEmpty())
    {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Indexes", "section");

    final List<Index> indexes = new ArrayList<>(indexesCollection);
    Collections
      .sort(indexes,
            NamedObjectSort
              .getNamedObjectSort(options.isAlphabeticalSortForIndexes()));

    for (final Index index: indexes)
    {
      if (index != null)
      {
        formattingHelper.writeEmptyRow();

        String indexName = "";
        if (!options.isHideIndexNames())
        {
          indexName = index.getName();
        }
        final IndexType indexType = index.getIndexType();
        String indexTypeString = "";
        if (indexType != IndexType.unknown && indexType != IndexType.other)
        {
          indexTypeString = indexType.toString() + SPACE;
        }
        final String indexDetails = "[" + (index.isUnique()? "": "non-")
                                    + "unique " + indexTypeString + "index]";
        formattingHelper.writeNameRow(indexName, indexDetails);

        if (!isBrief)
        {
          printTableColumns(index.getColumns(), true);
        }
        printDependantObjectDefinition(index);
      }
    }
  }

  private void printPrimaryKey(final Index primaryKey)
  {
    if (primaryKey != null)
    {
      formattingHelper.writeEmptyRow();
      formattingHelper.writeWideRow("Primary Key", "section");

      formattingHelper.writeEmptyRow();

      final String name = primaryKey.getName();
      String pkName = "";
      if (!options.isHidePrimaryKeyNames())
      {
        pkName = name;
      }
      if (isBlank(pkName))
      {
        pkName = "";
      }
      formattingHelper.writeNameRow(pkName, "[primary key]");
      printTableColumns(primaryKey.getColumns(), false);
      printDependantObjectDefinition(primaryKey);
    }
  }

  private void printPrivileges(final Collection<Privilege<Table>> privileges)
  {
    if (privileges.isEmpty())
    {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Privileges and Grants", "section");

    for (final Privilege<Table> privilege: privileges)
    {
      if (privilege != null)
      {
        formattingHelper.writeEmptyRow();
        formattingHelper.writeNameRow(privilege.getName(), "[privilege]");
        for (final Grant<?> grant: privilege.getGrants())
        {
          final String grantedFrom = String
            .format("%s %s %s%s",
                    grant.getGrantor(),
                    formattingHelper.createRightArrow(),
                    grant.getGrantee(),
                    grant.isGrantable()? " (grantable)": "");
          formattingHelper.writeDetailRow("", grantedFrom, "");
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
    formattingHelper.writeWideRow(object.getRemarks(), "");
  }

  private void printRoutineColumns(final List<? extends RoutineColumn<?>> columns)
  {
    if (columns.isEmpty())
    {
      return;
    }

    Collections.sort(columns,
                     NamedObjectSort.getNamedObjectSort(options
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
      final StringBuilder columnType = new StringBuilder(64);
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
      formattingHelper.writeDetailRow(ordinalNumberString,
                                      column.getName(),
                                      columnType.toString());
    }
  }

  private void printTableColumnAutoIncremented(final Column column)
  {
    if (column == null)
    {
      return;
    }
    try
    {
      if (!column.isAutoIncremented())
      {
        return;
      }
    }
    catch (final NotLoadedException e)
    {
      // The column may be partial for index pseudo-columns
      return;
    }
    formattingHelper.writeDetailRow("", "", "auto-incremented");
  }

  private void printTableColumnRemarks(final Column column)
  {
    if (column == null || !column.hasRemarks() || options.isHideRemarks())
    {
      return;
    }
    formattingHelper.writeDetailRow("", "", column.getRemarks());
  }

  private void printTableColumns(final List<? extends Column> columns,
                                 final boolean showHidden)
  {
    if (columns.isEmpty())
    {
      return;
    }

    Collections
      .sort(columns,
            NamedObjectSort
              .getNamedObjectSort(options.isAlphabeticalSortForTableColumns()));

    for (final Column column: columns)
    {
      if (!showHidden && column.isHidden())
      {
        continue;
      }
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
      formattingHelper.writeDetailRow(ordinalNumberString,
                                      columnName,
                                      true,
                                      columnDetails,
                                      emphasize);

      printTableColumnAutoIncremented(column);
      printTableColumnRemarks(column);
      if (column instanceof DefinedObject)
      {
        printDependantObjectDefinition((DefinedObject) column);
      }
    }
  }

  private void printTableConstraints(final Collection<TableConstraint> constraintsCollection)
  {

    final List<TableConstraint> constraints = constraintsCollection.stream()
      .filter(constraint -> (EnumSet
        .of(TableConstraintType.check, TableConstraintType.unique)
        .contains(constraint.getConstraintType())))
      .collect(Collectors.toList());
    Collections
      .sort(constraints,
            NamedObjectSort
              .getNamedObjectSort(options.isAlphabeticalSortForIndexes()));

    if (constraints.isEmpty())
    {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Table Constraints", "section");

    for (final TableConstraint constraint: constraints)
    {
      if (constraint != null)
      {
        String constraintName = "";
        if (!options.isHideTableConstraintNames())
        {
          constraintName = constraint.getName();
        }
        final String constraintType = constraint.getConstraintType().getValue()
          .toLowerCase();
        final String constraintDetails = "[" + constraintType + " constraint]";
        formattingHelper.writeEmptyRow();
        formattingHelper.writeNameRow(constraintName, constraintDetails);

        if (!isBrief)
        {
          printTableColumns(constraint.getColumns(), true);
        }
        printDependantObjectDefinition(constraint);
      }
    }
  }

  private void printTableRowCount(final Table table)
  {
    if (!options.isShowRowCounts() || table == null || !hasRowCount(table))
    {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Additional Information", "section");

    formattingHelper.writeEmptyRow();
    formattingHelper.writeNameRow(getRowCountMessage(table), "[row count]");
  }

  private void printTriggers(final Collection<Trigger> triggers)
  {
    if (triggers.isEmpty())
    {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Triggers", "section");

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
          timing = ", " + conditionTiming + SPACE + eventManipulationType;
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
        formattingHelper.writeEmptyRow();

        final String triggerName;
        if (options.isHideTriggerNames())
        {
          triggerName = "";
        }
        else
        {
          triggerName = trigger.getName();
        }

        formattingHelper.writeNameRow(triggerName, triggerType);

        if (!isBlank(actionCondition))
        {
          formattingHelper.writeWideRow(actionCondition, "definition");
        }
        if (!isBlank(actionStatement))
        {
          formattingHelper.writeWideRow(actionStatement, "definition");
        }
      }
    }
  }

  private void printWeakAssociations(final Table table)
  {
    final Collection<WeakAssociationForeignKey> weakAssociationsCollection = WeakAssociationsUtility
      .getWeakAssociations(table);
    if (weakAssociationsCollection.isEmpty())
    {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Weak Associations", "section");

    final List<WeakAssociationForeignKey> weakAssociations = new ArrayList<>(weakAssociationsCollection);
    Collections.sort(weakAssociations);
    for (final WeakAssociationForeignKey weakFk: weakAssociations)
    {
      if (weakFk != null)
      {
        formattingHelper.writeEmptyRow();

        final String fkDetails = "[weak association]";
        formattingHelper.writeNameRow("", fkDetails);
        printColumnReferences(false, table, weakFk);
      }
    }
  }
}

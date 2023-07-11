/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.text.formatter.schema;

import static java.util.Comparator.naturalOrder;
import static schemacrawler.loader.counts.TableRowCountsUtility.getRowCountMessage;
import static schemacrawler.loader.counts.TableRowCountsUtility.hasRowCount;
import static schemacrawler.schema.DataTypeType.user_defined;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectsType.hideAlternateKeys;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectsType.hideForeignKeys;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectsType.hidePrimaryKeys;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectsType.hideRoutineParameters;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectsType.hideTableColumns;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectsType.hideTableConstraints;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectsType.hideTriggers;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectsType.hideWeakAssociations;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import schemacrawler.crawl.NotLoadedException;
import schemacrawler.schema.ActionOrientationType;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ConditionTimingType;
import schemacrawler.schema.DefinedObject;
import schemacrawler.schema.DescribedObject;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyUpdateRule;
import schemacrawler.schema.Grant;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineParameter;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintColumn;
import schemacrawler.schema.TableConstraintType;
import schemacrawler.schema.TableReference;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.View;
import schemacrawler.schema.WeakAssociation;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.formatter.base.BaseTabularFormatter;
import schemacrawler.tools.text.formatter.base.helper.TextFormattingHelper.DocumentHeaderType;
import schemacrawler.tools.traversal.SchemaTraversalHandler;
import schemacrawler.utility.MetaDataUtility;
import schemacrawler.utility.MetaDataUtility.ForeignKeyCardinality;
import schemacrawler.utility.NamedObjectSort;

/** Text formatting of schema. */
public final class SchemaTextFormatter extends BaseTabularFormatter<SchemaTextOptions>
    implements SchemaTraversalHandler {

  private static final String SPACE = " ";

  private static String negate(final boolean positive, final String text) {
    String textValue = text;
    if (!positive) {
      textValue = "not " + textValue;
    }
    return textValue;
  }

  /**
   * Text formatting of schema.
   *
   * @param schemaTextDetailType Types for text formatting of schema
   * @param options Options for text formatting of schema
   * @param outputOptions Options for text formatting of schema
   * @param identifierQuoteString Quote character for identifier
   */
  public SchemaTextFormatter(
      final SchemaTextDetailType schemaTextDetailType,
      final SchemaTextOptions options,
      final OutputOptions outputOptions,
      final Identifiers identifiers) {
    super(schemaTextDetailType, options, outputOptions, identifiers);
  }

  /** {@inheritDoc} */
  @Override
  public void handle(final ColumnDataType columnDataType) {
    if (printVerboseDatabaseInfo() && isVerbose()) {
      formattingHelper.writeObjectStart();
      printColumnDataType(columnDataType);
      formattingHelper.writeObjectEnd();
    }
  }

  /** {@inheritDoc} */
  @Override
  public void handle(final Routine routine) {
    final String routineTypeDetail =
        String.format("%s, %s", routine.getRoutineType(), routine.getReturnType());
    final String routineName = quoteName(routine);
    final String routineType = "[" + routineTypeDetail + "]";

    formattingHelper.println();
    formattingHelper.println();
    formattingHelper.writeObjectStart();
    formattingHelper.writeObjectNameRow(
        nodeId(routine), routineName, routineType, colorMap.getColor(routine));
    printRemarks(routine);

    if (!isBrief()) {
      printRoutineParameters(routine.getParameters());
    }

    if (isVerbose()) {
      if (!options.isHideRoutineSpecificNames()) {
        final String specificName = routine.getSpecificName();
        if (!isBlank(specificName)) {
          formattingHelper.writeEmptyRow();
          formattingHelper.writeNameRow("", "[specific name]");
          formattingHelper.writeWideRow(identifiers.quoteName(specificName), "");
        }
      }
      printDefinition(routine);
    }

    formattingHelper.writeObjectEnd();
  }

  /** {@inheritDoc} */
  @Override
  public void handle(final Sequence sequence) {
    final String sequenceName = quoteName(sequence);
    final String sequenceType = "[sequence]";

    formattingHelper.println();
    formattingHelper.println();
    formattingHelper.writeObjectStart();
    formattingHelper.writeObjectNameRow(
        nodeId(sequence), sequenceName, sequenceType, colorMap.getColor(sequence));
    printRemarks(sequence);

    if (!isBrief()) {
      formattingHelper.writeDetailRow("", "increment", String.valueOf(sequence.getIncrement()));
      formattingHelper.writeDetailRow(
          "", "start value", Objects.toString(sequence.getStartValue(), ""));
      formattingHelper.writeDetailRow(
          "", "minimum value", Objects.toString(sequence.getMinimumValue(), ""));
      formattingHelper.writeDetailRow(
          "", "maximum value", Objects.toString(sequence.getMaximumValue(), ""));
      formattingHelper.writeDetailRow("", "cycle", String.valueOf(sequence.isCycle()));
    }

    formattingHelper.writeObjectEnd();
  }

  /** {@inheritDoc} */
  @Override
  public void handle(final Synonym synonym) {
    final String synonymName = quoteName(synonym);
    final String synonymType = "[synonym]";

    formattingHelper.println();
    formattingHelper.println();
    formattingHelper.writeObjectStart();
    formattingHelper.writeObjectNameRow(
        nodeId(synonym), synonymName, synonymType, colorMap.getColor(synonym));
    printRemarks(synonym);

    if (!isBrief()) {
      final String referencedObjectName = quoteName(synonym.getReferencedObject());
      formattingHelper.writeDetailRow(
          "",
          String.format(
              "%s %s %s",
              identifiers.quoteName(synonym),
              formattingHelper.createRightArrow(),
              referencedObjectName),
          "");
    }

    formattingHelper.writeObjectEnd();
  }

  @Override
  public void handle(final Table table) {
    final String tableName = quoteName(table);
    final String tableType = "[" + table.getTableType() + "]";

    formattingHelper.println();
    formattingHelper.println();
    formattingHelper.writeObjectStart();
    formattingHelper.writeObjectNameRow(
        nodeId(table), tableName, tableType, colorMap.getColor(table));
    printRemarks(table);

    if (!options.get(hideTableColumns)) {
      printTableColumns(table.getColumns(), true);
      if (isVerbose()) {
        printTableColumns(new ArrayList<>(table.getHiddenColumns()), true);
      }
    }

    printPrimaryKey(table.getPrimaryKey());
    printForeignKeys(table);
    if (!isBrief()) {
      printAlternateKeys(table);
      printWeakAssociations(table);
      printIndexes(table.getIndexes());
      printTriggers(table.getTriggers());
      printTableConstraints(table.getTableConstraints());

      if (isVerbose()) {
        printPrivileges(table.getPrivileges());
        printDefinition(table);
        printViewTableUsage(table);
      }

      printTableRowCount(table);
    }

    formattingHelper.writeObjectEnd();
  }

  /** {@inheritDoc} */
  @Override
  public void handleColumnDataTypesEnd() {
    // No output required
  }

  /** {@inheritDoc} */
  @Override
  public void handleColumnDataTypesStart() {
    if (printVerboseDatabaseInfo() && isVerbose()) {
      formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Data Types");
    }
  }

  /** {@inheritDoc} */
  @Override
  public void handleRoutinesEnd() {
    // No output required
  }

  /** {@inheritDoc} */
  @Override
  public void handleRoutinesStart() {
    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Routines");
  }

  /** {@inheritDoc} */
  @Override
  public void handleSequencesEnd() {
    // No output required
  }

  /** {@inheritDoc} */
  @Override
  public void handleSequencesStart() {
    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Sequences");
  }

  /** {@inheritDoc} */
  @Override
  public void handleSynonymsEnd() {
    // No output required
  }

  /** {@inheritDoc} */
  @Override
  public void handleSynonymsStart() {
    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Synonyms");
  }

  /** {@inheritDoc} */
  @Override
  public void handleTablesEnd() {
    // No output required
  }

  /** {@inheritDoc} */
  @Override
  public void handleTablesStart() {
    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Tables");
  }

  private void printAlternateKeys(final Table table) {
    if (table == null || options.get(hideAlternateKeys)) {
      return;
    }

    final Collection<PrimaryKey> alternateKeys = table.getAlternateKeys();
    if (alternateKeys == null || alternateKeys.isEmpty()) {
      return;
    }
    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Alternate Keys", "section");

    formattingHelper.writeEmptyRow();

    for (final TableConstraint alternateKey : alternateKeys) {
      final String name = identifiers.quoteName(alternateKey);
      final String akName;
      if (!options.isHideAlternateKeyNames()) {
        akName = name;
      } else {
        akName = "";
      }
      final String type = alternateKey.getType().getValue().toLowerCase();
      formattingHelper.writeNameRow(akName, "[" + type + "]");
      printRemarks(alternateKey);
      printTableColumns(alternateKey.getConstrainedColumns(), false);
    }
  }

  private void printColumnDataType(final ColumnDataType columnDataType) {

    final boolean isUserDefined = columnDataType.getType() == user_defined;
    final String dataType = String.format("[%sdata type]", isUserDefined ? "user defined " : "");

    final String typeName;
    if (options.isShowUnqualifiedNames()) {
      typeName = columnDataType.getName();
    } else {
      typeName = columnDataType.getFullName();
    }
    if (isBlank(typeName)) {
      // In some cases, JDBC drivers may not return a data type name
      return;
    }

    final String nullable = negate(columnDataType.isNullable(), "nullable");

    final String autoIncrementable =
        negate(columnDataType.isAutoIncrementable(), "auto-incrementable");

    final String createParameters = columnDataType.getCreateParameters();
    final String definedWith =
        "defined with " + (isBlank(createParameters) ? "no parameters" : createParameters);

    formattingHelper.writeNameRow(typeName, dataType);
    formattingHelper.writeDescriptionRow(definedWith);
    formattingHelper.writeDescriptionRow(nullable);
    formattingHelper.writeDescriptionRow(autoIncrementable);
    formattingHelper.writeDescriptionRow(columnDataType.getSearchable().toString());
    if (isUserDefined) {
      final String baseTypeName;
      final ColumnDataType baseColumnDataType = columnDataType.getBaseType();
      if (baseColumnDataType == null) {
        baseTypeName = "";
      } else if (options.isShowUnqualifiedNames()) {
        baseTypeName = baseColumnDataType.getName();
      } else {
        baseTypeName = baseColumnDataType.getFullName();
      }
      formattingHelper.writeDetailRow("", "based on", baseTypeName);

      final String remarks = columnDataType.getRemarks();
      if (!isBlank(remarks)) {
        formattingHelper.writeDetailRow("", "remarks", remarks);
      }
    }
  }

  private void printColumnReferences(
      final boolean isForeignKey, final Table table, final TableReference foreignKey) {
    final ForeignKeyCardinality fkCardinality =
        MetaDataUtility.findForeignKeyCardinality(foreignKey);
    for (final ColumnReference columnRef : foreignKey) {

      final Column pkColumn = columnRef.getPrimaryKeyColumn();
      final Column fkColumn = columnRef.getForeignKeyColumn();

      final Table referencedTable = columnRef.getPrimaryKeyColumn().getParent();
      final Table referencingTable = columnRef.getForeignKeyColumn().getParent();

      final boolean isPkColumnFiltered = isTableFiltered(referencedTable);
      final boolean isFkColumnFiltered = isTableFiltered(referencingTable);

      final String pkColumnName;
      final String fkColumnName;

      boolean isIncoming = false;
      if (referencedTable.equals(table)) {
        pkColumnName = identifiers.quoteName(pkColumn);
        isIncoming = true;
      } else if (options.isShowUnqualifiedNames()) {
        pkColumnName = identifiers.quoteShortName(pkColumn);
      } else {
        pkColumnName = identifiers.quoteFullName(pkColumn);
      }
      if (referencingTable.equals(table)) {
        fkColumnName = identifiers.quoteName(fkColumn);
      } else if (options.isShowUnqualifiedNames()) {
        fkColumnName = identifiers.quoteShortName(fkColumn);
      } else {
        fkColumnName = identifiers.quoteFullName(fkColumn);
      }
      String keySequenceString = "";
      if (options.isShowOrdinalNumbers()) {
        final int keySequence = columnRef.getKeySequence();
        keySequenceString = String.format("%2d", keySequence);
      }

      final String relationship;
      if (isIncoming) {
        final String fkHyperlink;
        if (isFkColumnFiltered) {
          fkHyperlink = fkColumnName;
        } else {
          fkHyperlink = formattingHelper.createAnchor(fkColumnName, "#" + nodeId(referencingTable));
        }
        final String arrow =
            isForeignKey
                ? formattingHelper.createLeftArrow()
                : formattingHelper.createWeakLeftArrow();
        relationship =
            String.format("%s %s%s %s", pkColumnName, arrow, fkCardinality.toString(), fkHyperlink);
      } else {
        final String pkHyperlink;
        if (isPkColumnFiltered) {
          pkHyperlink = pkColumnName;
        } else {
          pkHyperlink = formattingHelper.createAnchor(pkColumnName, "#" + nodeId(referencedTable));
        }
        final String arrow =
            isForeignKey
                ? formattingHelper.createRightArrow()
                : formattingHelper.createWeakRightArrow();
        relationship =
            String.format("%s %s%s %s", fkColumnName, fkCardinality.toString(), arrow, pkHyperlink);
      }
      formattingHelper.writeDetailRow(keySequenceString, relationship, "", false, false, "");
    }
  }

  private void printDefinition(final DefinedObject definedObject) {
    if (definedObject == null || !definedObject.hasDefinition() || !isVerbose()) {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Definition", "section");

    formattingHelper.writeNameRow("", "[definition]");
    formattingHelper.writeWideRow(definedObject.getDefinition(), "definition");
  }

  private void printDependantObjectDefinition(final DefinedObject definedObject) {
    if (definedObject == null || !definedObject.hasDefinition() || !isVerbose()) {
      return;
    }

    formattingHelper.writeWideRow(definedObject.getDefinition(), "definition");
  }

  private void printForeignKeys(final Table table) {
    if (table == null || options.get(hideForeignKeys)) {
      return;
    }

    final Collection<ForeignKey> foreignKeysCollection = table.getForeignKeys();
    if (foreignKeysCollection.isEmpty()) {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Foreign Keys", "section");

    final List<ForeignKey> foreignKeys = new ArrayList<>(foreignKeysCollection);
    Collections.sort(
        foreignKeys,
        NamedObjectSort.getNamedObjectSort(options.isAlphabeticalSortForForeignKeys()));

    for (final ForeignKey foreignKey : foreignKeys) {
      if (foreignKey != null) {
        final String name = identifiers.quoteName(foreignKey);

        String updateRuleString = "";
        final ForeignKeyUpdateRule updateRule = foreignKey.getUpdateRule();
        if (updateRule != null && updateRule != ForeignKeyUpdateRule.unknown) {
          updateRuleString = ", on update " + updateRule.toString();
        }

        String deleteRuleString = "";
        final ForeignKeyUpdateRule deleteRule = foreignKey.getDeleteRule();
        if (deleteRule != null && deleteRule != ForeignKeyUpdateRule.unknown) {
          deleteRuleString = ", on delete " + deleteRule.toString();
        }

        final String ruleString;
        if (deleteRule != null
            && updateRule == deleteRule
            && updateRule != ForeignKeyUpdateRule.unknown) {
          ruleString = ", with " + deleteRule.toString();
        } else {
          ruleString = updateRuleString + deleteRuleString;
        }

        formattingHelper.writeEmptyRow();

        String fkName = "";
        if (!options.isHideForeignKeyNames()) {
          fkName = name;
        }
        final String fkDetails = "[foreign key" + ruleString + "]";
        formattingHelper.writeNameRow(fkName, fkDetails);
        printRemarks(foreignKey);
        printColumnReferences(true, table, foreignKey);
        printDependantObjectDefinition(foreignKey);
      }
    }
  }

  private void printIndexes(final Collection<Index> indexesCollection) {
    if (indexesCollection.isEmpty()) {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Indexes", "section");

    final List<Index> indexes = new ArrayList<>(indexesCollection);
    Collections.sort(
        indexes, NamedObjectSort.getNamedObjectSort(options.isAlphabeticalSortForIndexes()));

    for (final Index index : indexes) {
      if (index != null) {
        formattingHelper.writeEmptyRow();

        String indexName = "";
        if (!options.isHideIndexNames()) {
          indexName = identifiers.quoteName(index);
        }
        final IndexType indexType = index.getIndexType();
        String indexTypeString = "";
        if (indexType != IndexType.unknown && indexType != IndexType.other) {
          indexTypeString = indexType.toString() + SPACE;
        }
        final String indexDetails =
            "[" + (index.isUnique() ? "" : "non-") + "unique " + indexTypeString + "index]";
        formattingHelper.writeNameRow(indexName, indexDetails);

        printRemarks(index);

        if (!isBrief()) {
          printTableColumns(index.getColumns(), false);
        }
        printDependantObjectDefinition(index);
      }
    }
  }

  private void printPrimaryKey(final PrimaryKey primaryKey) {
    if (primaryKey == null || options.get(hidePrimaryKeys)) {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Primary Key", "section");

    formattingHelper.writeEmptyRow();

    final String name = identifiers.quoteName(primaryKey);
    String pkName = "";
    if (!options.isHidePrimaryKeyNames()) {
      pkName = name;
    }
    pkName = trimToEmpty(pkName);
    formattingHelper.writeNameRow(pkName, "[primary key]");
    printRemarks(primaryKey);
    printTableColumns(primaryKey.getConstrainedColumns(), false);
    printDependantObjectDefinition(primaryKey);
  }

  private void printPrivileges(final Collection<Privilege<Table>> privileges) {
    if (privileges.isEmpty()) {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Privileges and Grants", "section");

    for (final Privilege<Table> privilege : privileges) {
      if (privilege != null) {
        String privilegeName = privilege.getName();
        if (isBlank(privilegeName)) {
          // Privilege names can be null or blank
          privilegeName = "";
        }
        formattingHelper.writeEmptyRow();
        formattingHelper.writeNameRow(privilegeName, "[privilege]");
        for (final Grant<?> grant : privilege.getGrants()) {
          final String grantor = isBlank(grant.getGrantor()) ? "" : grant.getGrantor();
          final String grantee = isBlank(grant.getGrantee()) ? "" : grant.getGrantee();
          final String grantedFrom =
              String.format(
                  "%s %s %s%s",
                  grantor,
                  formattingHelper.createRightArrow(),
                  grantee,
                  grant.isGrantable() ? " (grantable)" : "");
          formattingHelper.writeDetailRow("", grantedFrom, "");
        }
      }
    }
  }

  private void printRemarks(final DescribedObject object) {
    if (object == null || !object.hasRemarks() || options.isHideRemarks()) {
      return;
    }
    formattingHelper.writeWideRow(object.getRemarks(), "remarks");
  }

  private void printRoutineParameters(final List<? extends RoutineParameter<?>> parameters) {
    if (parameters.isEmpty() || options.get(hideRoutineParameters)) {
      return;
    }

    parameters.sort(
        NamedObjectSort.getNamedObjectSort(options.isAlphabeticalSortForRoutineParameters()));

    for (final RoutineParameter<?> parameter : parameters) {
      final String columnTypeName;
      if (options.isShowStandardColumnTypeNames()) {
        columnTypeName = parameter.getColumnDataType().getJavaSqlType().getName();
      } else {
        columnTypeName = parameter.getColumnDataType().getDatabaseSpecificTypeName();
      }
      final StringBuilder columnType = new StringBuilder(64);
      columnType.append(columnTypeName).append(parameter.getWidth());
      if (parameter.getParameterMode() != null) {
        columnType.append(", ").append(parameter.getParameterMode().toString());
      }

      String ordinalNumberString = "";
      if (options.isShowOrdinalNumbers()) {
        ordinalNumberString = String.valueOf(parameter.getOrdinalPosition() + 1);
      }
      formattingHelper.writeDetailRow(
          ordinalNumberString, identifiers.quoteName(parameter), columnType.toString());
    }
  }

  private void printTableColumnAutoIncremented(final Column column) {
    if (column == null) {
      return;
    }
    try {
      if (!column.isAutoIncremented()) {
        return;
      }
    } catch (final NotLoadedException e) {
      // The column may be partial for index pseudo-columns
      return;
    }
    formattingHelper.writeDetailRow("", "", "auto-incremented");
  }

  private void printTableColumnEnumValues(final Column column) {
    if (column == null
        || !column.isColumnDataTypeKnown()
        || !column.getColumnDataType().isEnumerated()) {
      return;
    }
    final String enumValues =
        String.format("'%s'", String.join("', ", column.getColumnDataType().getEnumValues()));
    formattingHelper.writeDetailRow("", "", enumValues);
  }

  private void printTableColumnGenerated(final Column column) {
    if (column == null) {
      return;
    }
    try {
      if (!column.isGenerated()) {
        return;
      }
    } catch (final NotLoadedException e) {
      // The column may be partial for index pseudo-columns
      return;
    }
    formattingHelper.writeDetailRow("", "", "generated");
  }

  private void printTableColumnHidden(final Column column) {
    if (column == null) {
      return;
    }
    try {
      if (!column.isHidden()) {
        return;
      }
    } catch (final NotLoadedException e) {
      // The column may be partial for index pseudo-columns
      return;
    }
    formattingHelper.writeDetailRow("", "", "hidden");
  }

  private void printTableColumnRemarks(final Column column) {
    if (column == null || !column.hasRemarks() || options.isHideRemarks()) {
      return;
    }
    formattingHelper.writeDetailRow("", "", column.getRemarks(), true, false, "remarks");
  }

  private void printTableColumns(final List<? extends Column> columns, final boolean extraDetails) {
    if (columns.isEmpty()) {
      return;
    }

    Collections.sort(
        columns, NamedObjectSort.getNamedObjectSort(options.isAlphabeticalSortForTableColumns()));

    for (final Column column : columns) {
      if (!isColumnSignificant(column)) {
        continue;
      }

      final String columnName = identifiers.quoteName(column);

      final String columnDetails;

      boolean emphasize = false;
      if (column instanceof IndexColumn) {
        columnDetails = ((IndexColumn) column).getSortSequence().name();
      } else if (column instanceof TableConstraintColumn) {
        columnDetails = "";
      } else {
        final String columnTypeName;
        if (options.isShowStandardColumnTypeNames()) {
          columnTypeName = column.getColumnDataType().getJavaSqlType().getName();
        } else {
          columnTypeName = column.getColumnDataType().getDatabaseSpecificTypeName();
        }
        final String columnType = columnTypeName + column.getWidth();
        final String nullable = columnNullable(columnTypeName, column.isNullable());
        columnDetails = columnType + nullable;
        emphasize = column.isPartOfPrimaryKey();
      }

      String ordinalNumberString = "";
      if (options.isShowOrdinalNumbers()) {
        ordinalNumberString = String.valueOf(column.getOrdinalPosition());
      }
      formattingHelper.writeDetailRow(
          ordinalNumberString, columnName, columnDetails, true, emphasize, "");

      if (extraDetails) {
        printTableColumnEnumValues(column);
        printTableColumnHidden(column);
        printTableColumnAutoIncremented(column);
        printTableColumnGenerated(column);
        printTableColumnRemarks(column);

        if (column instanceof DefinedObject) {
          printDependantObjectDefinition((DefinedObject) column);
        }
      }
    }
  }

  private void printTableConstraints(final Collection<TableConstraint> constraintsCollection) {
    if (options.get(hideTableConstraints)) {
      return;
    }

    final EnumSet<TableConstraintType> printableConstraints =
        EnumSet.of(TableConstraintType.check, TableConstraintType.unique);

    final List<TableConstraint> constraints = new ArrayList<>();
    for (final TableConstraint constraint : constraintsCollection) {
      if (printableConstraints.contains(constraint.getType())) {
        constraints.add(constraint);
      }
    }
    if (constraints.isEmpty()) {
      return;
    }

    Collections.sort(
        constraints, NamedObjectSort.getNamedObjectSort(options.isAlphabeticalSortForIndexes()));

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Table Constraints", "section");

    for (final TableConstraint constraint : constraints) {
      if (constraint != null) {
        String constraintName = "";
        if (!options.isHideTableConstraintNames()) {
          constraintName = identifiers.quoteName(constraint);
        }
        final String constraintType = constraint.getType().getValue().toLowerCase();
        final String constraintDetails = "[" + constraintType + " constraint]";
        formattingHelper.writeEmptyRow();
        formattingHelper.writeNameRow(constraintName, constraintDetails);

        printRemarks(constraint);
        if (!isBrief()) {
          printTableColumns(constraint.getConstrainedColumns(), false);
        }
        printDependantObjectDefinition(constraint);
      }
    }
  }

  private void printTableRowCount(final Table table) {
    if (options.isHideTableRowCounts() || table == null || !hasRowCount(table)) {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Additional Information", "section");

    formattingHelper.writeEmptyRow();
    formattingHelper.writeNameRow(getRowCountMessage(table), "[row count]");
  }

  private void printTriggers(final Collection<Trigger> triggers) {
    if (triggers.isEmpty() || options.get(hideTriggers)) {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Triggers", "section");

    for (final Trigger trigger : triggers) {
      if (trigger != null) {
        String timing = "";
        final ConditionTimingType conditionTiming = trigger.getConditionTiming();
        final EventManipulationType eventManipulationType = trigger.getEventManipulationType();
        if (conditionTiming != null
            && conditionTiming != ConditionTimingType.unknown
            && eventManipulationType != null
            && eventManipulationType != EventManipulationType.unknown) {
          timing = ", " + conditionTiming + SPACE + eventManipulationType;
        }
        String orientation = "";
        if (trigger.getActionOrientation() != null
            && trigger.getActionOrientation() != ActionOrientationType.unknown) {
          orientation = ", per " + trigger.getActionOrientation();
        }
        String triggerType = "[trigger" + timing + orientation + "]";
        triggerType = triggerType.toLowerCase(Locale.ENGLISH);
        final String actionCondition = trigger.getActionCondition();
        final String actionStatement = trigger.getActionStatement();
        formattingHelper.writeEmptyRow();

        final String triggerName;
        if (options.isHideTriggerNames()) {
          triggerName = "";
        } else {
          triggerName = identifiers.quoteName(trigger);
        }

        formattingHelper.writeNameRow(triggerName, triggerType);

        if (!isBlank(actionCondition)) {
          formattingHelper.writeWideRow(actionCondition, "definition");
        }
        if (!isBlank(actionStatement)) {
          formattingHelper.writeWideRow(actionStatement, "definition");
        }
      }
    }
  }

  private void printViewTableUsage(final Table table) {
    if (table == null || !(table instanceof View)) {
      return;
    }
    final View view = (View) table;
    final Collection<Table> tableUsage = view.getTableUsage();
    if (tableUsage.isEmpty()) {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Table Usage", "section");

    formattingHelper.writeEmptyRow();
    for (final Table usedTable : tableUsage) {
      final String tableName = quoteName(usedTable);
      final String tableType = "[" + usedTable.getTableType() + "]";
      formattingHelper.writeNameRow(tableName, tableType);
    }
  }

  private void printWeakAssociations(final Table table) {
    if (table == null || options.get(hideWeakAssociations)) {
      return;
    }

    final Collection<WeakAssociation> weakAssociationsCollection = table.getWeakAssociations();
    if (weakAssociationsCollection == null || weakAssociationsCollection.isEmpty()) {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Weak Associations", "section");

    final List<WeakAssociation> weakAssociations = new ArrayList<>(weakAssociationsCollection);
    weakAssociations.sort(naturalOrder());
    for (final WeakAssociation weakAssociation : weakAssociations) {
      if (weakAssociation != null) {
        final String name = identifiers.quoteName(weakAssociation);

        formattingHelper.writeEmptyRow();

        String fkName = "";
        if (!options.isHideWeakAssociationNames()) {
          fkName = name;
        }
        final String fkDetails = "[weak association]";
        formattingHelper.writeNameRow(fkName, fkDetails);
        printRemarks(weakAssociation);
        printColumnReferences(false, table, weakAssociation);
      }
    }
  }
}

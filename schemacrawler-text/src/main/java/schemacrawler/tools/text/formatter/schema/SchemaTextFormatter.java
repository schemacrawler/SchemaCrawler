/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.text.formatter.schema;

import static java.util.Comparator.naturalOrder;
import static schemacrawler.loader.counts.TableRowCountsUtility.getRowCountMessage;
import static schemacrawler.loader.counts.TableRowCountsUtility.hasRowCount;
import static schemacrawler.schema.DataTypeType.user_defined;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectNamesType.hideAlternateKeyNames;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectNamesType.hideForeignKeyNames;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectNamesType.hideIndexNames;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectNamesType.hidePrimaryKeyNames;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectNamesType.hideRoutineSpecificNames;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectNamesType.hideTableConstraintNames;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectNamesType.hideTriggerNames;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectNamesType.hideWeakAssociationNames;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectsType.hideRoutines;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectsType.hideSequences;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectsType.hideSynonyms;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectsType.hideTables;
import static schemacrawler.tools.command.text.schema.options.HideDependantDatabaseObjectsType.hideAlternateKeys;
import static schemacrawler.tools.command.text.schema.options.HideDependantDatabaseObjectsType.hideForeignKeys;
import static schemacrawler.tools.command.text.schema.options.HideDependantDatabaseObjectsType.hideIndexes;
import static schemacrawler.tools.command.text.schema.options.HideDependantDatabaseObjectsType.hidePrimaryKeys;
import static schemacrawler.tools.command.text.schema.options.HideDependantDatabaseObjectsType.hideRoutineParameters;
import static schemacrawler.tools.command.text.schema.options.HideDependantDatabaseObjectsType.hideTableColumns;
import static schemacrawler.tools.command.text.schema.options.HideDependantDatabaseObjectsType.hideTableConstraints;
import static schemacrawler.tools.command.text.schema.options.HideDependantDatabaseObjectsType.hideTriggers;
import static schemacrawler.tools.command.text.schema.options.HideDependantDatabaseObjectsType.hideWeakAssociations;
import static schemacrawler.utility.MetaDataUtility.isView;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.crawl.NotLoadedException;
import schemacrawler.schema.ActionOrientationType;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ConditionTimingType;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.DefinedObject;
import schemacrawler.schema.DescribedObject;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyUpdateRule;
import schemacrawler.schema.Grant;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.JdbcDriverProperty;
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
import schemacrawler.schema.TypedObject;
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
import us.fatehi.utility.ObjectToString;
import us.fatehi.utility.html.Alignment;
import us.fatehi.utility.property.Property;
import us.fatehi.utility.string.StringFormat;

/** Text formatting of schema. */
public final class SchemaTextFormatter extends BaseTabularFormatter<SchemaTextOptions>
    implements SchemaTraversalHandler {

  private static final Logger LOGGER = Logger.getLogger(SchemaTextFormatter.class.getName());

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
    if (routine == null || options.is(hideRoutines)) {
      LOGGER.log(Level.FINER, new StringFormat("Not showing routine <%s>", routine));
      return;
    }

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
      if (!options.is(hideRoutineSpecificNames)) {
        LOGGER.log(
            Level.FINER, new StringFormat("Not showing routine specific name for <%s>", routine));
        final String specificName = routine.getSpecificName();
        if (!isBlank(specificName) && !routine.getName().equals(specificName)) {
          formattingHelper.writeEmptyRow();
          formattingHelper.writeNameRow("", "[specific name]");
          formattingHelper.writeWideRow(identifiers.quoteName(specificName), "");
        }
      }
      printRoutineReferences(routine);
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
    if (synonym == null || options.is(hideSynonyms)) {
      LOGGER.log(Level.FINER, new StringFormat("Not showing synonym <%s>", synonym));
      return;
    }

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
    if (table == null || options.is(hideTables)) {
      LOGGER.log(Level.FINER, new StringFormat("Not showing table <%s>", table));
      return;
    }

    final String tableName = quoteName(table);
    final String tableType = "[" + table.getTableType() + "]";

    formattingHelper.println();
    formattingHelper.println();
    formattingHelper.writeObjectStart();
    formattingHelper.writeObjectNameRow(
        nodeId(table), tableName, tableType, colorMap.getColor(table));
    printRemarks(table);

    if (!options.is(hideTableColumns)) {
      LOGGER.log(Level.FINER, new StringFormat("Not showing table columns for <%s>", table));
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

  @Override
  public void handleInfo(final DatabaseInfo dbInfo) {
    if (!printVerboseDatabaseInfo() || !options.isShowDatabaseInfo() || dbInfo == null) {
      return;
    }

    final Collection<Property> serverInfo = dbInfo.getServerInfo();
    if (!serverInfo.isEmpty()) {
      formattingHelper.writeHeader(DocumentHeaderType.section, "Database Server Information");
      formattingHelper.writeObjectStart();
      for (final Property property : serverInfo) {
        final String name = property.getName();
        final Object value = property.getValue();
        formattingHelper.writeNameValueRow(
            name, ObjectToString.listOrObjectToString(value), Alignment.inherit);
      }
      formattingHelper.writeObjectEnd();
    }

    formattingHelper.writeHeader(DocumentHeaderType.section, "Database Information");

    formattingHelper.writeObjectStart();
    formattingHelper.writeNameValueRow(
        "database product name", dbInfo.getProductName(), Alignment.inherit);
    formattingHelper.writeNameValueRow(
        "database product version", dbInfo.getProductVersion(), Alignment.inherit);
    formattingHelper.writeNameValueRow(
        "database user name", dbInfo.getUserName(), Alignment.inherit);
    formattingHelper.writeObjectEnd();

    final Collection<Property> dbProperties = dbInfo.getProperties();
    if (!dbProperties.isEmpty()) {
      formattingHelper.writeHeader(DocumentHeaderType.section, "Database Characteristics");
      formattingHelper.writeObjectStart();
      for (final Property property : dbProperties) {
        final String name = property.getDescription();
        final Object value = property.getValue();
        formattingHelper.writeNameValueRow(
            name, ObjectToString.listOrObjectToString(value), Alignment.inherit);
      }
      formattingHelper.writeObjectEnd();
    }
  }

  @Override
  public void handleInfo(final JdbcDriverInfo driverInfo) {
    if (!printVerboseDatabaseInfo() || !options.isShowJdbcDriverInfo() || driverInfo == null) {
      return;
    }

    formattingHelper.writeHeader(DocumentHeaderType.section, "JDBC Driver Information");

    formattingHelper.writeObjectStart();
    formattingHelper.writeNameValueRow(
        "connection url", driverInfo.getConnectionUrl(), Alignment.inherit);
    formattingHelper.writeNameValueRow(
        "driver name", driverInfo.getProductName(), Alignment.inherit);
    formattingHelper.writeNameValueRow(
        "driver version", driverInfo.getProductVersion(), Alignment.inherit);
    if (driverInfo.hasDriverClassName()) {
      formattingHelper.writeNameValueRow(
          "driver class name", driverInfo.getDriverClassName(), Alignment.inherit);
      formattingHelper.writeNameValueRow(
          "is JDBC compliant", Boolean.toString(driverInfo.isJdbcCompliant()), Alignment.inherit);
      formattingHelper.writeNameValueRow(
          "supported JDBC version",
          String.format(
              "%d.%d", driverInfo.getJdbcMajorVersion(), driverInfo.getJdbcMinorVersion()),
          Alignment.inherit);
    }
    formattingHelper.writeObjectEnd();

    if (driverInfo.hasDriverClassName()) {
      final Collection<JdbcDriverProperty> jdbcDriverProperties = driverInfo.getDriverProperties();
      if (!jdbcDriverProperties.isEmpty()) {
        formattingHelper.writeHeader(DocumentHeaderType.section, "JDBC Driver Properties");
        for (final JdbcDriverProperty driverProperty : jdbcDriverProperties) {
          formattingHelper.writeObjectStart();
          printJdbcDriverProperty(driverProperty);
          formattingHelper.writeObjectEnd();
        }
      }
    }
  }

  @Override
  public void handleInfoEnd() {
    // Default implementation - NO-OP
  }

  @Override
  public void handleInfoStart() {
    if (!printVerboseDatabaseInfo()
        || options.isNoInfo()
        || !options.isShowDatabaseInfo() && !options.isShowJdbcDriverInfo()) {
      return;
    }

    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "System Information");
  }

  /** {@inheritDoc} */
  @Override
  public void handleRoutinesEnd() {
    // No output required
  }

  /** {@inheritDoc} */
  @Override
  public void handleRoutinesStart() {
    if (options.is(hideRoutines)) {
      LOGGER.log(Level.FINER, "Not showing routines");
      return;
    }

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
    if (options.is(hideSequences)) {
      LOGGER.log(Level.FINER, "Not showing sequences");
      return;
    }

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
    if (options.is(hideSynonyms)) {
      LOGGER.log(Level.FINER, "Not showing synonyms");
      return;
    }

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
    if (options.is(hideTables)) {
      LOGGER.log(Level.FINER, "Not showing tables");
      return;
    }

    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Tables");
  }

  private List<TableConstraint> filterPrintableConstraints(
      final Collection<TableConstraint> constraintsCollection) {
    final EnumSet<TableConstraintType> printableConstraints =
        EnumSet.of(TableConstraintType.check, TableConstraintType.unique);

    final List<TableConstraint> constraints = new ArrayList<>();
    for (final TableConstraint constraint : constraintsCollection) {
      // 1. There is no point in showing a constraint if there is no information
      // about the constrained columns, and the name is hidden
      final List<TableConstraintColumn> constrainedColumns = constraint.getConstrainedColumns();
      final boolean hasNoNameOrColumns =
          options.is(hideTableConstraintNames)
              && constrainedColumns.isEmpty()
              && !constraint.hasRemarks();
      // 2. Print only check constraints and unique constraints
      final boolean isNotPkOrFk = printableConstraints.contains(constraint.getType());
      // Keep only constraints that should be printed
      if (!hasNoNameOrColumns && isNotPkOrFk) {
        constraints.add(constraint);
      }
    }
    return constraints;
  }

  private String makeFkRuleString(final ForeignKey foreignKey) {
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
    return ruleString;
  }

  private void printAlternateKeys(final Table table) {
    if (table == null || options.is(hideAlternateKeys)) {
      LOGGER.log(Level.FINER, "Not showing alternate keys");
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
      if (!options.is(hideAlternateKeyNames)) {
        LOGGER.log(
            Level.FINER, new StringFormat("Not showing alternate key names for <%s>", table));
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
      final Table dependentTable = columnRef.getForeignKeyColumn().getParent();

      final boolean isPkColumnFiltered = isTableFiltered(referencedTable);
      final boolean isFkColumnFiltered = isTableFiltered(dependentTable);

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
      if (dependentTable.equals(table)) {
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
          fkHyperlink = formattingHelper.createAnchor(fkColumnName, "#" + nodeId(dependentTable));
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
    if (table == null || options.is(hideForeignKeys)) {
      LOGGER.log(Level.FINER, new StringFormat("Not showing foreign keys for <%s>", table));
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
        final String ruleString = makeFkRuleString(foreignKey);

        formattingHelper.writeEmptyRow();

        String fkName = "";
        if (!options.is(hideForeignKeyNames)) {
          LOGGER.log(
              Level.FINER, new StringFormat("Not showing foreign key names for <%s>", table));
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
    if (indexesCollection.isEmpty() || options.is(hideIndexes)) {
      LOGGER.log(Level.FINER, "Not showing indexes");
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
        if (!options.is(hideIndexNames)) {
          LOGGER.log(Level.FINER, new StringFormat("Not showing index names for <%s>", index));
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

        // Print filter condition
        if (index.hasFilterCondition()) {
          formattingHelper.writeWideRow(index.getFilterCondition(), "filter condition");
        }

        printRemarks(index);

        if (!isBrief()) {
          printTableColumns(index.getColumns(), false);
        }
        printDependantObjectDefinition(index);
      }
    }
  }

  private void printJdbcDriverProperty(final JdbcDriverProperty driverProperty) {
    final String required = (driverProperty.isRequired() ? "" : "not ") + "required";
    final StringBuilder details = new StringBuilder();
    details.append(required);
    if (driverProperty.getChoices() != null && !driverProperty.getChoices().isEmpty()) {
      details.append("; choices ").append(driverProperty.getChoices());
    }
    final String value = driverProperty.getValue();

    formattingHelper.writeNameRow(driverProperty.getName(), "[driver property]");
    formattingHelper.writeDescriptionRow(driverProperty.getDescription());
    formattingHelper.writeDescriptionRow(details.toString());
    formattingHelper.writeDetailRow("", "value", ObjectToString.listOrObjectToString(value));
  }

  private void printPrimaryKey(final PrimaryKey primaryKey) {
    if (primaryKey == null || options.is(hidePrimaryKeys)) {
      LOGGER.log(Level.FINER, new StringFormat("Not showing primary key <%s>", primaryKey));
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Primary Key", "section");

    formattingHelper.writeEmptyRow();

    final String name = identifiers.quoteName(primaryKey);
    String pkName = "";
    if (!options.is(hidePrimaryKeyNames)) {
      LOGGER.log(
          Level.FINER, new StringFormat("Not showing primary key name for <%s>", primaryKey));
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
    if (parameters.isEmpty() || options.is(hideRoutineParameters)) {
      LOGGER.log(Level.FINER, "Not showing routine parameters");
      return;
    }

    parameters.sort(
        NamedObjectSort.getNamedObjectSort(options.isAlphabeticalSortForRoutineParameters()));

    for (final RoutineParameter<?> parameter : parameters) {
      final String columnTypeName;
      if (options.isShowStandardColumnTypeNames()) {
        columnTypeName = parameter.getColumnDataType().getStandardTypeName();
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

  private void printRoutineReferences(final Routine routine) {
    if (routine == null) {
      return;
    }
    final Collection<? extends DatabaseObject> references = routine.getReferencedObjects();
    if (references.isEmpty()) {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("References", "section");

    formattingHelper.writeEmptyRow();
    for (final DatabaseObject reference : references) {
      final String objectName = quoteName(reference);
      final String objectType;
      if (reference instanceof TypedObject<?>) {
        objectType = "[" + ((TypedObject<?>) reference).getType() + "]";
      } else {
        objectType = "";
      }
      formattingHelper.writeNameRow(objectName, objectType);
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

  private void printTableColumnDefaultValue(final Column column) {
    if (column == null || !column.hasDefaultValue()) {
      return;
    }
    final String defaultValue = column.getDefaultValue();
    if ("NULL".equalsIgnoreCase(defaultValue)) {
      return;
    }
    formattingHelper.writeDetailRow("", "", "default " + defaultValue);
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
          columnTypeName = column.getColumnDataType().getStandardTypeName();
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
        printTableColumnDefaultValue(column);
        printTableColumnAutoIncremented(column);
        printTableColumnGenerated(column);
        printTableColumnEnumValues(column);
        printTableColumnHidden(column);
        printTableColumnRemarks(column);

        if (column instanceof DefinedObject) {
          printDependantObjectDefinition((DefinedObject) column);
        }
      }
    }
  }

  private void printTableConstraints(final Collection<TableConstraint> constraintsCollection) {
    if (options.is(hideTableConstraints)) {
      LOGGER.log(Level.FINER, "Not showing table constraints");
      return;
    }

    final List<TableConstraint> constraints = filterPrintableConstraints(constraintsCollection);
    if (constraints.isEmpty()) {
      return;
    }

    Collections.sort(
        constraints, NamedObjectSort.getNamedObjectSort(options.isAlphabeticalSortForIndexes()));

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Table Constraints", "section");

    for (final TableConstraint constraint : constraints) {
      if (constraint == null) {
        continue;
      }

      final String constraintName;
      if (!options.is(hideTableConstraintNames)) {
        LOGGER.log(
            Level.FINER,
            new StringFormat("Not showing table constraint name for <%s>", constraint));
        constraintName = identifiers.quoteName(constraint);
      } else {
        constraintName = "";
      }
      final String constraintType = constraint.getType().getValue().toLowerCase();
      final String constraintDetails = "[" + constraintType + " constraint]";
      formattingHelper.writeEmptyRow();
      formattingHelper.writeNameRow(constraintName, constraintDetails);

      printRemarks(constraint);
      if (!isBrief()) {
        final List<TableConstraintColumn> constrainedColumns = constraint.getConstrainedColumns();
        printTableColumns(constrainedColumns, false);
      }
      printDependantObjectDefinition(constraint);
    }
  }

  private void printTableRowCount(final Table table) {
    if (options.isHideTableRowCounts() || !hasRowCount(table)) {
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Additional Information", "section");

    formattingHelper.writeEmptyRow();
    formattingHelper.writeNameRow(getRowCountMessage(table), "[row count]");
  }

  private void printTriggers(final Collection<Trigger> triggers) {
    if (triggers.isEmpty() || options.is(hideTriggers)) {
      LOGGER.log(Level.FINER, "Not showing triggers");
      return;
    }

    formattingHelper.writeEmptyRow();
    formattingHelper.writeWideRow("Triggers", "section");

    for (final Trigger trigger : triggers) {
      if (trigger != null) {

        final StringBuilder timingBuffer = new StringBuilder();

        final ConditionTimingType conditionTiming = trigger.getConditionTiming();
        if (conditionTiming != null && conditionTiming != ConditionTimingType.unknown) {
          timingBuffer.append(conditionTiming);
        }
        final List<EventManipulationType> eventManipulationTypes =
            new ArrayList<>(trigger.getEventManipulationTypes());
        if (eventManipulationTypes != null
            && eventManipulationTypes.get(0) != EventManipulationType.unknown) {
          if (timingBuffer.length() > 0) {
            timingBuffer.append(SPACE);
          }
          for (final EventManipulationType eventManipulationType : eventManipulationTypes) {
            timingBuffer.append(eventManipulationType);
            if (eventManipulationTypes.indexOf(eventManipulationType)
                < eventManipulationTypes.size() - 1) {
              timingBuffer.append(" or ");
            }
          }
        }
        if (trigger.getActionOrientation() != null
            && trigger.getActionOrientation() != ActionOrientationType.unknown) {
          timingBuffer.append(", per ").append(trigger.getActionOrientation());
        }

        final String timing = timingBuffer.toString();

        String triggerType = "[trigger]";
        triggerType = triggerType.toLowerCase(Locale.ENGLISH);
        final String actionCondition = trigger.getActionCondition();
        final String actionStatement = trigger.getActionStatement();
        formattingHelper.writeEmptyRow();

        final String triggerName;
        if (options.is(hideTriggerNames)) {
          LOGGER.log(Level.FINER, new StringFormat("Not showing trigger name for <%s>", trigger));
          triggerName = "";
        } else {
          triggerName = identifiers.quoteName(trigger);
        }

        formattingHelper.writeNameRow(triggerName, triggerType);
        formattingHelper.writeDescriptionRow(timing);

        if (options.isHideTriggerActionStatements() || isBlank(actionStatement)) {
          LOGGER.log(
              Level.FINER,
              new StringFormat("Not showing trigger action statement for <%s>", trigger));
        } else {
          formattingHelper.writeNameRow("", "[action statement]");
          formattingHelper.writeWideRow(actionStatement, "definition");
        }
        if (!isBlank(actionCondition)) {
          formattingHelper.writeNameRow("", "[action condition]");
          formattingHelper.writeWideRow(actionCondition, "definition");
        }
      }
    }
  }

  private void printViewTableUsage(final Table table) {
    if (table == null || !isView(table)) {
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
    if (table == null || options.is(hideWeakAssociations)) {
      LOGGER.log(Level.FINER, new StringFormat("Not showing weak association for <%s>", table));
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
        if (!options.is(hideWeakAssociationNames)) {
          LOGGER.log(
              Level.FINER,
              new StringFormat("Not showing weak associations name for <%s>", weakAssociation));
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

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.text.formatter.schema;

import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectsType.hideRoutines;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectsType.hideSequences;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectsType.hideSynonyms;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectsType.hideTables;

import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Identifiers;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.formatter.base.BaseTabularFormatter;
import schemacrawler.tools.text.formatter.base.helper.TextFormattingHelper.DocumentHeaderType;
import schemacrawler.tools.traversal.SchemaTraversalHandler;
import us.fatehi.utility.string.StringFormat;

/** Text formatting of schema. */
public final class SchemaListFormatter extends BaseTabularFormatter<SchemaTextOptions>
    implements SchemaTraversalHandler {

  private static final Logger LOGGER = Logger.getLogger(SchemaListFormatter.class.getName());

  /**
   * Creates a list formatter with execution-local table visibility.
   *
   * @param schemaTextDetailType Schema text detail level
   * @param options Schema text options
   * @param outputOptions Output options
   * @param identifiers Identifier quoting options
   * @param tableVisibilityPredicate Returns true for tables selected in this execution
   */
  public SchemaListFormatter(
      final SchemaTextDetailType schemaTextDetailType,
      final SchemaTextOptions options,
      final OutputOptions outputOptions,
      final Identifiers identifiers,
      final Predicate<Table> tableVisibilityPredicate) {
    super(schemaTextDetailType, options, outputOptions, identifiers, tableVisibilityPredicate);
  }

  /** {@inheritDoc} */
  @Override
  public void handle(final ColumnDataType columnDataType) {
    // No output required
  }

  @Override
  public void handleInfo(final DatabaseInfo dbInfo) {
    // No output required
  }

  @Override
  public void handleInfo(final JdbcDriverInfo driverInfo) {
    // No output required
  }

  /** {@inheritDoc} */
  @Override
  public void handle(final Routine routine) {
    if (routine == null || options.is(hideRoutines)) {
      LOGGER.log(Level.FINER, new StringFormat("Not showing routine <%s>", routine));
      return;
    }

    final String routineTypeDetail =
        "%s, %s".formatted(routine.getRoutineType(), routine.getReturnType());
    final String routineName = quoteName(routine);
    final String routineType = "[" + routineTypeDetail + "]";

    formattingHelper.writeNameRow(routineName, routineType);
    printRemarks(routine);
  }

  /** {@inheritDoc} */
  @Override
  public void handle(final Sequence sequence) {
    if (sequence == null || options.is(hideSequences)) {
      LOGGER.log(Level.FINER, new StringFormat("Not showing sequence <%s>", sequence));
      return;
    }

    final String sequenceName = quoteName(sequence);
    final String sequenceType = "[sequence]";

    formattingHelper.writeNameRow(sequenceName, sequenceType);
    printRemarks(sequence);
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

    formattingHelper.writeNameRow(synonymName, synonymType);
    printRemarks(synonym);
  }

  @Override
  public void handle(final Table table) {
    if (options.is(hideTables)) {
      LOGGER.log(Level.FINER, new StringFormat("Not showing table <%s>", table));
      return;
    }

    final String tableName = quoteName(table);
    final String tableType = "[" + table.getTableType() + "]";

    formattingHelper.writeNameRow(tableName, tableType);
    printRemarks(table);
  }

  /** {@inheritDoc} */
  @Override
  public void handleColumnDataTypesEnd() {
    // No output required
  }

  /** {@inheritDoc} */
  @Override
  public void handleColumnDataTypesStart() {
    // No output required
  }

  @Override
  public void handleInfoEnd() {
    // No output required
  }

  @Override
  public void handleInfoStart() {
    // No output required
  }

  /** {@inheritDoc} */
  @Override
  public void handleRoutinesEnd() {
    if (options.is(hideRoutines)) {
      LOGGER.log(Level.FINER, "Not showing tables");
      return;
    }

    formattingHelper.writeObjectEnd();
  }

  /** {@inheritDoc} */
  @Override
  public void handleRoutinesStart() {
    if (options.is(hideRoutines)) {
      LOGGER.log(Level.FINER, "Not showing routines");
      return;
    }

    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Routines");

    formattingHelper.writeObjectStart();
  }

  /** {@inheritDoc} */
  @Override
  public void handleSequencesEnd() {
    if (options.is(hideSequences)) {
      LOGGER.log(Level.FINER, "Not showing sequences");
      return;
    }

    formattingHelper.writeObjectEnd();
  }

  /** {@inheritDoc} */
  @Override
  public void handleSequencesStart() {
    if (options.is(hideSequences)) {
      LOGGER.log(Level.FINER, "Not showing sequences");
      return;
    }

    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Sequences");

    formattingHelper.writeObjectStart();
  }

  /** {@inheritDoc} */
  @Override
  public void handleSynonymsEnd() {
    if (options.is(hideSynonyms)) {
      LOGGER.log(Level.FINER, "Not showing synonyms");
      return;
    }

    formattingHelper.writeObjectEnd();
  }

  /** {@inheritDoc} */
  @Override
  public void handleSynonymsStart() {
    if (options.is(hideSynonyms)) {
      LOGGER.log(Level.FINER, "Not showing synonyms");
      return;
    }

    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Synonyms");

    formattingHelper.writeObjectStart();
  }

  /** {@inheritDoc} */
  @Override
  public void handleTablesEnd() {
    if (options.is(hideTables)) {
      LOGGER.log(Level.FINER, "Not showing tables");
      return;
    }

    formattingHelper.writeObjectEnd();
  }

  /** {@inheritDoc} */
  @Override
  public void handleTablesStart() {
    if (options.is(hideTables)) {
      LOGGER.log(Level.FINER, "Not showing tables");
      return;
    }

    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Tables");

    formattingHelper.writeObjectStart();
  }

  private void printRemarks(final DatabaseObject object) {
    if (object == null || !object.hasRemarks() || options.isHideRemarks()) {
      return;
    }
    formattingHelper.writeDescriptionRow(object.getRemarks());
  }
}

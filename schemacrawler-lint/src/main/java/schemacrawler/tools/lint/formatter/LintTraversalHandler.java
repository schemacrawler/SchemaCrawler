/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.lint.formatter;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.traversal.SchemaTraversalHandler;

public interface LintTraversalHandler extends SchemaTraversalHandler {

  @Override
  default void handle(final ColumnDataType columnDataType) {
    // No-op
  }

  @Override
  default void handle(final Routine routine) {
    // No-op
  }

  @Override
  default void handle(final Sequence sequence) {
    // No-op
  }

  @Override
  default void handle(final Synonym synonym) {
    // No-op
  }

  @Override
  default void handleColumnDataTypesEnd() {
    // No-op
  }

  @Override
  default void handleColumnDataTypesStart() {
    // No-op
  }

  @Override
  default void handleInfo(DatabaseInfo databaseInfo) {
    // No-op
  }

  @Override
  default void handleInfo(JdbcDriverInfo jdbcDriverInfo) {
    // No-op
  }

  @Override
  default void handleInfoEnd() {
    // No-op
  }

  @Override
  default void handleInfoStart() {
    // No-op
  }

  @Override
  default void handleRoutinesEnd() {
    // No-op
  }

  @Override
  default void handleRoutinesStart() {
    // No-op
  }

  @Override
  default void handleSequencesEnd() {
    // No-op
  }

  @Override
  default void handleSequencesStart() {
    // No-op
  }

  @Override
  default void handleSynonymsEnd() {
    // No-op
  }

  @Override
  default void handleSynonymsStart() {
    // No-op
  }

  void setReport(final Lints report);
}

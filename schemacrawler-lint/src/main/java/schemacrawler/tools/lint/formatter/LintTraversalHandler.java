/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.lint.formatter;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.tools.lint.report.LintReport;
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

  void setReport(final LintReport report);
}

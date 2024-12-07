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

import static schemacrawler.tools.command.text.schema.options.SchemaTextDetailType.schema;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.formatter.base.BaseTabularFormatter;
import schemacrawler.tools.text.formatter.base.helper.TextFormattingHelper.DocumentHeaderType;
import us.fatehi.utility.Color;
import us.fatehi.utility.Multimap;

public final class LintReportTextFormatter extends BaseTabularFormatter<LintOptions>
    implements LintTraversalHandler {

  // Set per run
  private Lints report;

  public LintReportTextFormatter(
      final LintOptions lintOptions,
      final OutputOptions outputOptions,
      final Identifiers identifiers) {
    super(schema, lintOptions, outputOptions, identifiers);
  }

  @Override
  public void handle(final Table table) {
    final Collection<Lint<?>> lints = report.getLints(table);
    if (lints != null && !lints.isEmpty()) {
      formattingHelper.writeObjectStart();

      formattingHelper.println();
      formattingHelper.println();

      final String tableType = "[" + table.getTableType() + "]";
      formattingHelper.writeObjectNameRow(
          nodeId(table), identifiers.quoteFullName(table), tableType, colorMap.getColor(table));
      printLints(lints);
      formattingHelper.writeObjectEnd();
    }
  }

  @Override
  public void handleHeaderEnd() {
    requireNonNull(report, "No lint report provided");
    handleCatalog();
  }

  @Override
  public void handleTablesEnd() {
    // No output required
  }

  @Override
  public void handleTablesStart() {
    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Table Lints");
  }

  @Override
  public void setReport(final Lints report) {
    requireNonNull(report, "No lint report provided");
    this.report = report;
  }

  private void handleCatalog() {
    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Database Lints");

    final Collection<Lint<?>> lints = report.getCatalogLints();
    if (lints != null && !lints.isEmpty()) {
      formattingHelper.writeObjectStart();

      formattingHelper.writeObjectNameRow("", "Database", "[database]", Color.white);

      printLints(lints);
      formattingHelper.writeObjectEnd();
    }
  }

  private void printLints(final Collection<Lint<?>> lints) {
    formattingHelper.writeEmptyRow();

    final Multimap<LintSeverity, Lint<?>> multiMap = new Multimap<>();
    for (final Lint<?> lint : lints) {
      multiMap.add(lint.getSeverity(), lint);
    }
    final List<LintSeverity> severities = Arrays.asList(LintSeverity.values());
    Collections.reverse(severities);
    for (final LintSeverity severity : severities) {
      if (!multiMap.containsKey(severity)) {
        continue;
      }

      formattingHelper.writeNameRow("", String.format("[lint, %s]", severity));
      final List<Lint<?>> lintsById = new ArrayList<>(multiMap.get(severity));
      for (final Lint<?> lint : lintsById) {
        final Object lintValue = lint.getValue();
        if (lintValue instanceof Boolean) {
          if ((Boolean) lintValue) {
            formattingHelper.writeRow("", lint.getMessage(), "");
          }
        } else {
          formattingHelper.writeRow("", lint.getMessage(), lint.getValueAsString());
        }
      }
    }
  }
}

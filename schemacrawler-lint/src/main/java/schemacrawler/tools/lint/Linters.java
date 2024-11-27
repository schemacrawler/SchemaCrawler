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

package schemacrawler.tools.lint;

import static schemacrawler.tools.lint.LintUtility.LINTER_COMPARATOR;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.config.LinterConfig;
import schemacrawler.tools.lint.config.LinterConfigs;
import schemacrawler.tools.lint.report.LintReport;
import schemacrawler.tools.lint.report.LintReportBuilder;
import us.fatehi.utility.string.StringFormat;

public final class Linters {

  private static final Logger LOGGER = Logger.getLogger(Linters.class.getName());

  private final LinterConfigs linterConfigs;
  private final boolean runAllLinters;
  // Running state
  private List<Linter> linters;
  private LintCollector collector;
  private LintReport lintReport;

  public Linters(final LinterConfigs linterConfigs, final boolean runAllLinters) {
    this.linterConfigs = requireNonNull(linterConfigs, "No linter configs provided");
    this.runAllLinters = runAllLinters;

    // Initialize running state to empty
    linters = new ArrayList<>();
    collector = new LintCollector();
    lintReport = LintReportBuilder.builder().build();
  }

  public void dispatch(final LintDispatch lintDispatch) {

    LOGGER.log(Level.INFO, () -> getLintSummary());

    if (lintDispatch == null || lintDispatch == LintDispatch.none) {
      return;
    }

    if (!linters.isEmpty() && exceedsThreshold()) {
      LOGGER.log(Level.INFO, "Dispatching lint results");
      System.err.println(getLintSummary());
      lintDispatch.dispatch();
    }
  }

  public boolean exceedsThreshold() {
    for (final Linter linter : linters) {
      if (linter.exceedsThreshold()) {
        return true;
      }
    }
    return false;
  }

  public LintReport getLintReport() {
    return lintReport;
  }

  public String getLintSummary() {

    final List<Linter> linters = new ArrayList<>(this.linters);
    linters.sort(LINTER_COMPARATOR);

    final StringBuilder buffer = new StringBuilder(1024);
    for (final Linter linter : linters) {
      if (linter.getLintCount() > 0) {
        buffer.append(
            String.format(
                "%10s%s %5d - %s%n",
                "[" + linter.getSeverity() + "]",
                linter.exceedsThreshold() ? "*" : " ",
                linter.getLintCount(),
                linter.getSummary()));
      }
    }

    if (buffer.length() > 0) {
      buffer.insert(0, "Summary of schema lints:\n");
    }

    return buffer.toString();
  }

  public void lint(final Catalog catalog, final Connection connection) {

    requireNonNull(catalog, "No catalog provided");
    requireNonNull(connection, "No connection provided");

    initialize();
    runLinters(catalog, connection);
    // Produce lint report
    lintReport =
        LintReportBuilder.builder().withCatalog(catalog).withLints(collector.getLints()).build();
  }

  /**
   * Number of linters configured to run
   *
   * @return Number of linters configured to run
   */
  public int size() {
    return linters.size();
  }

  private void initialize() {

    linters = new ArrayList<>();
    collector = new LintCollector();

    final LinterRegistry registry = LinterRegistry.getLinterRegistry();
    final Set<String> registeredLinters = registry.getRegisteredLinters();

    // Add all configured linters, with as many instances as were
    // configured
    for (final LinterConfig linterConfig : linterConfigs) {
      if (linterConfig == null) {
        continue;
      }

      // First remove the linter id, because it is "seen",
      // whether it needs to be run or not
      final String linterId = linterConfig.getLinterId();
      registeredLinters.remove(linterId);

      if (!linterConfig.isRunLinter()) {
        LOGGER.log(
            Level.FINE, new StringFormat("Not running configured linter <%s>", linterConfig));
        continue;
      }

      final Linter linter = registry.newLinter(linterId, collector);
      if (linter != null) {
        linter.configure(linterConfig);
        linters.add(linter);
      }
    }

    if (runAllLinters) {
      // Add in all remaining linters that were not configured
      for (final String linterId : registeredLinters) {
        final Linter linter = registry.newLinter(linterId, collector);
        linters.add(linter);
      }
    }
  }

  private void runLinters(final Catalog catalog, final Connection connection) {

    linters.parallelStream()
        .forEach(
            linter -> {
              LOGGER.log(
                  Level.CONFIG,
                  new StringFormat("Linting with <%s>", linter.getLinterInstanceId()));
              try {
                linter.lint(catalog, connection);
              } catch (final Exception e) {
                LOGGER.log(
                    Level.WARNING,
                    e,
                    new StringFormat("Could not run linter <%s>", linter.getLinterInstanceId()));
              }
            });
  }
}

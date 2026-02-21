/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.lint;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.lint.LintUtility.LINTER_COMPARATOR;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.config.LinterConfig;
import schemacrawler.tools.lint.config.LinterConfigs;
import us.fatehi.utility.string.StringFormat;

public final class Linters {

  private static final Logger LOGGER = Logger.getLogger(Linters.class.getName());

  private final LinterConfigs linterConfigs;
  private final boolean runAllLinters;
  // Running state
  private List<Linter> linters;
  private LintCollector collector;

  public Linters(final LinterConfigs linterConfigs, final boolean runAllLinters) {
    this.linterConfigs = requireNonNull(linterConfigs, "No linter configs provided");
    this.runAllLinters = runAllLinters;

    // Initialize running state to empty
    linters = new ArrayList<>();
    collector = new LintCollector();
  }

  public void dispatch(final LintDispatch lintDispatch) {

    LOGGER.log(Level.INFO, (Supplier<String>) this::getLintSummary);

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

  public Lints getLints() {
    return new Lints(collector.getLints());
  }

  public String getLintSummary() {

    final List<Linter> linters = new ArrayList<>(this.linters);
    linters.sort(LINTER_COMPARATOR);

    final StringBuilder buffer = new StringBuilder(1024);
    for (final Linter linter : linters) {
      if (linter.getLintCount() > 0) {
        buffer.append(
            "%10s%s %5d - %s%n"
                .formatted(
                    "[" + linter.getSeverity() + "]",
                    linter.exceedsThreshold() ? "*" : " ",
                    linter.getLintCount(),
                    linter.getSummary()));
      }
    }

    if (!buffer.isEmpty()) {
      buffer.insert(0, "Summary of schema lints:\n");
    }

    return buffer.toString();
  }

  public void initialize(final LinterInitializer linterInitializer) {

    requireNonNull(linterInitializer, "No linter initializer provided");

    linters = new ArrayList<>();
    collector = new LintCollector();

    final Set<String> registeredLinters = linterInitializer.getRegisteredLinters();

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

      final Linter linter = linterInitializer.newLinter(linterId, collector);
      if (linter != null) {
        linter.configure(linterConfig);
        linters.add(linter);
      }
    }

    if (runAllLinters) {
      // Add in all remaining linters that were not configured
      for (final String linterId : registeredLinters) {
        final Linter linter = linterInitializer.newLinter(linterId, collector);
        linters.add(linter);
      }
    }
  }

  public void lint(final Catalog catalog, final Connection connection) {

    // Check if initialized
    requireNonNull(linters, "No linters provided");
    requireNonNull(collector, "No lint collectpr provided");
    if (linters.isEmpty()) {
      return;
    }

    requireNonNull(catalog, "No catalog provided");
    requireNonNull(connection, "No connection provided");

    runLinters(catalog, connection);
  }

  /**
   * Number of linters configured to run
   *
   * @return Number of linters configured to run
   */
  public int size() {
    return linters.size();
  }

  private void runLinters(final Catalog catalog, final Connection connection) {

    linters.parallelStream()
        .forEach(
            linter -> {
              LOGGER.log(
                  Level.CONFIG,
                  new StringFormat("Linting with <%s>", linter.getLinterInstanceId()));
              try {
                linter.initialize();
                linter.setCatalog(catalog);
                if (linter.usesConnection()) {
                  linter.setConnection(connection);
                }
                linter.execute();
              } catch (final Exception e) {
                LOGGER.log(
                    Level.WARNING,
                    e,
                    new StringFormat("Could not run linter <%s>", linter.getLinterInstanceId()));
              }
            });
  }
}

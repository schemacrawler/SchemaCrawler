/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.config.LinterConfig;
import schemacrawler.tools.lint.config.LinterConfigs;
import us.fatehi.utility.string.StringFormat;

public final class Linters implements Iterable<Linter> {

  private static final Logger LOGGER = Logger.getLogger(Linters.class.getName());

  private final List<Linter> linters;
  private final LintCollector collector;
  private final LinterRegistry registry;

  public Linters(final LinterConfigs linterConfigs, final boolean runAllLinters) {
    requireNonNull(linterConfigs, "No linter configs provided");

    linters = new ArrayList<>();
    collector = new LintCollector();
    registry = new LinterRegistry();

    final Set<String> registeredLinters = registry.allRegisteredLinters();

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

      final Linter linter = newLinter(linterId);
      if (linter != null) {
        // Configure linter
        linter.configure(linterConfig);

        linters.add(linter);
      }
    }

    if (runAllLinters) {
      // Add in all remaining linters that were not configured
      for (final String linterId : registeredLinters) {
        final Linter linter = newLinter(linterId);
        linters.add(linter);
      }
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

  public LintCollector getCollector() {
    return collector;
  }

  public String getLintSummary() {
    final class LinterComparator implements Comparator<Linter> {
      @Override
      public int compare(final Linter linter1, final Linter linter2) {
        if (linter1 == null) {
          return -1;
        }

        if (linter2 == null) {
          return 1;
        }

        int comparison = 0;

        if (comparison == 0) {
          comparison = linter1.getSeverity().compareTo(linter2.getSeverity());
        }

        if (comparison == 0) {
          comparison = linter1.getLintCount() - linter2.getLintCount();
        }

        if (comparison == 0) {
          comparison = linter1.getLinterId().compareTo(linter2.getLinterId());
        }

        return comparison;
      }
    }

    final List<Linter> linters = new ArrayList<>(this.linters);
    linters.sort(new LinterComparator());

    final StringBuilder buffer = new StringBuilder(1024);
    for (final Linter linter : linters) {
      if (linter.getLintCount() > 0) {
        buffer.append(
            String.format(
                "%8s%s %5d- %s%n",
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

  @Override
  public Iterator<Linter> iterator() {
    return linters.iterator();
  }

  public void lint(final Catalog catalog, final Connection connection) {
    for (final Linter linter : linters) {
      LOGGER.log(Level.CONFIG, new StringFormat("Linting with <%s>", linter.getLinterInstanceId()));
      try {
        linter.lint(catalog, connection);
      } catch (final Exception e) {
        LOGGER.log(
            Level.WARNING,
            e,
            new StringFormat("Could not run linter <%s>", linter.getLinterInstanceId()));
      }
    }
  }

  /**
   * Number of linters configured to run
   *
   * @return Number of linters configured to run
   */
  public int size() {
    return linters.size();
  }

  @Override
  public String toString() {
    return linters.toString();
  }

  private Linter newLinter(final String linterId) {
    final Linter linter = registry.newLinter(linterId);
    if (linter != null) {
      linter.setLintCollector(collector);
    } else {
      LOGGER.log(Level.FINE, new StringFormat("Cannot find linter <%s>", linterId));
    }
    return linter;
  }
}

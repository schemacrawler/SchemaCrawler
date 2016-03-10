/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.lint.executable;


import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.LintDispatch;
import schemacrawler.tools.lint.LinterConfig;
import schemacrawler.tools.lint.LinterConfigs;

public class LintDispatcher
{

  private static final Logger LOGGER = Logger
    .getLogger(LintDispatcher.class.getName());

  private final LinterConfigs linterConfigs;
  private final SortedMap<LinterConfig, Integer> lintCounts;

  public LintDispatcher(final LinterConfigs linterConfigs)
  {
    requireNonNull(linterConfigs, "No lint configs provided");

    this.linterConfigs = linterConfigs;
    lintCounts = new TreeMap<>();
  }

  public void dispatch(final LintCollector lintCollector)
  {
    requireNonNull(lintCollector, "No lint collector provided");

    for (final Lint<?> lint: lintCollector)
    {
      final String linterId = lint.getLinterId();
      if (linterConfigs.containsKey(linterId))
      {
        final LinterConfig linterConfig = linterConfigs.get(linterId);
        lintCounts.computeIfPresent(linterConfig, (k, v) -> v + 1);
        lintCounts.computeIfAbsent(linterConfig, k -> 1);
      }
    }

    if (LOGGER.isLoggable(Level.INFO))
    {
      final StringBuilder buffer = new StringBuilder(1024);
      buffer.append("Too many schema lints were found:");
      getDispatchableLinters().forEach(lintCountEntry -> {
        final LinterConfig linterConfig = lintCountEntry.getKey();
        final int count = lintCountEntry.getValue();
        buffer.append(String.format("%n[%s] %s - %d",
                                    linterConfig.getSeverity(),
                                    linterConfig.getLinterId(),
                                    count));
      });
      LOGGER.log(Level.INFO, buffer.toString());
    }

    // Dispatch, in a loop, since not all dispatchers may interrupt the
    // loop
    getDispatchableLinters().forEach(lintCountEntry -> {
      final LinterConfig linterConfig = lintCountEntry.getKey();
      linterConfig.dispatch();
    });
  }

  private Stream<Map.Entry<LinterConfig, Integer>> getDispatchableLinters()
  {
    return lintCounts.entrySet().stream()
      .filter(lintCountEntry -> lintCountEntry.getKey()
        .getDispatch() != LintDispatch.none)
      .filter(lintCountEntry -> lintCountEntry.getValue() > lintCountEntry
        .getKey().getDispatchThreshold());
  }

}

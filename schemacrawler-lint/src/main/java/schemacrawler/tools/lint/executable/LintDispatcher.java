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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LinterConfig;
import schemacrawler.tools.lint.LinterConfigs;
import schemacrawler.tools.lint.collector.LintCollector;
import sf.util.StringFormat;

public class LintDispatcher
{

  private static final Logger LOGGER = Logger
    .getLogger(LintDispatcher.class.getName());

  private final LinterConfigs linterConfigs;
  private final Map<LinterConfig, Integer> lintCounts;

  public LintDispatcher(final LinterConfigs linterConfigs)
  {
    requireNonNull(linterConfigs, "No lint configs provided");

    this.linterConfigs = linterConfigs;
    lintCounts = new HashMap<>();
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

    lintCounts.entrySet()
      .stream().filter(lintCountEntry -> lintCountEntry
        .getValue() > lintCountEntry.getKey().getDispatchThreshold())
      .forEach(lintCountEntry -> {
        final LinterConfig linterConfig = lintCountEntry.getKey();
        LOGGER.log(Level.FINE,
                   new StringFormat("Processing dispatches for lint, %s",
                                    linterConfig.getLinterId()));
        linterConfig.dispatch();
      });
  }

}

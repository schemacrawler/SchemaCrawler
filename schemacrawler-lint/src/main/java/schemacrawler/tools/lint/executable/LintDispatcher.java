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
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintDispatch;
import schemacrawler.tools.lint.LinterConfig;
import schemacrawler.tools.lint.LinterConfigs;
import schemacrawler.tools.lint.collector.LintCollector;

public class LintDispatcher
{

  private static final Logger LOGGER = Logger
    .getLogger(LintDispatcher.class.getName());

  private final Map<String, LinterDispatchRule> linterDispatches;
  private final SortedMap<LinterDispatchRule, Integer> lintCounts;

  public LintDispatcher(final LinterConfigs linterConfigs)
  {
    requireNonNull(linterConfigs, "No lint configs provided");

    linterDispatches = new HashMap<>();
    lintCounts = new TreeMap<>();

    for (final LinterConfig linterConfig: linterConfigs)
    {
      final LinterDispatchRule linterDispatchRule = new LinterDispatchRule(linterConfig);
      linterDispatches.put(linterDispatchRule.getLinterId(),
                           linterDispatchRule);
    }
  }

  public void dispatch(final LintCollector lintCollector)
  {
    requireNonNull(lintCollector, "No lint collector provided");

    for (final Lint<?> lint: lintCollector)
    {
      final String linterId = lint.getLinterId();
      if (linterDispatches.containsKey(linterId))
      {
        final LinterDispatchRule linterDispatchRule = linterDispatches
          .get(linterId);
        lintCounts.computeIfPresent(linterDispatchRule, (k, v) -> v + 1);
        lintCounts.computeIfAbsent(linterDispatchRule, k -> 1);
      }
    }

    lintCounts.forEach((linterDispatchRule, count) -> {
      if (count > linterDispatchRule.getDispatchThreshold())
      {
        final LintDispatch dispatch = linterDispatchRule.getDispatch();
        if (dispatch != null)
        {
          LOGGER
            .log(Level.SEVERE,
                 "Abnormal system termination, since a critical schema lint was found");
          switch (dispatch)
          {
            case none:
              LOGGER.log(Level.FINE, "Not dispatched");
              break;
            case write_err:
              System.err
                .println("Abnormal system termination, since a critical schema lint was found");
              break;
            case throw_exception:
              throw new RuntimeException("Abnormal system termination, since a critical schema lint was found");
              // break;
            case terminate_system:
              System.exit(1);
              break;
            default:
              break;
          }
        }
      }
    });
  }

}

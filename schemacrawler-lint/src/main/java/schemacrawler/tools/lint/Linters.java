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
package schemacrawler.tools.lint;


import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.StringFormat;

public final class Linters
{

  private static final Logger LOGGER = Logger
    .getLogger(Linters.class.getName());

  private final List<Linter> linters = new ArrayList<>();
  private final LintCollector collector = new LintCollector();
  private final LinterRegistry registry = new LinterRegistry();

  public Linters(final LinterConfigs linterConfigs)
    throws SchemaCrawlerException
  {
    requireNonNull(linterConfigs, "No linter configs provided");

    final Set<String> registeredLinters = registry.allRegisteredLinters();

    // Add all configured linters, with as many instances as were
    // configured
    for (final LinterConfig linterConfig: linterConfigs)
    {
      if (linterConfig == null)
      {
        continue;
      }

      // First remove the linter id, because it is "seen",
      // whether it needs to be run or not
      final String linterId = linterConfig.getLinterId();
      registeredLinters.remove(linterId);

      if (!linterConfig.isRunLinter())
      {
        LOGGER.log(Level.FINE,
                   new StringFormat("Not running configured linter, %s",
                                    linterConfig));
        continue;
      }

      final Linter linter = newLinter(linterId);
      if (linter != null)
      {
        // Configure linter
        linter.configure(linterConfig);

        linters.add(linter);
      }
    }

    // Add in all remaining linters that were not configured
    for (final String linterId: registeredLinters)
    {
      final Linter linter = newLinter(linterId);
      linters.add(linter);
    }

    Collections.sort(linters);
  }

  public void dispatch()
  {
    if (LOGGER.isLoggable(Level.INFO))
    {
      final String lintSummary = getLintSummary();
      if (lintSummary.isEmpty())
      {
        LOGGER.log(Level.INFO, lintSummary);
      }
    }

    // Dispatch, in a loop, since not all dispatchers may interrupt the
    // loop
    linters.forEach(linter -> linter.dispatch());
  }

  public LintCollector getCollector()
  {
    return collector;
  }

  public String getLintSummary()
  {
    final StringBuilder buffer = new StringBuilder(1024);

    linters.stream().filter(linter -> linter.getLintCount() > 0)
      .forEach(linter -> buffer.append(String.format("[%6s] %5d%s- %s%n",
                                                     linter.getSeverity(),
                                                     linter.getLintCount(),
                                                     linter
                                                       .shouldDispatch()? "*"
                                                                        : " ",
                                                     linter.getSummary())));
    if (buffer.length() > 0)
    {
      buffer.insert(0, "Summary of schema lints:\n");
    }

    return buffer.toString();
  }

  public void lint(final Catalog catalog, final Connection connection)
    throws SchemaCrawlerException
  {
    for (final Linter linter: linters)
    {
      LOGGER.log(Level.FINE,
                 new StringFormat("Linting with, %s",
                                  linter.getLinterInstanceId()));
      linter.lint(catalog, connection);
    }
  }

  @Override
  public String toString()
  {
    return linters.toString();
  }

  private Linter newLinter(final String linterId)
  {
    final Linter linter = registry.newLinter(linterId);
    if (linter != null)
    {
      linter.setLintCollector(collector);
    }
    else
    {
      LOGGER.log(Level.FINE,
                 new StringFormat("Cannot find linter, %s", linterId));
    }
    return linter;
  }

}

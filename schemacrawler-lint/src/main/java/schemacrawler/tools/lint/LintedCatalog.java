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
import static sf.util.DatabaseUtility.checkConnection;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.BaseCatalogDecorator;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public final class LintedCatalog
  extends BaseCatalogDecorator
{

  private static final Logger LOGGER = Logger
    .getLogger(LintedCatalog.class.getName());

  private static final long serialVersionUID = -3953296149824921463L;

  private final LintCollector collector;

  public LintedCatalog(final Catalog catalog,
                       final Connection connection,
                       final LinterConfigs linterConfigs)
                         throws SchemaCrawlerException
  {
    super(catalog);

    collector = new SimpleLintCollector();

    try
    {
      checkConnection(connection);
    }
    catch (final SchemaCrawlerException e)
    {
      // The offline snapshot executable may not have a live connection,
      // so we cannot fail with an exception. Log and continue.
      LOGGER.log(Level.WARNING, "No connection provided", e);
    }
    
    requireNonNull(linterConfigs, "No linter configs provided");

    final List<Linter> linters = new ArrayList<>();

    final LinterRegistry linterRegistry = new LinterRegistry();
    final Set<String> registeredLinters = linterRegistry.allRegisteredLinters();

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
      final String linterId = linterConfig.getId();
      registeredLinters.remove(linterId);

      if (!linterConfig.isRunLinter())
      {
        LOGGER.log(Level.FINE,
                   String.format("Not running configured linter, %s",
                                 linterConfig));
        continue;
      }

      final Linter linter = newLinter(linterRegistry, linterId);
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
      final Linter linter = newLinter(linterRegistry, linterId);
      linters.add(linter);
    }

    // Perform lint
    for (final Linter linter: linters)
    {
      LOGGER.log(Level.FINE,
                 String.format("Linting with %s", linter.getClass().getName()));
      linter.lint(catalog, connection);
    }
  }

  public LintCollector getCollector()
  {
    return collector;
  }

  private Linter newLinter(final LinterRegistry registry, final String linterId)
  {
    final Linter linter = registry.newLinter(linterId);
    if (linter != null)
    {
      linter.setLintCollector(collector);
    }
    else
    {
      LOGGER.log(Level.FINE, String.format("Cannot find linter, %s", linterId));
    }
    return linter;
  }

}

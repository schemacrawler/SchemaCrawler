package schemacrawler.tools.lint;


import java.sql.Connection;
import java.util.ArrayList;
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

  public Linters(final LinterConfigs linterConfigs)
    throws SchemaCrawlerException
  {

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
      final String linterId = linterConfig.getLinterId();
      registeredLinters.remove(linterId);

      if (!linterConfig.isRunLinter())
      {
        LOGGER.log(Level.FINE,
                   new StringFormat("Not running configured linter, %s",
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
  }

  public void dispatch()
  {
    if (LOGGER.isLoggable(Level.INFO))
    {
      final StringBuilder buffer = new StringBuilder(1024);
      buffer.append("Too many schema lints were found:");
      linters.stream().filter(linter -> linter.shouldDispatch())
        .forEach(linter -> buffer.append(String.format("%n[%s] %s - %d",
                                                       linter.getSeverity(),
                                                       linter.getSummary(),
                                                       linter.getLintCount())));
      LOGGER.log(Level.INFO, buffer.toString());
    }

    // Dispatch, in a loop, since not all dispatchers may interrupt the
    // loop
    linters.forEach(linter -> linter.dispatch());
  }

  public LintCollector getCollector()
  {
    return collector;
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

  private Linter newLinter(final LinterRegistry registry, final String linterId)
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

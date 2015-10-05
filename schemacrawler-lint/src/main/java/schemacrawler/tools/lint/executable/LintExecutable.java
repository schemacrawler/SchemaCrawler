/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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


import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.newBufferedReader;
import static sf.util.Utility.isBlank;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseStagedExecutable;
import schemacrawler.tools.lint.LintedCatalog;
import schemacrawler.tools.lint.LinterConfigs;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.utility.NamedObjectSort;

public class LintExecutable
  extends BaseStagedExecutable
{

  private static final Logger LOGGER = Logger
    .getLogger(LintExecutable.class.getName());

  public static final String COMMAND = "lint";

  private LintOptions lintOptions;

  public LintExecutable()
  {
    super(COMMAND);
  }

  @Override
  public void executeOn(final Catalog db, final Connection connection)
    throws Exception
  {
    // Read lint options from the config
    lintOptions = getLintOptions();

    final LinterConfigs linterConfigs = readLinterConfigs();
    final LintedCatalog catalog = new LintedCatalog(db, connection,
                                                    linterConfigs);

    final LintTraversalHandler formatter = getSchemaTraversalHandler();

    formatter.begin();

    formatter.handleInfoStart();
    formatter.handle(catalog.getSchemaCrawlerInfo());
    formatter.handle(catalog.getDatabaseInfo());
    formatter.handle(catalog.getJdbcDriverInfo());
    formatter.handleInfoEnd();

    formatter.handleStart();
    formatter.handle(catalog);

    final List<? extends Table> tablesList = new ArrayList<>(catalog
      .getTables());
    Collections.sort(tablesList, NamedObjectSort
      .getNamedObjectSort(lintOptions.isAlphabeticalSortForTables()));
    for (final Table table: tablesList)
    {
      formatter.handle(table);
    }

    formatter.handleEnd();

    formatter.end();

  }

  public final LintOptions getLintOptions()
  {
    final LintOptions lintOptions;
    if (this.lintOptions == null)
    {
      lintOptions = new LintOptionsBuilder().fromConfig(additionalConfiguration)
        .toOptions();
    }
    else
    {
      lintOptions = this.lintOptions;
    }
    return lintOptions;
  }

  public final void setLintOptions(final LintOptions lintOptions)
  {
    this.lintOptions = lintOptions;
  }

  private LintTraversalHandler getSchemaTraversalHandler()
    throws SchemaCrawlerException
  {
    final LintTraversalHandler formatter;

    final TextOutputFormat outputFormat = TextOutputFormat
      .fromFormat(outputOptions.getOutputFormatValue());
    if (outputFormat == TextOutputFormat.json)
    {
      formatter = new LintJsonFormatter(lintOptions, outputOptions);
    }
    else
    {
      formatter = new LintTextFormatter(lintOptions, outputOptions);
    }

    return formatter;
  }

  /**
   * Obtain linter configuration from a system property
   *
   * @return LinterConfigs
   * @throws SchemaCrawlerException
   */
  private LinterConfigs readLinterConfigs()
  {
    final LinterConfigs linterConfigs = new LinterConfigs();
    String linterConfigsFile = null;
    try
    {
      linterConfigsFile = lintOptions.getLinterConfigs();
      if (!isBlank(linterConfigsFile))
      {
        final Path linterConfigsFilePath = Paths.get(linterConfigsFile)
          .toAbsolutePath();
        if (isRegularFile(linterConfigsFilePath)
            && isReadable(linterConfigsFilePath))
        {
          linterConfigs.parse(newBufferedReader(linterConfigsFilePath));
        }
        else
        {
          LOGGER
            .log(Level.WARNING,
                 "Could not find linter configs file, " + linterConfigsFile);
        }
      }
      else
      {
        LOGGER.log(Level.CONFIG, "Using default linter configs");
      }

      return linterConfigs;
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not load linter configs from file, "
                                + linterConfigsFile,
                 e);
      return linterConfigs;
    }
  }

}

/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.lint.executable;


import static schemacrawler.tools.lint.LintUtility.readLinterConfigs;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseStagedExecutable;
import schemacrawler.tools.lint.LintDispatch;
import schemacrawler.tools.lint.LintedCatalog;
import schemacrawler.tools.lint.LinterConfigs;
import schemacrawler.tools.lint.Linters;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.utility.NamedObjectSort;

public class LintExecutable
  extends BaseStagedExecutable
{

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

    final LinterConfigs linterConfigs = readLinterConfigs(lintOptions);
    final Linters linters = new Linters(linterConfigs);

    final LintedCatalog catalog = new LintedCatalog(db, connection, linters);

    generateReport(catalog);

    dispatch(linters);
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

  private void dispatch(final Linters linters)
  {
    if (!StreamSupport.stream(linters.spliterator(), false)
      .anyMatch(linter -> linter.exceedsThreshold()))
    {
      return;
    }

    final String lintSummary = linters.getLintSummary();
    if (!lintSummary.isEmpty())
    {
      System.err.println(lintSummary);
    }

    final LintDispatch lintDispatch = additionalConfiguration
      .getEnumValue("lintdispatch", LintDispatch.none);
    lintDispatch.dispatch();
  }

  private void generateReport(final LintedCatalog catalog)
    throws SchemaCrawlerException
  {
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
    Collections
      .sort(tablesList,
            NamedObjectSort
              .getNamedObjectSort(lintOptions.isAlphabeticalSortForTables()));
    for (final Table table: tablesList)
    {
      formatter.handle(table);
    }

    formatter.handleEnd();

    formatter.end();
  }

  private LintTraversalHandler getSchemaTraversalHandler()
    throws SchemaCrawlerException
  {
    final LintTraversalHandler formatter;

    final TextOutputFormat outputFormat = TextOutputFormat
      .valueOfFromString(outputOptions.getOutputFormatValue());
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

}

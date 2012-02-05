/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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


import java.io.FileReader;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.lint.LintedDatabase;
import schemacrawler.tools.lint.LinterConfigs;
import schemacrawler.tools.options.OutputFormat;
import sf.util.Utility;

public class LintExecutable
  extends BaseExecutable
{

  private static final Logger LOGGER = Logger.getLogger(LintExecutable.class
    .getName());
  public static final String COMMAND = "lint";

  private LintOptions lintOptions;

  public LintExecutable()
  {
    super(COMMAND);
  }

  public final LintOptions getLintOptions()
  {
    final LintOptions lintOptions;
    if (this.lintOptions == null)
    {
      lintOptions = new LintOptions(additionalConfiguration);
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

  @Override
  protected void executeOn(final Database db, final Connection connection)
    throws Exception
  {
    final LinterConfigs linterConfigs = readLinterConfigs();
    final LintedDatabase database = new LintedDatabase(db, linterConfigs);

    final LintFormatter formatter = getSchemaTraversalHandler();

    formatter.begin();

    formatter.handleInfoStart();
    formatter.handle(database.getSchemaCrawlerInfo());
    formatter.handle(database.getDatabaseInfo());
    formatter.handle(database.getJdbcDriverInfo());
    formatter.handleInfoEnd();

    formatter.handleStart();
    formatter.handle(database);
    for (final Schema schema: database.getSchemas())
    {
      final Table[] tables = schema.getTables();
      for (final Table table: tables)
      {
        formatter.handle(table);
      }
    }
    formatter.handleEnd();

    formatter.end();

  }

  private LintFormatter getSchemaTraversalHandler()
    throws SchemaCrawlerException
  {
    final LintFormatter formatter;
    final LintOptions lintOptions = getLintOptions();

    final OutputFormat outputFormat = outputOptions.getOutputFormat();
    if (outputFormat == OutputFormat.json)
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
      linterConfigsFile = System
        .getProperty("schemacrawer.linter_configs.file");
      if (!Utility.isBlank(linterConfigsFile))
      {
        linterConfigs.parse(new FileReader(linterConfigsFile));
      }
      return linterConfigs;
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not load linter configs from file "
                                + linterConfigsFile, e);
      return linterConfigs;
    }
  }

}

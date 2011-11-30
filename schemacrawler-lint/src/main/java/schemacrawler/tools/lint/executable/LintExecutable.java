/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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


import java.sql.Connection;

import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.lint.LintedDatabase;
import schemacrawler.tools.options.OutputFormat;

public class LintExecutable
  extends BaseExecutable
{

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
    final LintedDatabase database = new LintedDatabase(db);

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

}

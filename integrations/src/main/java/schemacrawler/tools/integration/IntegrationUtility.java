/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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

package schemacrawler.tools.integration;


import javax.sql.DataSource;

import schemacrawler.main.SchemaCrawlerMain;
import schemacrawler.tools.ExecutionContext;
import schemacrawler.tools.Executor;
import schemacrawler.tools.schematext.SchemaTextOptions;
import sf.util.CommandLineUtility;

/**
 * Main class that takes arguments for a database for crawling a schema.
 */
public final class IntegrationUtility
{

  /**
   * Adapts a SchemaCrawlerExecutor into an Executor.
   * 
   * @author sfatehi
   */
  private final static class ToolsExecutorAdapter
    implements Executor
  {

    private final SchemaCrawlerExecutor schemaCrawlerExecutor;

    ToolsExecutorAdapter(final SchemaCrawlerExecutor schemaCrawlerExecutor)
    {
      this.schemaCrawlerExecutor = schemaCrawlerExecutor;
    }

    /**
     * {@inheritDoc}
     * 
     * @see schemacrawler.tools.Executor#execute(schemacrawler.tools.ExecutionContext,
     *      javax.sql.DataSource)
     */
    public void execute(final ExecutionContext executionContext,
                        final DataSource dataSource)
      throws Exception
    {
      schemaCrawlerExecutor.execute(executionContext.getSchemaCrawlerOptions(),
                                    (SchemaTextOptions) executionContext
                                      .getToolOptions(),
                                    dataSource);
    }

  }

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   * @param readmeResource
   *        Resource location for readme file.
   * @param schemaCrawlerExecutor
   *        SchemaCrawler executor
   * @throws Exception
   *         On an exception
   */
  public static void integrationToolMain(final String[] args,
                                         final String readmeResource,
                                         final SchemaCrawlerExecutor schemaCrawlerExecutor)
    throws Exception
  {
    CommandLineUtility.checkForHelp(args, readmeResource);
    CommandLineUtility.setLogLevel(args);

    SchemaCrawlerMain
      .schemacrawler(args, new ToolsExecutorAdapter(schemaCrawlerExecutor));
  }

  private IntegrationUtility()
  {
  }

}

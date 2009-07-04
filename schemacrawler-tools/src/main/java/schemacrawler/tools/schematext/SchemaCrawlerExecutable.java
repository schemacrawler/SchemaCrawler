/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.tools.schematext;


import javax.sql.DataSource;

import schemacrawler.crawl.DatabaseSchemaCrawler;
import schemacrawler.main.HelpOptions;
import schemacrawler.main.SchemaCrawlerCommandLine;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.CrawlHandler;
import schemacrawler.schemacrawler.SchemaCrawler;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.Commands;
import schemacrawler.tools.Executable;
import schemacrawler.tools.OutputOptions;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
 */
public class SchemaCrawlerExecutable
  extends Executable<SchemaTextOptions>
{
  protected CrawlHandler crawlHandler;

  /**
   * Sets up default options.
   */
  public SchemaCrawlerExecutable()
  {
    toolOptions = new SchemaTextOptions();
  }

  @Override
  public void execute(final DataSource dataSource)
    throws Exception
  {
    schemaCrawlerOptions.setSchemaInfoLevel(toolOptions
      .getSchemaTextDetailType().mapToInfoLevel());

    CrawlHandler handler = crawlHandler;
    if (handler == null)
    {
      handler = SchemaTextFactory.createSchemaTextCrawlHandler(toolOptions);
    }

    final SchemaCrawler crawler = new DatabaseSchemaCrawler(dataSource
      .getConnection());
    crawler.crawl(schemaCrawlerOptions, handler);
  }

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   * @param helpResource
   *        A resource for help text
   * @throws Exception
   *         On an exception
   */
  public final void executeOnSchema(final String[] args,
                                    final HelpOptions helpOptions)
    throws Exception
  {
    final SchemaCrawlerCommandLine commandLine = new SchemaCrawlerCommandLine(args,
                                                                              helpOptions);
    final Config config = commandLine.getConfig();
    final SchemaCrawlerOptions schemaCrawlerOptions = commandLine
      .getSchemaCrawlerOptions();
    final OutputOptions outputOptions = commandLine.getOutputOptions();

    final Commands commands = commandLine.getCommands();
    final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType
      .valueOf(commands.getFirstComand().getName());

    final SchemaTextOptions schemaTextOptions = new SchemaTextOptions(config,
                                                                      outputOptions,
                                                                      schemaTextDetailType);

    setSchemaCrawlerOptions(schemaCrawlerOptions);
    setToolOptions(schemaTextOptions);
    execute(commandLine.createDataSource());
  }

}

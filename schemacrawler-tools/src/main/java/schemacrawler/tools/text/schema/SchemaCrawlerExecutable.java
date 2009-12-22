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

package schemacrawler.tools.text.schema;


import java.sql.Connection;

import schemacrawler.crawl.DatabaseSchemaCrawler;
import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawler;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.CrawlHandler;
import schemacrawler.tools.Crawler;
import schemacrawler.tools.Executable;
import schemacrawler.tools.ExecutionException;
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

  @Override
  public final void execute(final Connection connection)
    throws ExecutionException
  {
    if (connection == null)
    {
      throw new IllegalArgumentException("No connection provided");
    }

    adjustSchemaInfoLevel();

    try
    {
      final CrawlHandler handler;
      if (crawlHandler == null)
      {
        handler = SchemaTextFactory.createSchemaTextCrawlHandler(toolOptions);
      }
      else
      {
        handler = crawlHandler;
      }

      final SchemaCrawler schemaCrawler = new DatabaseSchemaCrawler(connection);
      final Database database = schemaCrawler.crawl(schemaCrawlerOptions);
      final Crawler crawler = new Crawler(database);
      crawler.crawl(handler);
    }
    catch (final SchemaCrawlerException e)
    {
      throw new ExecutionException("Could not execute SchemaCrawler", e);
    }
  }

  @Override
  public void initializeToolOptions(final String command,
                                    final Config config,
                                    final OutputOptions outputOptions)
    throws ExecutionException
  {
    final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType
      .valueOf(command);
    toolOptions = new SchemaTextOptions(config,
                                        outputOptions,
                                        schemaTextDetailType);
  }

}

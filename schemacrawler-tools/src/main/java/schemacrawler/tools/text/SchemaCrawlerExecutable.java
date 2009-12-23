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

package schemacrawler.tools.text;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.Executable;
import schemacrawler.tools.ExecutionException;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.text.base.CrawlHandler;
import schemacrawler.tools.text.base.Crawler;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.operation.OperationHandler;
import schemacrawler.tools.text.operation.OperationOptions;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import sf.util.Utility;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerExecutable
  extends Executable
{

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
      final SchemaCrawler schemaCrawler = new SchemaCrawler(connection);
      final Database database = schemaCrawler.crawl(schemaCrawlerOptions);
      final List<CrawlHandler> crawlHandlers = createCrawlHandlers(database,
                                                                   connection);
      final Crawler crawler = new Crawler(database);
      for (final CrawlHandler crawlHandler: crawlHandlers)
      {
        crawler.crawl(crawlHandler);
      }
    }
    catch (final SchemaCrawlerException e)
    {
      throw new ExecutionException("Could not execute SchemaCrawler", e);
    }
  }

  private List<CrawlHandler> createCrawlHandlers(final Database database,
                                                 final Connection connection)
    throws SchemaCrawlerException
  {
    final Commands commands;
    if (Utility.isBlank(command))
    {
      commands = new Commands(SchemaTextDetailType.standard_schema.name());
    }
    else
    {
      commands = new Commands(command);
    }

    final OutputOptions masterOutputOptions = outputOptions;
    final List<CrawlHandler> crawlHandlers = new ArrayList<CrawlHandler>();
    for (final String command: commands)
    {
      final OutputOptions outputOptions = masterOutputOptions.duplicate();
      if (commands.size() > 1)
      {
        if (commands.isFirstCommand(command))
        {
          // First command - no footer
          outputOptions.setNoFooter(true);
        }
        else if (commands.isLastCommand(command))
        {
          // Last command - no header, or info
          outputOptions.setNoHeader(true);
          outputOptions.setNoInfo(true);

          outputOptions.setAppendOutput(true);
        }
        else
        {
          // Middle command - no header, footer, or info
          outputOptions.setNoHeader(true);
          outputOptions.setNoInfo(true);
          outputOptions.setNoFooter(true);

          outputOptions.setAppendOutput(true);
        }
      }

      final CrawlHandler crawlHandler;
      SchemaTextDetailType schemaTextDetailType;
      try
      {
        schemaTextDetailType = SchemaTextDetailType.valueOf(command);
      }
      catch (final Exception e)
      {
        schemaTextDetailType = null;
      }
      if (schemaTextDetailType != null)
      {
        final SchemaTextOptions toolOptions = new SchemaTextOptions(config,
                                                                    outputOptions,
                                                                    schemaTextDetailType);
        crawlHandler = SchemaTextFactory
          .createSchemaTextCrawlHandler(toolOptions);
      }
      else
      {
        Operation operation;
        OperationOptions operationOptions;
        try
        {
          operation = Operation.valueOf(command);
          operationOptions = new OperationOptions(config,
                                                  outputOptions,
                                                  operation);
        }
        catch (final IllegalArgumentException e)
        {
          final String queryName = command;
          operationOptions = new OperationOptions(config,
                                                  outputOptions,
                                                  queryName);
        }
        crawlHandler = new OperationHandler(operationOptions, connection);
      }
      crawlHandlers.add(crawlHandler);
    }

    return crawlHandlers;
  }

}

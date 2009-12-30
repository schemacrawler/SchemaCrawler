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

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.CrawlHandler;
import schemacrawler.tools.text.base.Crawler;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.operation.OperationHandler;
import schemacrawler.tools.text.operation.OperationOptions;
import schemacrawler.tools.text.operation.Query;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextFormatter;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import sf.util.Utility;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerTextExecutable
  extends BaseExecutable
{

  private static final long serialVersionUID = -6824567755397315920L;

  private Config config;
  private OperationOptions operationOptions;
  private SchemaTextOptions schemaTextOptions;

  public SchemaCrawlerTextExecutable(final String command)
  {
    super(command);
  }

  public final Config getConfig()
  {
    return config;
  }

  public final OperationOptions getOperationOptions()
  {
    return operationOptions;
  }

  public final SchemaTextOptions getSchemaTextOptions()
  {
    return schemaTextOptions;
  }

  public final void setConfig(final Config config)
  {
    this.config = config;
  }

  public final void setOperationOptions(final OperationOptions operationOptions)
  {
    this.operationOptions = operationOptions;
  }

  public final void setSchemaTextOptions(final SchemaTextOptions schemaTextOptions)
  {
    this.schemaTextOptions = schemaTextOptions;
  }

  @Override
  protected final void executeOn(final Database database,
                                 final Connection connection)
    throws Exception
  {
    final List<CrawlHandler> crawlHandlers = createCrawlHandlers(connection);
    final Crawler crawler = new Crawler(database);
    for (final CrawlHandler crawlHandler: crawlHandlers)
    {
      crawler.crawl(crawlHandler);
    }
  }

  private List<CrawlHandler> createCrawlHandlers(final Connection connection)
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
        final SchemaTextOptions schemaTextOptions;
        if (this.schemaTextOptions == null)
        {
          schemaTextOptions = new SchemaTextOptions(config,
                                                    schemaTextDetailType);
        }
        else
        {
          schemaTextOptions = this.schemaTextOptions.duplicate();
          schemaTextOptions.setSchemaTextDetailType(schemaTextDetailType);
        }

        crawlHandler = new SchemaTextFormatter(schemaTextOptions, outputOptions);
      }
      else
      {
        String queryName = null;
        Operation operation;
        OperationOptions operationOptions;
        try
        {
          operation = Operation.valueOf(command);
        }
        catch (final IllegalArgumentException e)
        {
          operation = null;
          queryName = command;
        }
        if (this.operationOptions == null)
        {
          operationOptions = new OperationOptions();
        }
        else
        {
          operationOptions = this.operationOptions.duplicate();
        }
        if (operation == null)
        {
          final Query query = operationOptions.getQuery();
          if (query == null || !query.getName().equals(queryName))
          {
            final String queryString;
            if (config != null)
            {
              queryString = config.get(queryName);
            }
            else
            {
              queryString = null;
            }
            operationOptions.setQuery(new Query(queryName, queryString));
          }
        }
        else
        {
          operationOptions.setOperation(operation);
        }

        crawlHandler = new OperationHandler(operationOptions,
                                            outputOptions,
                                            connection);
      }
      crawlHandlers.add(crawlHandler);
    }

    return crawlHandlers;
  }

}

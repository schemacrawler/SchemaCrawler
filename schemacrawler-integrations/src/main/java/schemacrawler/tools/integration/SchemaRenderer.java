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
package schemacrawler.tools.integration;


import java.io.Writer;
import java.sql.Connection;

import schemacrawler.crawl.CachingCrawlHandler;
import schemacrawler.crawl.DatabaseSchemaCrawler;
import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawler;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.ExecutionException;
import schemacrawler.tools.main.HelpOptions;

/**
 * An executor that uses a template renderer to render a schema.
 * 
 * @author sfatehi
 */
public abstract class SchemaRenderer
  extends IntegrationsExecutable
{

  protected SchemaRenderer(final String name)
  {
    super(name);
  }

  @Override
  public final void execute(final Connection connection)
    throws ExecutionException
  {
    if (connection == null)
    {
      throw new IllegalArgumentException("No connection provided");
    }

    initialize();

    try
    {
      final CachingCrawlHandler handler = new CachingCrawlHandler();
      final SchemaCrawler crawler = new DatabaseSchemaCrawler(connection);
      crawler.crawl(schemaCrawlerOptions, handler);
      final Database database = handler.getDatabase();

      // Executable-specific work
      final Writer writer = toolOptions.getOutputOptions().openOutputWriter();
      final String templateName = toolOptions.getOutputOptions()
        .getOutputFormatValue();
      render(connection, templateName, database, writer);
      toolOptions.getOutputOptions().closeOutputWriter(writer);
    }
    catch (final SchemaCrawlerException e)
    {
      throw new ExecutionException("Could not execute renderer", e);
    }
  }

  @Override
  protected HelpOptions getHelpOptions()
  {
    return new IntegrationHelpOptions("SchemaCrawler - Templating",
                                      "/help/OutputOptions.templating.txt");
  }

  /**
   * Renders the schema with the given template.
   * 
   * @param resource
   *        Location of the resource
   * @param database
   *        Database
   * @param writer
   *        Writer
   * @throws Exception
   */
  protected abstract void render(Connection connection,
                                 final String resource,
                                 final Database database,
                                 final Writer writer)
    throws ExecutionException;

}

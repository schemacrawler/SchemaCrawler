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

import javax.sql.DataSource;

import schemacrawler.crawl.CachingCrawlHandler;
import schemacrawler.crawl.DatabaseSchemaCrawler;
import schemacrawler.main.HelpOptions;
import schemacrawler.main.HelpOptions.CommandHelpType;
import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawler;

/**
 * An executor that uses a template renderer to render a schema.
 * 
 * @author sfatehi
 */
public abstract class SchemaRenderer
  extends IntegrationsExecutable
{

  @Override
  public final void execute(final DataSource dataSource)
    throws Exception
  {
    if (dataSource == null)
    {
      throw new IllegalArgumentException("No data-source provided");
    }

    schemaCrawlerOptions.setSchemaInfoLevel(toolOptions.getSchemaInfoLevel());

    final CachingCrawlHandler handler = new CachingCrawlHandler();
    final SchemaCrawler crawler = new DatabaseSchemaCrawler(dataSource
      .getConnection());
    crawler.crawl(schemaCrawlerOptions, handler);
    final Database database = handler.getDatabase();

    // Executable-specific work
    final Writer writer = toolOptions.getOutputOptions().openOutputWriter();
    final String templateName = toolOptions.getOutputOptions()
      .getOutputFormatValue();
    render(templateName, database, writer);
    toolOptions.getOutputOptions().closeOutputWriter(writer);
  }

  @Override
  protected HelpOptions getHelpOptions()
  {
    final HelpOptions helpOptions = new HelpOptions("SchemaCrawler - Templating");
    helpOptions.setCommandHelpType(CommandHelpType.without_operations);
    helpOptions.setResourceOutputOptions("/help/OutputOptions.templating.txt");

    return helpOptions;
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
  protected abstract void render(final String resource,
                                 final Database database,
                                 final Writer writer)
    throws Exception;

}

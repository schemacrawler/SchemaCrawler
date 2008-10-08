/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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

package schemacrawler.tools.grep;


import javax.sql.DataSource;

import schemacrawler.crawl.DatabaseSchemaCrawler;
import schemacrawler.schemacrawler.CrawlHandler;
import schemacrawler.schemacrawler.SchemaCrawler;
import schemacrawler.tools.BaseSchemaCrawlerExecutable;
import schemacrawler.tools.schematext.SchemaTextFormatter;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
 */
public class GrepExecutable
  extends BaseSchemaCrawlerExecutable<GrepOptions>
{

  /**
   * Sets up default options.
   */
  public GrepExecutable()
  {
    toolOptions = new GrepOptions();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#execute(javax.sql.DataSource)
   */
  @Override
  public void execute(final DataSource dataSource)
    throws Exception
  {
    schemaCrawlerOptions.setTableInclusionRule(toolOptions
      .getTableInclusionRule());
    schemaCrawlerOptions.setSchemaInfoLevel(toolOptions
      .getSchemaTextDetailType().mapToInfoLevel());

    CrawlHandler chainedCrawlHandler = crawlHandler;
    if (chainedCrawlHandler == null)
    {
      chainedCrawlHandler = new SchemaTextFormatter(toolOptions);
    }

    final CrawlHandler handler = new GrepCrawlHandler(toolOptions,
                                                      chainedCrawlHandler);
    final SchemaCrawler crawler = new DatabaseSchemaCrawler(dataSource
      .getConnection());
    crawler.crawl(schemaCrawlerOptions, handler);
  }

}

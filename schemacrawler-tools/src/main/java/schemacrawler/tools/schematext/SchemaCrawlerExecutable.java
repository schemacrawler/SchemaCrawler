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
import schemacrawler.schemacrawler.CrawlHandler;
import schemacrawler.schemacrawler.SchemaCrawler;
import schemacrawler.tools.Executable;

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
  public final void execute(final DataSource dataSource)
    throws Exception
  {
    if (dataSource == null)
    {
      throw new IllegalArgumentException("No data-source provided");
    }

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

}

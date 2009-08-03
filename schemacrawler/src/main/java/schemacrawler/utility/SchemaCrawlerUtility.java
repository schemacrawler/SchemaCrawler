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

package schemacrawler.utility;


import java.sql.Connection;

import schemacrawler.crawl.CachingCrawlHandler;
import schemacrawler.crawl.DatabaseSchemaCrawler;
import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawler;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

/**
 * SchemaCrawler utility methods.
 * 
 * @author sfatehi
 */
public class SchemaCrawlerUtility
{

  public static Database getDatabase(final Connection connection,
                                     final SchemaCrawlerOptions schemaCrawlerOptions)
    throws SchemaCrawlerException
  {
    final CachingCrawlHandler crawlHandler = new CachingCrawlHandler();
    final SchemaCrawler schemaCrawler = new DatabaseSchemaCrawler(connection);
    schemaCrawler.crawl(schemaCrawlerOptions, crawlHandler);
    final Database database = crawlHandler.getDatabase();
    return database;
  }

  private SchemaCrawlerUtility()
  {
    // Prevent instantiation
  }

}

/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.utility;


import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.crawl.CachingCrawlHandler;
import schemacrawler.crawl.DatabaseSchemaCrawler;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawler;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.utility.test.TestUtility;

/**
 * SchemaCrawler utility methods.
 * 
 * @author sfatehi
 */
public class SchemaCrawlerUtility
{

  private static final Logger LOGGER = Logger.getLogger(TestUtility.class
    .getName());

  public static Schema getSchema(final Connection connection,
                                 final SchemaCrawlerOptions schemaCrawlerOptions)
  {
    SchemaCrawler schemaCrawler;
    try
    {
      final CachingCrawlHandler crawlHandler = new CachingCrawlHandler();
      schemaCrawler = new DatabaseSchemaCrawler(connection);
      schemaCrawler.crawl(schemaCrawlerOptions, crawlHandler);
      final Schema schema = crawlHandler.getSchema();
      return schema;
    }
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      return null;
    }
  }

  private SchemaCrawlerUtility()
  {
    // Prevent instantiation
  }

}

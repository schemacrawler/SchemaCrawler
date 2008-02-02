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

package schemacrawler.crawl;


import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

/**
 * Caches a crawled schema internally.
 * 
 * @author sfatehi
 */
public final class CachingCrawlerHandler
  implements CrawlHandler
{

  private final MutableSchema schema;
  private final SchemaInfoLevel infoLevel;

  /**
   * Creates a new caching crawl handler.
   */
  public CachingCrawlerHandler()
  {
    this("", null);
  }

  /**
   * Creates a new caching crawl handler.
   * 
   * @param catalogName
   *        Catalog name.
   * @param infoLevel
   *        Schema information level.
   */
  public CachingCrawlerHandler(final String catalogName,
                               final SchemaInfoLevel infoLevel)
  {
    schema = new MutableSchema(catalogName, "schema", "schema");
    if (infoLevel == null)
    {
      this.infoLevel = SchemaInfoLevel.maximum();
    }
    else
    {
      this.infoLevel = infoLevel;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.crawl.CrawlHandler#begin()
   */
  public void begin()
    throws SchemaCrawlerException
  {
    // do nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.crawl.CrawlHandler#end()
   */
  public void end()
    throws SchemaCrawlerException
  {
    // do nothing
  }

  /**
   * Column info level.
   * 
   * @return Column info level
   * @see CrawlHandler#getInfoLevelHint()
   */
  public SchemaInfoLevel getInfoLevelHint()
  {
    return infoLevel;
  }

  /**
   * Gets the entire schema.
   * 
   * @return Schema
   */
  public Schema getSchema()
  {
    return schema;
  }

  /**
   * {@inheritDoc}
   */
  public void handle(final DatabaseInfo databaseInfo)
  {
    schema.setDatabaseInfo(databaseInfo);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.crawl.CrawlHandler#handle(schemacrawler.schema.JdbcDriverInfo)
   */
  public void handle(final JdbcDriverInfo driverInfo)
  {
    schema.setJdbcDriverInfo(driverInfo);
  }

  /**
   * Provides information on the database schema.
   * 
   * @param procedure
   *        Procedure metadata.
   */
  public void handle(final Procedure procedure)
  {
    schema.addProcedure((MutableProcedure) procedure);
  }

  /**
   * Provides information on the database schema.
   * 
   * @param table
   *        Table metadata.
   */
  public void handle(final Table table)
  {
    schema.addTable((MutableTable) table);
  }

}

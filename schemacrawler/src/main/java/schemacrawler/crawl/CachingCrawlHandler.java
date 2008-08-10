/*
 * CatalogCrawler
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


import schemacrawler.schema.Catalog;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.CrawlHandler;
import schemacrawler.schemacrawler.SchemaCrawlerException;

/**
 * Caches a crawled catalog internally.
 * 
 * @author Sualeh Fatehi
 */
public final class CachingCrawlHandler
  implements CrawlHandler
{

  private final MutableCatalog catalog;

  /**
   * Creates a new caching crawl handler.
   */
  public CachingCrawlHandler()
  {
    this("");
  }

  /**
   * Creates a new caching crawl handler.
   * 
   * @param catalogName
   *        Catalog name
   */
  public CachingCrawlHandler(final String catalogName)
  {
    catalog = new MutableCatalog(catalogName);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.CrawlHandler#begin()
   */
  public void begin()
    throws SchemaCrawlerException
  {
    // do nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.CrawlHandler#end()
   */
  public void end()
    throws SchemaCrawlerException
  {
    // do nothing
  }

  /**
   * Gets the entire catalog.
   * 
   * @return Catalog
   */
  public Catalog getCatalog()
  {
    return catalog;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.CrawlHandler#handle(schemacrawler.schema.ColumnDataType)
   */
  public void handle(ColumnDataType dataType)
  {
    final String schemaName = dataType.getSchemaName();
    final MutableSchema schema = lookupOrCreateSchema(schemaName);
    schema.addColumnDataType((MutableColumnDataType) dataType);
  }

  /**
   * {@inheritDoc}
   */
  public void handle(final DatabaseInfo databaseInfo)
  {
    catalog.setDatabaseInfo(databaseInfo);
  }

  /**
   * {@inheritDoc}
   * 
   * @see catalogcrawler.catalogcrawler.CrawlHandler#handle(catalogcrawler.catalog.JdbcDriverInfo)
   */
  public void handle(final JdbcDriverInfo driverInfo)
  {
    catalog.setJdbcDriverInfo(driverInfo);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.CrawlHandler#handle(schemacrawler.schema.Procedure)
   */
  public void handle(final Procedure procedure)
  {
    final String schemaName = procedure.getSchemaName();
    final MutableSchema schema = lookupOrCreateSchema(schemaName);
    schema.addProcedure((MutableProcedure) procedure);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.CrawlHandler#handle(schemacrawler.schema.Table)
   */
  public void handle(final Table table)
  {
    final String schemaName = table.getSchemaName();
    final MutableSchema schema = lookupOrCreateSchema(schemaName);
    schema.addTable((MutableTable) table);
  }

  private MutableSchema lookupOrCreateSchema(final String schemaName)
  {
    MutableSchema schema = catalog.lookupSchema(schemaName);
    if (schema == null)
    {
      schema = new MutableSchema(catalog, schemaName);
      catalog.addSchema(schema);
    }
    return schema;
  }

}

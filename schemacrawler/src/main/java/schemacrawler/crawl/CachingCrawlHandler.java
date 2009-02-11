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
  public void handle(final ColumnDataType dataType)
  {
    final String schemaName = dataType.getSchemaName();
    if (schemaName != null)
    {
      final MutableSchema schema = lookupOrCreateSchema(schemaName);
      schema.addColumnDataType((MutableColumnDataType) dataType);
    }
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

  @Override
  public String toString()
  {
    return catalog.toString();
  }

}

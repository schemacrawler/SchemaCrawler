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


import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Database;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Table;
import schemacrawler.schema.WeakAssociations;
import schemacrawler.schemacrawler.CrawlHandler;
import schemacrawler.schemacrawler.SchemaCrawlerException;

/**
 * Caches a crawled database internally.
 * 
 * @author Sualeh Fatehi
 */
public final class CachingCrawlHandler
  implements CrawlHandler
{

  private final MutableDatabase database;

  /**
   * Creates a new caching crawl handler.
   * 
   * @param databaseName
   *        Database name
   */
  public CachingCrawlHandler(final String databaseName)
  {
    database = new MutableDatabase(databaseName);
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
   * Gets the entire database.
   * 
   * @return Database
   */
  public Database getDatabase()
  {
    return database;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.CrawlHandler#handle(schemacrawler.schema.ColumnDataType)
   */
  public void handle(final ColumnDataType dataType)
  {
    String catalogName = dataType.getCatalogName();
    if (catalogName == null)
    {
      catalogName = "";
    }
    final String schemaName = dataType.getSchemaName();
    if (schemaName != null)
    {
      final MutableSchema schema = lookupOrCreateSchema(catalogName, schemaName);
      schema.addColumnDataType((MutableColumnDataType) dataType);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void handle(final DatabaseInfo databaseInfo)
  {
    database.setDatabaseInfo(databaseInfo);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.databasecrawler.CrawlHandler#handle(databasecrawler.database.JdbcDriverInfo)
   */
  public void handle(final JdbcDriverInfo driverInfo)
  {
    database.setJdbcDriverInfo(driverInfo);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.CrawlHandler#handle(schemacrawler.schema.Procedure)
   */
  public void handle(final Procedure procedure)
  {
    final String catalogName = procedure.getCatalogName();
    final String schemaName = procedure.getSchemaName();
    final MutableSchema schema = lookupOrCreateSchema(catalogName, schemaName);
    schema.addProcedure((MutableProcedure) procedure);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.CrawlHandler#handle(schemacrawler.schema.Table)
   */
  public void handle(final Table table)
  {
    final String catalogName = table.getCatalogName();
    final String schemaName = table.getSchemaName();
    final MutableSchema schema = lookupOrCreateSchema(catalogName, schemaName);
    schema.addTable((MutableTable) table);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.CrawlHandler#handle(schemacrawler.schema.WeakAssociations)
   */
  public void handle(final WeakAssociations weakAssociations)
    throws SchemaCrawlerException
  {
    database.setWeakAssociations(weakAssociations);
  }

  @Override
  public String toString()
  {
    return database.toString();
  }

  private MutableSchema lookupSchema(final String catalogName,
                                     final String schemaName)
  {
    String catalogName1 = catalogName;
    if (catalogName1 == null)
    {
      catalogName1 = "";
    }
    MutableCatalog catalog = database.lookupCatalog(catalogName1);
    if (catalog == null)
    {
      catalog = new MutableCatalog(catalogName1);
      database.addCatalog(catalog);
    }

    MutableSchema schema = catalog.lookupSchema(schemaName);
    if (schema == null)
    {
      schema = new MutableSchema(catalog, schemaName);
      catalog.addSchema(schema);
    }
    return schema;
  }

}

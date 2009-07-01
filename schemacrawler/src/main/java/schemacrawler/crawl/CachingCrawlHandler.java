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
import schemacrawler.schema.ColumnMap;
import schemacrawler.schema.Database;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Table;
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

  private MutableDatabase database;

  /**
   * Creates a new caching crawl handler.
   */
  public CachingCrawlHandler()
  {
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.CrawlHandler#begin()
   */
  public void begin()
    throws SchemaCrawlerException
  { // do nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.CrawlHandler#end()
   */
  public void end()
    throws SchemaCrawlerException
  { // do nothing
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
  { // do nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.CrawlHandler#handle(schemacrawler.schema.WeakAssociations)
   */
  public void handle(final ColumnMap[] weakAssociations)
    throws SchemaCrawlerException
  { // do nothing
  }

  /**
   * {@inheritDoc}
   */
  public void handle(final DatabaseInfo databaseInfo)
  { // do nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.CrawlHandler#handle(schemacrawler.schema.Procedure)
   */
  public void handle(final Procedure procedure)
  { // do nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.CrawlHandler#handle(schemacrawler.schema.Table)
   */
  public void handle(final Table table)
  { // do nothing
  }

  void setDatabase(final MutableDatabase database)
  {
    this.database = database;
  }

}

/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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


import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

/**
 * 
 */
final class CachingCrawlerHandler
  implements CrawlHandler
{

  private final SchemaInfoLevel infoLevel;
  private final MutableSchema schema;

  /**
   * @param writer
   *        Writer to output to.
   */
  CachingCrawlerHandler(final String catalogName,
                        final SchemaInfoLevel columnInfoLevel)
  {
    schema = new MutableSchema(catalogName, "schema", "schema");
    infoLevel = columnInfoLevel;
  }

  /**
   * @see CrawlHandler#begin()
   */
  public void begin()
    throws SchemaCrawlerException
  {
    // do nothing
  }

  /**
   * @see CrawlHandler#end()
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
   * @see CrawlHandler#handle(DatabaseInfo)
   */
  public void handle(final DatabaseInfo databaseInfo)
  {
    schema.setDatabaseInfo(databaseInfo);
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

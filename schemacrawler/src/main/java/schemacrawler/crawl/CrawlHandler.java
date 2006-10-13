/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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
import schemacrawler.schema.Table;

/**
 * Handler for SchemaCrawler.
 */
public interface CrawlHandler
{

  /**
   * Hint for the level detail requires. Helps SchemaCrawler optimize
   * the metadata queries. One of the level constants defined in
   * SchemaCrawler.
   * 
   * @return Level hint
   */
  SchemaInfoLevel getInfoLevelHint();

  /**
   * Handles the begin of the crawl.
   * 
   * @throws SchemaCrawlerException
   *         On an exception
   */
  void begin()
    throws SchemaCrawlerException;

  /**
   * Handles the end of the crawl.
   * 
   * @throws SchemaCrawlerException
   *         On an exception
   */
  void end()
    throws SchemaCrawlerException;

  /**
   * Provides information on the database schema.
   * 
   * @param table
   *        Table information
   * @throws SchemaCrawlerException
   *         On an exception
   */
  void handle(final Table table)
    throws SchemaCrawlerException;

  /**
   * Provides information on the database schema.
   * 
   * @param procedure
   *        Procedure information
   * @throws SchemaCrawlerException
   *         On an exception
   */
  void handle(final Procedure procedure)
    throws SchemaCrawlerException;

  /**
   * Provides information on the database schema.
   * 
   * @param databaseInfo
   *        Database information
   * @throws SchemaCrawlerException
   *         On an exception
   */
  void handle(final DatabaseInfo databaseInfo)
    throws SchemaCrawlerException;

}

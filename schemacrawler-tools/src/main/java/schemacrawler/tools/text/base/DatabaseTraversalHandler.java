/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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

package schemacrawler.tools.text.base;


import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;

/**
 * Handler for SchemaCrawler.
 */
public interface DatabaseTraversalHandler
{

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
   * Handles information on column data types. This method may be called
   * more than once, once for each schema.
   * 
   * @param dataType
   *        Column data type information
   * @throws SchemaCrawlerException
   *         On an exception
   */
  void handle(ColumnDataType dataType)
    throws SchemaCrawlerException;

  /**
   * Provides information on the database schema.
   * 
   * @param procedure
   *        Procedure information
   * @throws SchemaCrawlerException
   *         On an exception
   */
  void handle(Procedure procedure)
    throws SchemaCrawlerException;

  /**
   * Handles information on the database.
   * 
   * @param schemaCrawlerInfo
   *        SchemaCrawler information
   * @param databaseInfo
   *        Database information
   * @param jdbcDriverInfo
   *        JDBC driver information
   * @throws SchemaCrawlerException
   *         On an exception
   */
  void handle(SchemaCrawlerInfo schemaCrawlerInfo,
              DatabaseInfo databaseInfo,
              JdbcDriverInfo jdbcDriverInfo)
    throws SchemaCrawlerException;

  /**
   * Provides information on the database schema.
   * 
   * @param table
   *        Table information
   * @throws SchemaCrawlerException
   *         On an exception
   */
  void handle(Table table)
    throws SchemaCrawlerException;

}

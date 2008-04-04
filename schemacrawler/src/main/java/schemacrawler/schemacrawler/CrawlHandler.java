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

package schemacrawler.schemacrawler;


import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Table;

/**
 * Handler for SchemaCrawler.
 */
public interface CrawlHandler
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
   * Provides information on the database schema.
   * 
   * @param databaseInfo
   *        Database information
   * @throws SchemaCrawlerException
   *         On an exception
   */
  void handle(final DatabaseInfo databaseInfo)
    throws SchemaCrawlerException;

  /**
   * Provides information on the database schema.
   * 
   * @param driverInfo
   *        JDBC driver information
   * @throws SchemaCrawlerException
   *         On an exception
   */
  void handle(final JdbcDriverInfo driverInfo)
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
   * @param table
   *        Table information
   * @throws SchemaCrawlerException
   *         On an exception
   */
  void handle(final Table table)
    throws SchemaCrawlerException;

}

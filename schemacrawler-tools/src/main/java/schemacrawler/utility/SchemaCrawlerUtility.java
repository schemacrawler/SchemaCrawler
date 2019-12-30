/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.utility;


import java.sql.Connection;
import java.sql.ResultSet;
import java.util.logging.Level;

import schemacrawler.crawl.ResultsCrawler;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerSQLException;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import sf.util.DatabaseUtility;
import sf.util.ObjectToString;
import sf.util.SchemaCrawlerLogger;
import sf.util.UtilityMarker;

/**
 * SchemaCrawler utility methods.
 *
 * @author sfatehi
 */
@UtilityMarker
public final class SchemaCrawlerUtility
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(SchemaCrawlerUtility.class.getName());

  /**
   * Allows building of database specific options programatically, using an
   * existing SchemaCrawler database plugin as a starting point.
   *
   * @return SchemaRetrievalOptionsBuilder
   * @throws SchemaCrawlerException
   *   On an exception.
   */
  private static SchemaRetrievalOptionsBuilder buildSchemaRetrievalOptions(final Connection connection)
    throws SchemaCrawlerException
  {
    checkConnection(connection);
    final DatabaseConnectorRegistry registry =
      DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    final DatabaseConnector dbConnector =
      registry.lookupDatabaseConnector(connection);
    LOGGER.log(Level.INFO,
               "Using database plugin for "
               + dbConnector.getDatabaseServerType());

    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
      dbConnector.getSchemaRetrievalOptionsBuilder(connection);
    return schemaRetrievalOptionsBuilder;
  }

  private static void checkConnection(final Connection connection)
    throws SchemaCrawlerException
  {
    try
    {
      DatabaseUtility.checkConnection(connection);
    }
    catch (final SchemaCrawlerSQLException e)
    {
      throw new SchemaCrawlerException("Bad database connection", e);
    }
  }

  private static void checkResultSet(final ResultSet resultSet)
    throws SchemaCrawlerException
  {
    try
    {
      DatabaseUtility.checkResultSet(resultSet);
    }
    catch (final SchemaCrawlerSQLException e)
    {
      throw new SchemaCrawlerException("Bad result-set", e);
    }
  }

  /**
   * Crawls a database, and returns a catalog.
   *
   * @param connection
   *   Live database connection.
   * @param schemaCrawlerOptions
   *   Options.
   * @return Database catalog.
   * @throws SchemaCrawlerException
   *   On an exception.
   */
  public static Catalog getCatalog(final Connection connection,
                                   final SchemaCrawlerOptions schemaCrawlerOptions)
    throws SchemaCrawlerException
  {
    checkConnection(connection);
    if (LOGGER.isLoggable(Level.CONFIG))
    {
      LOGGER.log(Level.CONFIG, ObjectToString.toString(schemaCrawlerOptions));
    }

    final SchemaRetrievalOptions schemaRetrievalOptions =
      matchSchemaRetrievalOptions(connection);
    final SchemaCrawler schemaCrawler = new SchemaCrawler(connection,
                                                          schemaRetrievalOptions,
                                                          schemaCrawlerOptions);
    final Catalog catalog = schemaCrawler.crawl();

    return catalog;
  }

  /**
   * Obtains result-set metadata from a live result-set.
   *
   * @param resultSet
   *   Live result-set.
   * @return Result-set metadata.
   * @throws SchemaCrawlerException
   *   On an exception.
   */
  public static ResultsColumns getResultsColumns(final ResultSet resultSet)
    throws SchemaCrawlerException
  {
    // NOTE: Some JDBC drivers like SQLite may not work with closed
    // result-sets
    checkResultSet(resultSet);
    final ResultsCrawler resultSetCrawler = new ResultsCrawler(resultSet);
    final ResultsColumns resultsColumns = resultSetCrawler.crawl();
    return resultsColumns;
  }

  /**
   * Returns database specific options using an existing SchemaCrawler database
   * plugin.
   *
   * @return SchemaRetrievalOptions
   * @throws SchemaCrawlerException
   *   On an exception.
   */
  public static SchemaRetrievalOptions matchSchemaRetrievalOptions(final Connection connection)
    throws SchemaCrawlerException
  {
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
      buildSchemaRetrievalOptions(connection);

    final SchemaRetrievalOptions schemaRetrievalOptions =
      schemaRetrievalOptionsBuilder.toOptions();

    return schemaRetrievalOptions;
  }

  private SchemaCrawlerUtility()
  {
    // Prevent instantiation
  }

}

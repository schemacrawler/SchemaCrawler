/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static sf.util.DatabaseUtility.checkConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.logging.Level;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
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

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SchemaCrawlerUtility.class.getName());

  /**
   * Allows building of database specific options programatically.
   *
   * @return DatabaseSpecificOverrideOptionsBuilder
   * @throws SchemaCrawlerException
   *         On an exception.
   */
  public static DatabaseSpecificOverrideOptionsBuilder buildDatabaseSpecificOverrideOptions()
    throws SchemaCrawlerException
  {
    return new DatabaseSpecificOverrideOptionsBuilder();
  }

  /**
   * Allows building of database specific options programatically, using
   * an existing SchemaCrawler database plugin as a starting point.
   *
   * @return DatabaseSpecificOverrideOptionsBuilder
   * @throws SchemaCrawlerException
   *         On an exception.
   */
  public static DatabaseSpecificOverrideOptionsBuilder buildDatabaseSpecificOverrideOptions(final Connection connection)
    throws SchemaCrawlerException
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    final DatabaseConnector dbConnector = registry
      .lookupDatabaseConnector(connection);
    LOGGER
      .log(Level.INFO,
           "Using database plugin for " + dbConnector.getDatabaseServerType());

    final DatabaseSpecificOverrideOptionsBuilder databaseSpecificOverrideOptionsBuilder = dbConnector
      .getDatabaseSpecificOverrideOptionsBuilder();
    return databaseSpecificOverrideOptionsBuilder;
  }

  /**
   * Crawls a database, and returns a catalog.
   *
   * @param connection
   *        Live database connection.
   * @param schemaCrawlerOptions
   *        Options.
   * @return Database catalog.
   * @throws SchemaCrawlerException
   *         On an exception.
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

    final DatabaseSpecificOverrideOptions dbSpecificOverrideOptions = matchDatabaseSpecificOverrideOptions(connection);
    final SchemaCrawler schemaCrawler = new SchemaCrawler(connection,
                                                          dbSpecificOverrideOptions);
    final Catalog catalog = schemaCrawler.crawl(schemaCrawlerOptions);

    return catalog;
  }

  /**
   * Obtains result-set metadata from a live result-set.
   *
   * @param resultSet
   *        Live result-set.
   * @return Result-set metadata.
   */
  public static ResultsColumns getResultColumns(final ResultSet resultSet)
  {
    return SchemaCrawler.getResultColumns(resultSet);
  }

  /**
   * Returns database specific options using an existing SchemaCrawler
   * database plugin.
   *
   * @return DatabaseSpecificOverrideOptions
   * @throws SchemaCrawlerException
   *         On an exception.
   */
  public static DatabaseSpecificOverrideOptions matchDatabaseSpecificOverrideOptions(final Connection connection)
    throws SchemaCrawlerException
  {
    final DatabaseSpecificOverrideOptionsBuilder databaseSpecificOverrideOptionsBuilder = buildDatabaseSpecificOverrideOptions(connection);

    final DatabaseSpecificOverrideOptions dbSpecificOverrideOptions = databaseSpecificOverrideOptionsBuilder
      .toOptions();

    return dbSpecificOverrideOptions;
  }

  private SchemaCrawlerUtility()
  {
    // Prevent instantiation
  }

}

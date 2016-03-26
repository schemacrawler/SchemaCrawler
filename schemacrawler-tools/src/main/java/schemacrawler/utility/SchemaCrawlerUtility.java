/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
import java.util.logging.Logger;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

/**
 * SchemaCrawler utility methods.
 *
 * @author sfatehi
 */
public final class SchemaCrawlerUtility
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerUtility.class.getName());

  public static Catalog getCatalog(final Connection connection,
                                   final SchemaCrawlerOptions schemaCrawlerOptions)
                                     throws SchemaCrawlerException
  {
    checkConnection(connection);

    final DatabaseSpecificOverrideOptions dbSpecificOverrideOptions = matchDatabaseSpecificOverrideOptions(connection);
    final SchemaCrawler schemaCrawler = new SchemaCrawler(connection,
                                                          dbSpecificOverrideOptions);
    final Catalog catalog = schemaCrawler.crawl(schemaCrawlerOptions);

    return catalog;
  }

  public static ResultsColumns getResultColumns(final ResultSet resultSet)
  {
    return SchemaCrawler.getResultColumns(resultSet);
  }

  public static DatabaseSpecificOverrideOptions matchDatabaseSpecificOverrideOptions(final Connection connection)
    throws SchemaCrawlerException
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    final DatabaseConnector dbConnector = registry
      .lookupDatabaseConnector(connection);
    final DatabaseSpecificOverrideOptions dbSpecificOverrideOptions = dbConnector
      .getDatabaseSpecificOverrideOptionsBuilder().toOptions();

    LOGGER
      .log(Level.INFO,
           "Using database plugin for " + dbConnector.getDatabaseServerType());
    return dbSpecificOverrideOptions;
  }

  private SchemaCrawlerUtility()
  {
    // Prevent instantiation
  }

}

/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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

package schemacrawler.utility;


import static java.util.Objects.requireNonNull;
import static sf.util.DatabaseUtility.checkConnection;
import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.sql.ResultSet;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.databaseconnector.DatabaseSystemConnector;

/**
 * SchemaCrawler utility methods.
 *
 * @author sfatehi
 */
public final class SchemaCrawlerUtility
{

  public static Catalog
    getCatalog(final Connection connection, final DatabaseConnector dbConnector,
               final SchemaCrawlerOptions schemaCrawlerOptions)
                 throws SchemaCrawlerException
  {
    checkConnection(connection);
    requireNonNull(dbConnector,
                   "No database specific override options provided");

    final DatabaseSystemConnector dbSystemConnector = dbConnector
      .getDatabaseSystemConnector();
    final DatabaseSpecificOverrideOptions dbSpecificOverrideOptions = dbSystemConnector
      .getDatabaseSpecificOverrideOptionsBuilder().toOptions();

    final Catalog catalog = SchemaCrawlerUtility
      .getCatalog(connection, dbSpecificOverrideOptions, schemaCrawlerOptions);

    return catalog;
  }

  public static Catalog
    getCatalog(final Connection connection,
               final DatabaseSpecificOverrideOptions dbSpecificOverrideOptions,
               final SchemaCrawlerOptions schemaCrawlerOptions)
                 throws SchemaCrawlerException
  {
    checkConnection(connection);
    requireNonNull(dbSpecificOverrideOptions,
                   "No database specific override options provided");

    final SchemaCrawler schemaCrawler = new SchemaCrawler(connection,
                                                          dbSpecificOverrideOptions);
    final Catalog catalog = schemaCrawler.crawl(schemaCrawlerOptions);
    return catalog;
  }

  public static Catalog
    getCatalog(final Connection connection,
               final SchemaCrawlerOptions schemaCrawlerOptions)
                 throws SchemaCrawlerException
  {
    return getCatalog(connection, new DatabaseSpecificOverrideOptions(),
                      schemaCrawlerOptions);
  }

  public static Catalog
    getCatalog(final Connection connection, final String dbServerType,
               final SchemaCrawlerOptions schemaCrawlerOptions)
                 throws SchemaCrawlerException
  {
    checkConnection(connection);
    if (!isBlank(dbServerType))
    {
      throw new SchemaCrawlerException("No database server type provided");
    }

    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    final DatabaseConnector dbConnector = registry
      .lookupDatabaseSystemIdentifier(dbServerType);

    return getCatalog(connection, dbConnector, schemaCrawlerOptions);
  }

  public static ResultsColumns getResultColumns(final ResultSet resultSet)
  {
    return SchemaCrawler.getResultColumns(resultSet);
  }

  private SchemaCrawlerUtility()
  {
    // Prevent instantiation
  }

}

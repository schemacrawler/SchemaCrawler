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
package schemacrawler.tools.databaseconnector;


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.DatabaseServerType;
import sf.util.DatabaseUtility;

public abstract class DatabaseConnector
{

  protected static final DatabaseConnector UNKNOWN = new DatabaseConnector()
  {

    @Override
    protected Pattern getConnectionUrlPattern()
    {
      return Pattern.compile(".*");
    }

  };
  private final DatabaseServerType dbServerType;
  private final String connectionHelpResource;

  private final DatabaseSystemConnector dbSystemConnector;

  protected DatabaseConnector(final DatabaseServerType dbServerType,
                              final String connectionHelpResource,
                              final DatabaseSystemConnector dbSystemConnector)
  {
    this.dbServerType = requireNonNull(dbServerType,
                                       "No database server type provided");

    if (isBlank(connectionHelpResource))
    {
      throw new IllegalArgumentException("No connection help resource provided");
    }
    this.connectionHelpResource = connectionHelpResource;

    this.dbSystemConnector = dbSystemConnector;
  }

  protected DatabaseConnector(final DatabaseServerType dbServerType,
                              final String connectionHelpResource,
                              final String configResource,
                              final String informationSchemaViewsResourceFolder)
  {
    this.dbServerType = requireNonNull(dbServerType,
                                       "No database server type provided");

    if (isBlank(connectionHelpResource))
    {
      throw new IllegalArgumentException("No connection help resource provided");
    }
    this.connectionHelpResource = connectionHelpResource;

    dbSystemConnector = new DatabaseSystemConnector(dbServerType,
                                                    configResource,
                                                    informationSchemaViewsResourceFolder);
  }

  private DatabaseConnector()
  {
    dbServerType = DatabaseServerType.UNKNOWN;
    connectionHelpResource = null;
    dbSystemConnector = DatabaseSystemConnector.UNKNOWN;
  }

  public String getConnectionHelpResource()
  {
    return connectionHelpResource;
  }

  public DatabaseServerType getDatabaseServerType()
  {
    return dbServerType;
  }

  public DatabaseSystemConnector getDatabaseSystemConnector()
  {
    return dbSystemConnector;
  }

  public final boolean isConnectionForConnector(final Connection connection)
    throws SchemaCrawlerException
  {
    DatabaseUtility.checkConnection(connection);
    try
    {
      final String url = connection.getMetaData().getURL();
      if (isBlank(url))
      {
        throw new SchemaCrawlerException("Cannot check database connection URL");
      }
      final Pattern connectionUrlPattern = getConnectionUrlPattern();
      if (connectionUrlPattern == null)
      {
        throw new IllegalArgumentException("No connection URL pattern provided");
      }
      return connectionUrlPattern.matcher(url).matches();
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Cannot check database connection URL",
                                       e);
    }
  }

  protected abstract Pattern getConnectionUrlPattern();

}

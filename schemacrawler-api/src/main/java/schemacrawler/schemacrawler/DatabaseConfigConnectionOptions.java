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

package schemacrawler.schemacrawler;


import static sf.util.Utility.isBlank;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DatabaseConfigConnectionOptions
  extends BaseDatabaseConnectionOptions
{

  private static final long serialVersionUID = -8141436553988174836L;

  private static final Logger LOGGER = Logger
    .getLogger(DatabaseConfigConnectionOptions.class.getName());

  private static final String HOST = "host";
  private static final String PORT = "port";
  private static final String DATABASE = "database";
  private static final String URLX = "urlx";

  public DatabaseConfigConnectionOptions(final Map<String, String> properties)
    throws SchemaCrawlerException
  {
    super(properties);

    setConnectionProperties(properties.get(URLX));
  }

  public String getDatabase()
  {
    return connectionProperties.get(DATABASE);
  }

  public String getHost()
  {
    return connectionProperties.get(HOST);
  }

  public int getPort()
  {
    final String port = connectionProperties.get(PORT);
    try
    {
      return Integer.parseInt(port);
    }
    catch (final NumberFormatException e)
    {
      throw new IllegalArgumentException("Cannot connect to port, " + port);
    }
  }

  public void setDatabase(final String database)
  {
    // (database can be an empty string)
    if (database != null)
    {
      connectionProperties.put(DATABASE, database);
    }
  }

  public void setHost(final String host)
  {
    if (!isBlank(host))
    {
      connectionProperties.put(HOST, host);
    }
  }

  public void setPort(final int port)
  {
    if (port > 0)
    {
      connectionProperties.put(PORT, String.valueOf(port));
    }
    else
    {
      LOGGER.log(Level.WARNING, "Cannot connect to port, " + port);
    }
  }

}

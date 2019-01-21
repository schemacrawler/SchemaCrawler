/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.databaseconnector;


import static sf.util.Utility.isBlank;

import java.util.Map;
import java.util.logging.Level;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

/**
 * Data source with connection information provided from a configuration
 * file.
 *
 * @author Sualeh Fatehi
 */
public final class DatabaseConfigConnectionOptions
  extends BaseDatabaseConnectionOptions
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(DatabaseConfigConnectionOptions.class.getName());

  private static final String HOST = "host";
  private static final String PORT = "port";
  private static final String DATABASE = "database";
  private static final String URLX = "urlx";

  public DatabaseConfigConnectionOptions(final UserCredentials userCredentials,
                                         final Map<String, String> properties)
    throws SchemaCrawlerException
  {
    super(userCredentials, properties);
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

  public String getUrlX()
  {
    return connectionProperties.get(URLX);
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
      LOGGER.log(Level.WARNING,
                 new StringFormat("Cannot connect to port, %d", port));
    }
  }

  public void setUrlX(final String urlx)
  {
    if (!isBlank(urlx))
    {
      connectionProperties.put(URLX, urlx);
    }
    else
    {
      connectionProperties.remove(URLX);
    }
  }

}

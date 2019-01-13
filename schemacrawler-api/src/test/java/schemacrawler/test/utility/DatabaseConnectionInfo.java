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
package schemacrawler.test.utility;


import static sf.util.Utility.isBlank;

import java.io.Serializable;

public class DatabaseConnectionInfo
  implements Serializable
{

  private static final long serialVersionUID = 3513025340881301828L;

  private final String host;
  private final int port;
  private final String database;
  private final String url;

  public DatabaseConnectionInfo(final String host,
                                final int port,
                                final String database,
                                final String url)
  {
    if (isBlank(host))
    {
      throw new IllegalArgumentException("No host provided");
    }
    if (port <= 0)
    {
      throw new IllegalArgumentException("No port provided");
    }
    if (isBlank(database))
    {
      throw new IllegalArgumentException("No database provided");
    }
    if (isBlank(url))
    {
      throw new IllegalArgumentException("No url provided");
    }

    this.host = host;
    this.port = port;
    this.database = database;
    this.url = url;
  }

  public String getConnectionUrl()
  {
    return url;
  }

  public String getDatabase()
  {
    return database;
  }

  public String getHost()
  {
    return host;
  }

  public int getPort()
  {
    return port;
  }

}

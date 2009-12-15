/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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


import java.util.HashMap;
import java.util.Map;

import sf.util.TemplatingUtility;
import sf.util.Utility;

public final class DatabaseConfigConnectionOptions
  extends BaseDatabaseConnectionOptions
{

  private static final long serialVersionUID = -8141436553988174836L;

  private static final String DRIVER = "driver";
  private static final String URL = "url";
  private static final String HOST = "host";
  private static final String PORT = "port";
  private static final String USER = "user";
  private static final String PASSWORD = "password";

  private final Map<String, String> properties;

  public DatabaseConfigConnectionOptions(final Map<String, String> properties)
    throws SchemaCrawlerException
  {
    if (properties == null)
    {
      throw new SchemaCrawlerException("No connection properties provided");
    }
    this.properties = new HashMap<String, String>(properties);

    loadJdbcDriver(properties.get(DRIVER));
    setUser(properties.get(USER));
    setPassword(properties.get(PASSWORD));
  }

  @Override
  public String getConnectionUrl()
  {
    final Map<String, String> properties = new HashMap<String, String>(this.properties);
    TemplatingUtility.substituteVariables(properties);
    return properties.get(URL);
  }

  public String getHost()
  {
    return properties.get(HOST);
  }

  public int getPort()
  {
    try
    {
      return Integer.parseInt(properties.get(PORT));
    }
    catch (final NumberFormatException e)
    {
      return 0;
    }
  }

  public void setHost(final String host)
  {
    if (!Utility.isBlank(host))
    {
      properties.put(HOST, host);
    }
  }

  public void setPort(final int port)
  {
    if (port > 0)
    {
      properties.put(PORT, String.valueOf(port));
    }
  }

}

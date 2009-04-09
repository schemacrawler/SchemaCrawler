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
package schemacrawler.main.dbconnector;


/**
 * Additional options needed for Spring.
 * 
 * @author Sualeh Fatehi
 */
public class BundledDriverOptions
  extends BaseConnectorOptions
{

  private static final long serialVersionUID = 5125868244511892692L;

  private String host;
  private int port;
  private String database;

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

  public boolean hasDatabase()
  {
    return database != null;
  }

  public boolean hasHost()
  {
    return host != null;
  }

  public boolean hasPort()
  {
    return port > 0;
  }

  public void setDatabase(final String database)
  {
    this.database = database;
  }

  public void setHost(final String host)
  {
    this.host = host;
  }

  public void setPort(final int port)
  {
    this.port = port;
  }

}

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


import sf.util.Utility;

public final class DatabaseConnectionOptions
  extends BaseDatabaseConnectionOptions
{

  private static final long serialVersionUID = -8141436553988174836L;

  private final String connectionUrl;

  public DatabaseConnectionOptions(final String jdbcDriverClassName,
                                   final String connectionUrl)
    throws SchemaCrawlerException
  {
    if (Utility.isBlank(connectionUrl))
    {
      throw new SchemaCrawlerException("No database connection URL provided");
    }
    this.connectionUrl = connectionUrl;
    loadJdbcDriver(jdbcDriverClassName);
  }

  @Override
  public String getConnectionUrl()
  {
    return connectionUrl;
  }

}

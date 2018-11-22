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
package schemacrawler.server.sqlserver;


import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.regex.Pattern;

import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.iosource.ClasspathInputResource;
import schemacrawler.utility.Query;
import schemacrawler.utility.QueryUtility;
import sf.util.SchemaCrawlerLogger;

public final class SqlServerDatabaseConnector
  extends DatabaseConnector
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SqlServerDatabaseConnector.class.getName());

  public SqlServerDatabaseConnector()
    throws IOException
  {
    super(new DatabaseServerType("sqlserver", "Microsoft SQL Server"),
          new ClasspathInputResource("/help/Connections.sqlserver.txt"),
          new ClasspathInputResource("/schemacrawler-sqlserver.config.properties"),
          (informationSchemaViewsBuilder,
           connection) -> informationSchemaViewsBuilder
             .fromResourceFolder("/sqlserver.information_schema"),
          url -> Pattern.matches("jdbc:sqlserver:.*", url));
  }

  @Override
  protected String getCatalogName(final Connection connection)
  {
    if (connection == null)
    {
      return "";
    }
    try
    {
      final Query query = new Query("Get catalog",
                                    "SELECT CONVERT(SYSNAME, SERVERPROPERTY('servername'))");
      final Object catalog = QueryUtility.executeForScalar(query, connection);
      if (catalog != null)
      {
        return catalog.toString();
      }
      else
      {
        return "";
      }
    }
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.WARNING, "");
      return "";
    }
  }

}

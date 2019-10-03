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

package schemacrawler.integration.test;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import org.testcontainers.containers.PostgreSQLContainer;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.SchemaCrawlerLogger;

public class EmbeddedPostgreSQLWrapper
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(EmbeddedPostgreSQLWrapper.class.getName());

  private final Thread hook;
  private PostgreSQLContainer postgreSQL;

  public EmbeddedPostgreSQLWrapper()
  {
    hook = new Thread(() -> {
      stopServer();
    });
  }

  public String getConnectionUrl()
  {
    return postgreSQL.getJdbcUrl();
  }

  public void startServer()
    throws SchemaCrawlerException
  {
    try
    {
      postgreSQL = new PostgreSQLContainer<>().withDatabaseName(getDatabase())
        .withUsername(getUser()).withPassword(getPassword());
      postgreSQL.start();
      /**
       postgreSQL.start(runtimeConfig,
       "localhost",
       findFreePort(),
       getDatabase(),
       getUser(),
       getPassword(),
       Arrays.asList("-E", "'UTF-8'"));
       **/
      Runtime.getRuntime().addShutdownHook(hook);
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not start PostgreSQL server", e);
    }
  }

  public void stopServer()
  {
    if (postgreSQL != null)
    {
      LOGGER.log(Level.FINE, "Stopping PostgreSQL server");
      postgreSQL.stop();
      postgreSQL = null;
    }
  }

  public String getPassword()
  {
    return "schemacrawler";
  }

  public String getUser()
  {
    return "schemacrawler";
  }

  public String getDatabase()
  {
    return "schemacrawler";
  }

}

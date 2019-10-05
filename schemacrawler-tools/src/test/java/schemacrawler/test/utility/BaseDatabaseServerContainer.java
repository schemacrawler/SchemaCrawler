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


import java.util.logging.Level;

import org.testcontainers.containers.JdbcDatabaseContainer;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.SchemaCrawlerLogger;

public class BaseDatabaseServerContainer<C extends JdbcDatabaseContainer>
  implements DatabaseServerContainer
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(BaseDatabaseServerContainer.class.getName());

  private final String databaseName;
  private final C dbContainer;

  protected BaseDatabaseServerContainer(final C dbContainer)
  {
    this.dbContainer = dbContainer;
    this.databaseName = null;
  }

  protected BaseDatabaseServerContainer(final C dbContainer,
                                        final String databaseName)
  {
    this.dbContainer = dbContainer;
    this.databaseName = databaseName;
  }

  public String getConnectionUrl()
  {
    return dbContainer.getJdbcUrl();
  }

  public void startServer()
    throws SchemaCrawlerException
  {
    try
    {
      if (databaseName != null)
      {
        dbContainer.withDatabaseName(databaseName);
      }

      LOGGER.log(Level.FINE, "Starting " + dbContainer.getDockerImageName());
      System.out.println("Starting " + dbContainer.getDockerImageName());
      dbContainer.start();

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        stopServer();
      }));
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not start database server", e);
    }
  }

  public void stopServer()
  {
    if (dbContainer != null)
    {
      LOGGER.log(Level.FINE, "Stopping " + dbContainer.getDockerImageName());
      System.out.println("Stopping " + dbContainer.getDockerImageName());
      dbContainer.stop();
    }
  }

  public String getPassword()
  {
    return dbContainer.getPassword();
  }

  public String getUser()
  {
    return dbContainer.getUsername();
  }

}

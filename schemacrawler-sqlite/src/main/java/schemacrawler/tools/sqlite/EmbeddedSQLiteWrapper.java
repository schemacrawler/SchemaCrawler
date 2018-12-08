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
package schemacrawler.tools.sqlite;


import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SingleUseUserCredentials;
import schemacrawler.tools.integration.embeddeddb.EmbeddedDatabaseWrapper;

public class EmbeddedSQLiteWrapper
  extends EmbeddedDatabaseWrapper
{

  private Path databaseFile;

  @Override
  public ConnectionOptions createConnectionOptions()
    throws SchemaCrawlerException
  {
    requireNonNull(databaseFile, "Database file not loaded");

    final Config config = new Config();
    config.put("server", "sqlite");
    config.put("database", databaseFile.toString());
    try
    {
      final ConnectionOptions connectionOptions = new SQLiteDatabaseConnector()
        .newDatabaseConnectionOptions(new SingleUseUserCredentials(), config);
      return connectionOptions;
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException("Cannot read datad database file, "
                                       + databaseFile,
                                       e);
    }
  }

  @Override
  public String getConnectionUrl()
  {
    requireNonNull(databaseFile, "Database file not loaded");
    return "jdbc:sqlite:" + databaseFile.toString();
  }

  @Override
  public String getDatabase()
  {
    if (databaseFile == null)
    {
      return "";
    }
    else
    {
      return databaseFile.toString();
    }
  }

  @Override
  public String getPassword()
  {
    return "schemacrawler";
  }

  @Override
  public String getUser()
  {
    return "schemacrawler";
  }

  @Override
  public void loadDatabaseFile(final Path dbFile)
    throws IOException
  {
    databaseFile = checkDatabaseFile(dbFile);
  }

  @Override
  public void startServer()
    throws SchemaCrawlerException
  {
    // No-op
  }

  @Override
  public void stopServer()
    throws SchemaCrawlerException
  {
    // No-op
  }

}

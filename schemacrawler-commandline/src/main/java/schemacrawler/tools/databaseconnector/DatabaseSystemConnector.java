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
package schemacrawler.tools.databaseconnector;


import static java.util.Objects.requireNonNull;

import java.sql.Connection;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.DatabaseConfigConnectionOptions;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.DatabaseServerType;

public class DatabaseSystemConnector
{

  private final class NoOpExecutable
    extends BaseExecutable
  {
    private NoOpExecutable(final String command)
    {
      super(command);
    }

    @Override
    public void execute(final Connection connection)
      throws Exception
    {
      // No-op
    }

    @Override
    public void
      execute(final Connection connection,
              final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
                throws Exception
    {
      // No-op
    }
  }

  protected static final DatabaseSystemConnector UNKNOWN = new DatabaseSystemConnector();
  private final DatabaseServerType dbServerType;
  private final String configResource;
  private final String informationSchemaViewsResourceFolder;

  protected DatabaseSystemConnector(final DatabaseServerType dbServerType,
                                    final String configResource,
                                    final String informationSchemaViewsResourceFolder)
  {
    this.dbServerType = requireNonNull(dbServerType,
                                       "No database server type provided");
    this.configResource = configResource;
    this.informationSchemaViewsResourceFolder = informationSchemaViewsResourceFolder;
  }

  private DatabaseSystemConnector()
  {
    dbServerType = DatabaseServerType.UNKNOWN;
    configResource = null;
    informationSchemaViewsResourceFolder = null;
  }

  /**
   * Checks if the database connection options are valid, the JDBC driver class
   * can be loaded, and so on. Throws an exception if there is a problem.
   *
   * @throws SchemaCrawlerException
   *         If there is a problem with creating connection options.
   */
  public void checkDatabaseConnectionOptions()
    throws SchemaCrawlerException
  {
    final Config additionalConfig = new Config();
    additionalConfig.put("user", "fake");
    newDatabaseConnectionOptions(additionalConfig);
  }

  /**
   * Gets the complete bundled database configuration set. This is useful in
   * building the SchemaCrawler options.
   */
  public final Config getConfig()
  {
    final Config config = Config.loadResource(configResource);
    return config;
  }

  public DatabaseServerType getDatabaseServerType()
  {
    return dbServerType;
  }

  /**
   * Gets the complete bundled database specific configuration set, including
   * the SQL for information schema views.
   */
  public DatabaseSpecificOverrideOptionsBuilder
    getDatabaseSpecificOverrideOptionsBuilder()
  {
    final DatabaseSpecificOverrideOptionsBuilder databaseSpecificOverrideOptionsBuilder = new DatabaseSpecificOverrideOptionsBuilder();
    databaseSpecificOverrideOptionsBuilder.withInformationSchemaViews()
      .fromResourceFolder(informationSchemaViewsResourceFolder);

    return databaseSpecificOverrideOptionsBuilder;
  }

  /**
   * Creates a datasource for connecting to a database. Additional connection
   * options are provided, from the command-line, and configuration file.
   *
   * @param additionalConfig
   *        Configuration from the command-line, and from configuration files.
   */
  public ConnectionOptions
    newDatabaseConnectionOptions(final Config additionalConfig)
      throws SchemaCrawlerException
  {
    if (additionalConfig == null || additionalConfig.isEmpty())
    {
      throw new IllegalArgumentException("No connection configuration provided");
    }

    final Config config = getConfig();
    config.putAll(additionalConfig);

    final ConnectionOptions connectionOptions;
    if (dbServerType.isUnknownDatabaseSystem())
    {
      connectionOptions = new DatabaseConnectionOptions(config);
    }
    else
    {
      connectionOptions = new DatabaseConfigConnectionOptions(config);
    }

    return connectionOptions;
  }

  public Executable newExecutable(final String command)
    throws SchemaCrawlerException
  {
    return new SchemaCrawlerExecutable(command);
  }

  public Executable newPostExecutable()
    throws SchemaCrawlerException
  {
    return new NoOpExecutable("no-op");
  }

  public Executable newPreExecutable()
    throws SchemaCrawlerException
  {
    return new NoOpExecutable("no-op");
  }

}

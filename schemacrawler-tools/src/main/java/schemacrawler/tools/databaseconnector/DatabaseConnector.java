/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.util.regex.Pattern;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.DatabaseConfigConnectionOptions;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptionsBuilder;
import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;

public abstract class DatabaseConnector
  implements Options
{

  private static final long serialVersionUID = 6133330582637434099L;

  protected static final DatabaseConnector UNKNOWN = new DatabaseConnector()
  {

    private static final long serialVersionUID = 3057770737518232349L;

  };

  private final DatabaseServerType dbServerType;
  private final String connectionHelpResource;
  private final String configResource;
  private final String informationSchemaViewsResourceFolder;
  private final Pattern connectionUrlPattern;

  protected DatabaseConnector(final DatabaseServerType dbServerType,
                              final String connectionHelpResource,
                              final String configResource,
                              final String informationSchemaViewsResourceFolder,
                              final String connectionUrlPrefix)
  {
    this.dbServerType = requireNonNull(dbServerType,
                                       "No database server type provided");

    if (isBlank(connectionHelpResource))
    {
      throw new IllegalArgumentException("No connection help resource provided");
    }
    this.connectionHelpResource = connectionHelpResource;

    this.configResource = configResource;
    this.informationSchemaViewsResourceFolder = informationSchemaViewsResourceFolder;

    if (isBlank(connectionUrlPrefix))
    {
      throw new IllegalArgumentException("No JDBC connection URL prefix provided");
    }
    connectionUrlPattern = Pattern.compile(connectionUrlPrefix);
  }

  private DatabaseConnector()
  {
    dbServerType = DatabaseServerType.UNKNOWN;
    connectionHelpResource = null;
    configResource = null;
    informationSchemaViewsResourceFolder = null;
    connectionUrlPattern = null;
  }

  /**
   * Checks if the database connection options are valid, the JDBC
   * driver class can be loaded, and so on. Throws an exception if there
   * is a problem.
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
   * Gets the complete bundled database configuration set. This is
   * useful in building the SchemaCrawler options.
   */
  public final Config getConfig()
  {
    final Config config = Config.loadResource(configResource);
    return config;
  }

  public String getConnectionHelpResource()
  {
    return connectionHelpResource;
  }

  public final Pattern getConnectionUrlPattern()
  {
    return connectionUrlPattern;
  }

  public DatabaseServerType getDatabaseServerType()
  {
    return dbServerType;
  }

  /**
   * Gets the complete bundled database specific configuration set,
   * including the SQL for information schema views.
   */
  public DatabaseSpecificOverrideOptionsBuilder getDatabaseSpecificOverrideOptionsBuilder()
  {
    final DatabaseSpecificOverrideOptionsBuilder databaseSpecificOverrideOptionsBuilder = new DatabaseSpecificOverrideOptionsBuilder();
    databaseSpecificOverrideOptionsBuilder.withInformationSchemaViews()
      .fromResourceFolder(informationSchemaViewsResourceFolder);

    return databaseSpecificOverrideOptionsBuilder;
  }

  public boolean isUnknownDatabaseSystem()
  {
    return dbServerType.isUnknownDatabaseSystem();
  }

  /**
   * Creates a datasource for connecting to a database. Additional
   * connection options are provided, from the command-line, and
   * configuration file.
   *
   * @param additionalConfig
   *        Configuration from the command-line, and from configuration
   *        files.
   */
  public ConnectionOptions newDatabaseConnectionOptions(final Config additionalConfig)
    throws SchemaCrawlerException
  {
    if (additionalConfig == null || additionalConfig.isEmpty())
    {
      throw new IllegalArgumentException("No connection configuration provided");
    }

    final Config config = getConfig();
    config.putAll(additionalConfig);

    final ConnectionOptions connectionOptions;
    if (dbServerType.isUnknownDatabaseSystem() || config.hasValue("url"))
    {
      connectionOptions = new DatabaseConnectionOptions(config);
    }
    else
    {
      connectionOptions = new DatabaseConfigConnectionOptions(config);
    }

    // Remove sensitive properties from the original configuration
    additionalConfig.remove("user");
    additionalConfig.remove("password");

    return connectionOptions;
  }

  public Executable newExecutable(final String command)
    throws SchemaCrawlerException
  {
    return new SchemaCrawlerExecutable(command);
  }

}

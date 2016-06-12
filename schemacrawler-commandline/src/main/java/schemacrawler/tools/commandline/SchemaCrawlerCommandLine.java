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
package schemacrawler.tools.commandline;


import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerCommandLineException;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.options.OutputOptions;
import sf.util.StringFormat;

/**
 * Utility for parsing the SchemaCrawler command-line.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerCommandLine
  implements CommandLine
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerCommandLine.class.getName());

  private final String command;
  private final Config config;
  private final SchemaCrawlerOptions schemaCrawlerOptions;
  private final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions;
  private final OutputOptions outputOptions;
  private final ConnectionOptions connectionOptions;
  private final DatabaseConnector dbConnector;

  public SchemaCrawlerCommandLine(final Config argsMap)
    throws SchemaCrawlerException
  {
    requireNonNull(argsMap, "No command-line arguments provided");
    if (argsMap.isEmpty())
    {
      throw new SchemaCrawlerCommandLineException("No command-line arguments provided");
    }

    // Match the database connector in the best possible way, using the
    // server argument, or the JDBC connection URL
    final DatabaseServerTypeParser dbServerTypeParser = new DatabaseServerTypeParser(argsMap);
    dbConnector = dbServerTypeParser.getOptions();
    LOGGER.log(Level.INFO,
               new StringFormat("Using database plugin, %s",
                                dbConnector.getDatabaseServerType()));

    config = new Config();
    loadConfig(argsMap);

    final CommandParser commandParser = new CommandParser(config);
    command = commandParser.getOptions().toString();

    final SchemaCrawlerOptionsParser schemaCrawlerOptionsParser = new SchemaCrawlerOptionsParser(config);
    schemaCrawlerOptions = schemaCrawlerOptionsParser.getOptions();

    final OutputOptionsParser outputOptionsParser = new OutputOptionsParser(config);
    outputOptions = outputOptionsParser.getOptions();

    final AdditionalConfigOptionsParser additionalConfigOptionsParser = new AdditionalConfigOptionsParser(config);
    additionalConfigOptionsParser.loadConfig();

    parseConnectionOptions();
    // Connect using connection options provided from the command-line,
    // provided configuration, and bundled configuration
    connectionOptions = dbConnector.newDatabaseConnectionOptions(config);

    // Get partially built database specific options, built from the
    // classpath resources, and then override from config loaded in from
    // the command-line
    final DatabaseSpecificOverrideOptionsBuilder databaseSpecificOverrideOptionsBuilder = dbConnector
      .getDatabaseSpecificOverrideOptionsBuilder();
    databaseSpecificOverrideOptionsBuilder.fromConfig(config);
    databaseSpecificOverrideOptions = databaseSpecificOverrideOptionsBuilder
      .toOptions();
  }

  @Override
  public void execute()
    throws Exception
  {
    if (connectionOptions == null)
    {
      throw new SchemaCrawlerException("No connection options provided");
    }

    final Executable executable = dbConnector.newExecutable(command);
    // Configure
    executable.setOutputOptions(outputOptions);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(config);
    // Execute
    try (final Connection connection = connectionOptions.getConnection();)
    {
      executable.execute(connection, databaseSpecificOverrideOptions);
    }
  }

  public final String getCommand()
  {
    return command;
  }

  public final Config getConfig()
  {
    return config;
  }

  public final ConnectionOptions getConnectionOptions()
  {
    return connectionOptions;
  }

  public final OutputOptions getOutputOptions()
  {
    return outputOptions;
  }

  public final SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    return schemaCrawlerOptions;
  }

  /**
   * Loads configuration from a number of sources, in order of priority.
   */
  private void loadConfig(final Config argsMap)
    throws SchemaCrawlerException
  {
    // 1. Get bundled database config
    config.putAll(dbConnector.getConfig());

    // 2. Load config from files, in place
    config.putAll(argsMap);
    new ConfigParser(config).loadConfig();

    // 3. Override/ overwrite from the command-line options
    config.putAll(argsMap);

    new ConfigParser(config).consumeOptions();
  }

  /**
   * Parse connection options, for both ways of connecting.
   *
   * @param dbServerType
   *        Database server type
   */
  private void parseConnectionOptions()
    throws SchemaCrawlerException
  {
    final BaseDatabaseConnectionOptionsParser dbConnectionOptionsParser;
    if (dbConnector.isUnknownDatabaseSystem() || config.hasValue("url"))
    {
      dbConnectionOptionsParser = new CommandLineConnectionOptionsParser(config);
    }
    else
    {
      dbConnectionOptionsParser = new BundledDriverConnectionOptionsParser(config);
    }
    dbConnectionOptionsParser.loadConfig();
    config.putAll(dbConnectionOptionsParser.getOptions());
  }

}

/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
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
package schemacrawler.tools.commandline;


import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerCommandLineException;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.databaseconnector.DatabaseSystemConnector;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.DatabaseServerType;
import schemacrawler.tools.options.OutputOptions;
import sf.util.commandlineparser.CommandLineUtility;

/**
 * Utility for parsing the SchemaCrawler command-line.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerCommandLine
  implements CommandLine
{

  private final String command;
  private final Config config;
  private final SchemaCrawlerOptions schemaCrawlerOptions;
  private final OutputOptions outputOptions;
  private final ConnectionOptions connectionOptions;
  private final DatabaseSystemConnector dbSystemConnector;

  public SchemaCrawlerCommandLine(final DatabaseSystemConnector dbSystemConnector,
                                  final String... args)
    throws SchemaCrawlerException
  {
    if (args == null || args.length == 0)
    {
      throw new SchemaCrawlerCommandLineException("No command-line arguments provided");
    }
    requireNonNull(dbSystemConnector, "No database connector provided");

    this.dbSystemConnector = dbSystemConnector;

    config = new Config();
    loadConfig(dbSystemConnector, args);

    final CommandParser commandParser = new CommandParser(config);
    command = commandParser.getOptions().toString();

    final SchemaCrawlerOptionsParser schemaCrawlerOptionsParser = new SchemaCrawlerOptionsParser(config);
    schemaCrawlerOptions = schemaCrawlerOptionsParser.getOptions();

    final OutputOptionsParser outputOptionsParser = new OutputOptionsParser(config);
    outputOptions = outputOptionsParser.getOptions();

    final AdditionalConfigOptionsParser additionalConfigOptionsParser = new AdditionalConfigOptionsParser(config);
    additionalConfigOptionsParser.loadConfig();

    parseConnectionOptions(dbSystemConnector.getDatabaseServerType());
    // Connect using connection options provided from the command-line,
    // provided configuration, and bundled configuration
    connectionOptions = dbSystemConnector.newDatabaseConnectionOptions(config);

  }

  @Override
  public void execute()
    throws Exception
  {
    final List<Executable> executables = new ArrayList<>();

    Executable executableForList;

    executableForList = dbSystemConnector.newPreExecutable();
    initialize(executableForList);
    executables.add(executableForList);

    executableForList = new SchemaCrawlerExecutable(command);
    initialize(executableForList);
    executables.add(executableForList);

    executableForList = dbSystemConnector.newPostExecutable();
    initialize(executableForList);
    executables.add(executableForList);

    if (connectionOptions != null)
    {
      try (final Connection connection = connectionOptions.getConnection();)
      {
        for (final Executable executable: executables)
        {
          executable.execute(connection);
        }
      }
    }
    else
    {
      throw new SchemaCrawlerException("No connection options provided");
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

  private void initialize(final Executable executable)
  {
    if (outputOptions != null)
    {
      executable.setOutputOptions(outputOptions);
    }
    if (schemaCrawlerOptions != null)
    {
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    }
    if (config != null)
    {
      executable.setAdditionalConfiguration(config);
    }
  }

  /**
   * Loads configuration from a number of sources, in order of priority.
   */
  private void loadConfig(final DatabaseSystemConnector dbSystemConnector,
                          final String[] args)
    throws SchemaCrawlerException
  {
    final Config optionsMap = CommandLineUtility.loadConfig(args);

    // 1. Get bundled database config
    config.putAll(dbSystemConnector.getConfig());

    // 2. Load config from files, in place
    config.putAll(optionsMap);
    final ConfigParser configParser = new ConfigParser(config);
    configParser.loadConfig();

    // 3. Override/ overwrite from the command-line options
    config.putAll(optionsMap);
  }

  /**
   * Parse connection options, for both ways of connecting.
   *
   * @param dbServerType
   *        Database server type
   */
  private void parseConnectionOptions(final DatabaseServerType dbServerType)
    throws SchemaCrawlerException
  {
    final BaseDatabaseConnectionOptionsParser dbConnectionOptionsParser;
    if (dbServerType.isUnknownDatabaseSystem())
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

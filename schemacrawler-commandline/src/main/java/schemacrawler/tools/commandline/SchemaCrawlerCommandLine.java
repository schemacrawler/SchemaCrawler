/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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
import java.util.logging.Level;
import java.util.logging.Logger;

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
import sf.util.ObjectToString;

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

    String[] remainingArgs = args;
    this.dbSystemConnector = dbSystemConnector;

    final CommandParser commandParser = new CommandParser();
    remainingArgs = commandParser.parse(remainingArgs);
    command = commandParser.getOptions().toString();

    config = new Config();
    remainingArgs = loadConfig(dbSystemConnector, remainingArgs);

    final SchemaCrawlerOptionsParser schemaCrawlerOptionsParser = new SchemaCrawlerOptionsParser(config);
    remainingArgs = schemaCrawlerOptionsParser.parse(remainingArgs);
    schemaCrawlerOptions = schemaCrawlerOptionsParser.getOptions();

    final OutputOptionsParser outputOptionsParser = new OutputOptionsParser(config);
    remainingArgs = outputOptionsParser.parse(remainingArgs);
    outputOptions = outputOptionsParser.getOptions();

    remainingArgs = parseConnectionOptions(dbSystemConnector.getDatabaseServerType(),
                                           remainingArgs);
    // Connect using connection options provided from the command-line,
    // provided configuration, and bundled configuration
    connectionOptions = dbSystemConnector.newDatabaseConnectionOptions(config);

    if (remainingArgs.length > 0)
    {
      LOGGER.log(Level.INFO, "Too many command-line arguments provided: "
                             + ObjectToString.toString(remainingArgs));
    }
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
        LOGGER.log(Level.INFO, "Made connection, " + connection);
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
  private String[] loadConfig(final DatabaseSystemConnector dbSystemConnector,
                              final String[] args)
    throws SchemaCrawlerException
  {
    config.putAll(dbSystemConnector.getConfig());

    String[] remainingArgs = args;
    if (remainingArgs.length > 0)
    {
      final ConfigParser configParser = new ConfigParser(config);
      remainingArgs = configParser.parse(remainingArgs);
      config.putAll(configParser.getOptions());
    }

    if (remainingArgs.length > 0)
    {
      final AdditionalConfigParser additionalConfigParser = new AdditionalConfigParser(config);
      remainingArgs = additionalConfigParser.parse(remainingArgs);
      config.putAll(additionalConfigParser.getOptions());
    }

    return remainingArgs;
  }

  /**
   * Parse connection options, for both ways of connecting.
   * 
   * @param dbServerType
   *        Database server type
   */
  private String[] parseConnectionOptions(DatabaseServerType dbServerType,
                                          final String[] args)
    throws SchemaCrawlerException
  {
    String[] remainingArgs = args;

    final BaseDatabaseConnectionOptionsParser dbConnectionOptionsParser;
    if (dbServerType.isUnknownDatabaseSystem())
    {
      dbConnectionOptionsParser = new CommandLineConnectionOptionsParser(config);
    }
    else
    {
      dbConnectionOptionsParser = new BundledDriverConnectionOptionsParser(config);
    }
    remainingArgs = dbConnectionOptionsParser.parse(remainingArgs);
    config.putAll(dbConnectionOptionsParser.getOptions());

    return remainingArgs;
  }

}

/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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


import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import sf.util.ObjectToString;

/**
 * Utility for parsing the SchemaCrawler command line.
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

  /**
   * Loads objects from command line options. Optionally loads the
   * config from the classpath.
   * 
   * @param args
   *        Command line arguments.
   * @param config
   *        Configuration settings.
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public SchemaCrawlerCommandLine(final Config config, final String... args)
    throws SchemaCrawlerException
  {
    this(config, null, args);
  }

  public SchemaCrawlerCommandLine(final ConnectionOptions connectionOptions,
                                  final String... args)
    throws SchemaCrawlerException
  {
    this(null, connectionOptions, args);
  }

  private SchemaCrawlerCommandLine(final Config providedConfig,
                                   final ConnectionOptions connectionOptions,
                                   final String... args)
    throws SchemaCrawlerException
  {
    if (args == null || args.length == 0)
    {
      throw new SchemaCrawlerException("No command line arguments provided");
    }

    String[] remainingArgs = args;

    final CommandParser commandParser = new CommandParser();
    remainingArgs = commandParser.parse(remainingArgs);
    if (!commandParser.hasOptions())
    {
      throw new SchemaCrawlerException("No command specified");
    }
    command = commandParser.getOptions().toString();

    final OutputOptionsParser outputOptionsParser = new OutputOptionsParser();
    remainingArgs = outputOptionsParser.parse(remainingArgs);
    outputOptions = outputOptionsParser.getOptions();

    final boolean isBundledWithDriver = providedConfig != null;
    if (isBundledWithDriver)
    {
      config = providedConfig;
    }
    else
    {
      config = new Config();
    }
    if (remainingArgs.length > 0)
    {
      final ConfigParser configParser = new ConfigParser();
      remainingArgs = configParser.parse(remainingArgs);
      config.putAll(configParser.getOptions());
    }

    if (remainingArgs.length > 0)
    {
      final AdditionalConfigParser additionalConfigParser = new AdditionalConfigParser();
      remainingArgs = additionalConfigParser.parse(remainingArgs);
      config.putAll(additionalConfigParser.getOptions());
    }

    if (connectionOptions != null)
    {
      this.connectionOptions = connectionOptions;
    }
    else if (isBundledWithDriver)
    {
      final BaseDatabaseConnectionOptionsParser bundledDriverConnectionOptionsParser = new BundledDriverConnectionOptionsParser(config);
      remainingArgs = bundledDriverConnectionOptionsParser.parse(remainingArgs);
      this.connectionOptions = bundledDriverConnectionOptionsParser
        .getOptions();
    }
    else
    {
      final CommandLineConnectionOptionsParser commandLineConnectionOptionsParser = new CommandLineConnectionOptionsParser(config);
      remainingArgs = commandLineConnectionOptionsParser.parse(remainingArgs);
      this.connectionOptions = commandLineConnectionOptionsParser.getOptions();
    }

    final SchemaCrawlerOptionsParser schemaCrawlerOptionsParser = new SchemaCrawlerOptionsParser(config);
    remainingArgs = schemaCrawlerOptionsParser.parse(remainingArgs);
    schemaCrawlerOptions = schemaCrawlerOptionsParser.getOptions();

    if (remainingArgs.length > 0)
    {
      LOGGER.log(Level.INFO, "Too many command line arguments provided: "
                             + ObjectToString.toString(remainingArgs));
    }
  }

  @Override
  public void execute()
    throws Exception
  {
    final Executable executable = new SchemaCrawlerExecutable(command);
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
    if (connectionOptions != null)
    {
      Connection connection = null;
      try
      {
        connection = connectionOptions.getConnection();
        LOGGER.log(Level.INFO, "Made connection" + connection);
        executable.execute(connection);
      }
      finally
      {
        if (connection != null)
        {
          connection.close();
          LOGGER.log(Level.INFO, "Closed connection" + connection);
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

}

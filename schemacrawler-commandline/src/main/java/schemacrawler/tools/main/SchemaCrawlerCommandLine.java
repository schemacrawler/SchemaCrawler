/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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
package schemacrawler.tools.main;


import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.tools.Executable;
import schemacrawler.tools.OutputOptions;
import sf.util.Utility;

/**
 * Utility for parsing the SchemaCrawler command line.
 * 
 * @author Sualeh Fatehi
 */
public class SchemaCrawlerCommandLine
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerCommandLine.class.getName());

  private final Commands commands;
  private final Config config;
  private final SchemaCrawlerOptions schemaCrawlerOptions;
  private final OutputOptions outputOptions;
  private final ConnectionOptions connectionOptions;

  public SchemaCrawlerCommandLine(final ConnectionOptions connectionOptions,
                                  final SchemaInfoLevel infoLevel,
                                  final Commands commands,
                                  final Config config,
                                  final OutputOptions outputOptions)
  {
    this.connectionOptions = connectionOptions;
    this.commands = commands;
    this.config = config;
    this.outputOptions = outputOptions;

    schemaCrawlerOptions = new SchemaCrawlerOptions();
    if (infoLevel != null)
    {
      schemaCrawlerOptions.setSchemaInfoLevel(infoLevel);
    }
  }

  /**
   * Loads objects from command line options.
   * 
   * @param args
   *        Command line arguments.
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public SchemaCrawlerCommandLine(final String[] args,
                                  final HelpOptions helpOptions)
    throws SchemaCrawlerException
  {
    this(args, helpOptions, null);
  }

  /**
   * Loads objects from command line options. Optionally loads the
   * config from the classpath.
   * 
   * @param args
   *        Command line arguments.
   * @param configResource
   *        Config resource.
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public SchemaCrawlerCommandLine(final String[] args,
                                  final HelpOptions helpOptions,
                                  final String configResource)
    throws SchemaCrawlerException
  {
    if (args == null || args.length == 0)
    {
      helpOptions.showHelp();
      System.exit(0);
    }

    final ApplicationOptions applicationOptions;
    if (args.length > 0)
    {
      applicationOptions = new ApplicationOptionsParser(args).getOptions();
      if (applicationOptions.isShowHelp())
      {
        helpOptions.showHelp();
        System.exit(0);
      }

      commands = new CommandsParser(args).getOptions();
      outputOptions = new OutputOptionsParser(args).getOptions();
    }
    else
    {
      applicationOptions = new ApplicationOptions();
      commands = new Commands();
      outputOptions = new OutputOptions();
    }

    applicationOptions.applyApplicationLogLevel();
    LOGGER.log(Level.INFO, HelpOptions.about());
    LOGGER.log(Level.CONFIG, "Command line: " + Arrays.toString(args));

    if (!Utility.isBlank(configResource))
    {
      config = Config.load(SchemaCrawlerCommandLine.class
        .getResourceAsStream(configResource));
      connectionOptions = new BundledDriverConnectionOptionsParser(args, config)
        .getOptions();
    }
    else
    {
      if (args != null && args.length > 0)
      {
        config = new ConfigParser(args).getOptions();
      }
      else
      {
        config = new Config();
      }

      ConnectionOptions connectionOptions = new CommandLineConnectionOptionsParser(args,
                                                                                   config)
        .getOptions();
      if (connectionOptions == null)
      {
        connectionOptions = new ConfigConnectionOptionsParser(args, config)
          .getOptions();
      }
      this.connectionOptions = connectionOptions;
    }

    schemaCrawlerOptions = new SchemaCrawlerOptionsParser(args, config)
      .getOptions();
  }

  /**
   * Executes with the command line, and a given executor. The executor
   * allows for the command line to be parsed independently of the
   * execution. The execution can integrate with other software, such as
   * Velocity.
   * 
   * @throws Exception
   *         On an exception
   */
  public void execute()
    throws Exception
  {
    final List<Executable<?>> executables = ExecutableFactory
      .createExecutables(this);

    Connection connection = null;
    try
    {
      connection = connectionOptions.createConnection();
      for (final Executable<?> executable: executables)
      {
        executable.execute(connection);
      }
    }
    finally
    {
      if (connection != null)
      {
        connection.close();
        LOGGER.log(Level.INFO, "Closed database connection, " + connection);
      }
    }
  }

  /**
   * Gets the commands.
   * 
   * @return Commands.
   */
  Commands getCommands()
  {
    return commands;
  }

  /**
   * Gets the config.
   * 
   * @return Config.
   */
  Config getConfig()
  {
    return new Config(config);
  }

  /**
   * Gets the output options.
   * 
   * @return Output options.
   */
  OutputOptions getOutputOptions()
  {
    return outputOptions.duplicate();
  }

  SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    return schemaCrawlerOptions;
  }

}

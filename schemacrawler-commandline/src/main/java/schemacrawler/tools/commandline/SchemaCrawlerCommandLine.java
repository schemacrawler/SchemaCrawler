/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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
import sf.util.Utility;

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

  public SchemaCrawlerCommandLine(final ConnectionOptions connectionOptions,
                                  final String... args)
    throws SchemaCrawlerException
  {
    this(null, connectionOptions, args);
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
  public SchemaCrawlerCommandLine(final String configResource,
                                  final String... args)
    throws SchemaCrawlerException
  {
    this(configResource, null, args);
  }

  private SchemaCrawlerCommandLine(final String configResource,
                                   final ConnectionOptions connectionOptions,
                                   final String... args)
    throws SchemaCrawlerException
  {
    if (args == null || args.length == 0)
    {
      throw new SchemaCrawlerException("No command line arguments provided");
    }

    String[] remainingArgs = args;

    final CommandParser commandParser = new CommandParser(remainingArgs);
    command = commandParser.getOptions().toString();
    remainingArgs = commandParser.getUnparsedArgs();

    final OutputOptionsParser outputOptionsParser = new OutputOptionsParser(remainingArgs);
    outputOptions = outputOptionsParser.getOptions();
    remainingArgs = outputOptionsParser.getUnparsedArgs();

    final boolean hasConfigResource = !Utility.isBlank(configResource);
    if (hasConfigResource)
    {
      config = Config.load(SchemaCrawlerCommandLine.class
        .getResourceAsStream(configResource));
    }
    else
    {
      if (remainingArgs.length > 0)
      {
        final ConfigParser configParser = new ConfigParser(remainingArgs);
        config = configParser.getOptions();
        remainingArgs = configParser.getUnparsedArgs();
      }
      else
      {
        config = new Config();
      }
    }

    if (connectionOptions != null)
    {
      this.connectionOptions = connectionOptions;
    }
    else if (hasConfigResource)
    {
      final BundledDriverConnectionOptionsParser bundledDriverConnectionOptionsParser = new BundledDriverConnectionOptionsParser(remainingArgs,
                                                                                                                                 config);
      this.connectionOptions = bundledDriverConnectionOptionsParser
        .getOptions();
      remainingArgs = bundledDriverConnectionOptionsParser.getUnparsedArgs();
    }
    else
    {
      final CommandLineConnectionOptionsParser commandLineConnectionOptionsParser = new CommandLineConnectionOptionsParser(remainingArgs,
                                                                                                                           config);
      ConnectionOptions parsedConnectionOptions = commandLineConnectionOptionsParser
        .getOptions();
      remainingArgs = commandLineConnectionOptionsParser.getUnparsedArgs();
      if (parsedConnectionOptions == null)
      {
        final ConfigConnectionOptionsParser configConnectionOptionsParser = new ConfigConnectionOptionsParser(remainingArgs,
                                                                                                              config);
        parsedConnectionOptions = configConnectionOptionsParser.getOptions();
        remainingArgs = configConnectionOptionsParser.getUnparsedArgs();
      }
      this.connectionOptions = parsedConnectionOptions;
    }

    final SchemaCrawlerOptionsParser schemaCrawlerOptionsParser = new SchemaCrawlerOptionsParser(remainingArgs,
                                                                                                 config);
    schemaCrawlerOptions = schemaCrawlerOptionsParser.getOptions();
    remainingArgs = schemaCrawlerOptionsParser.getUnparsedArgs();

    if (remainingArgs.length > 0)
    {
      LOGGER.log(Level.INFO, "Too many command line arguments provided: "
                             + ObjectToString.toString(remainingArgs));
    }
  }

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
      executable.execute(connectionOptions.createConnection());
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

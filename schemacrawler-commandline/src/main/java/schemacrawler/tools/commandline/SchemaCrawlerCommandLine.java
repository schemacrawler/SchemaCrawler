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
package schemacrawler.tools.commandline;


import java.util.logging.Logger;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import sf.util.Utility;

/**
 * Utility for parsing the SchemaCrawler command line.
 * 
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerCommandLine
  implements CommandLine
{

  private static final long serialVersionUID = -3748989545708155963L;

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerCommandLine.class.getName());

  private final String command;
  private final SchemaCrawlerOptions schemaCrawlerOptions;
  private final OutputOptions outputOptions;
  private final ConnectionOptions connectionOptions;

  public SchemaCrawlerCommandLine(final ConnectionOptions connectionOptions,
                                  final SchemaInfoLevel infoLevel,
                                  final String command,
                                  final Config config,
                                  final OutputOptions outputOptions)
  {
    this.connectionOptions = connectionOptions;
    this.command = command;
    this.outputOptions = outputOptions;

    schemaCrawlerOptions = new SchemaCrawlerOptions(config);
    if (infoLevel != null)
    {
      schemaCrawlerOptions.setSchemaInfoLevel(infoLevel);
    }
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
                                  final String configResource)
    throws SchemaCrawlerException
  {
    if (args == null)
    {
      throw new IllegalArgumentException("No command line arguments provided");
    }

    if (args.length > 0)
    {
      command = new CommandParser(args).getOptions().toString();
      outputOptions = new OutputOptionsParser(args).getOptions();
    }
    else
    {
      command = null;
      outputOptions = new OutputOptions();
    }

    final Config config;
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

  public void execute()
    throws Exception
  {
    final Executable executable = new SchemaCrawlerExecutable(command);
    if (connectionOptions != null)
    {
      executable.setConnectionOptions(connectionOptions);
    }
    if (outputOptions != null)
    {
      executable.setOutputOptions(outputOptions);
    }
    if (schemaCrawlerOptions != null)
    {
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    }
    executable.execute();
  }

  public final String getCommand()
  {
    return command;
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

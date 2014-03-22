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


import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.BundledDriverOptions;
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
  private final BundledDriverOptions bundledDriverOptions;

  public SchemaCrawlerCommandLine(final BundledDriverOptions bundledDriverOptions,
                                  final String... args)
    throws SchemaCrawlerException
  {
    this(bundledDriverOptions, null, args);
  }

  public SchemaCrawlerCommandLine(final ConnectionOptions connectionOptions,
                                  final String... args)
    throws SchemaCrawlerException
  {
    this(null, connectionOptions, args);
  }

  private SchemaCrawlerCommandLine(final BundledDriverOptions bundledDriverOptions,
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

    if (bundledDriverOptions != null)
    {
      this.bundledDriverOptions = bundledDriverOptions;
    }
    else
    {
      this.bundledDriverOptions = new BundledDriverOptions()
      {

        private static final long serialVersionUID = -8917733124364175122L;
      };
    }

    config = this.bundledDriverOptions.getConfig();

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
    else if (this.bundledDriverOptions.hasConfig())
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

    final OutputOptionsParser outputOptionsParser = new OutputOptionsParser(config);
    remainingArgs = outputOptionsParser.parse(remainingArgs);
    outputOptions = outputOptionsParser.getOptions();

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
    final List<Executable> executables = new ArrayList<>();

    Executable executableForList;

    executableForList = bundledDriverOptions.newPreExecutable();
    initialize(executableForList);
    executables.add(executableForList);

    executableForList = new SchemaCrawlerExecutable(command);
    initialize(executableForList);
    executables.add(executableForList);

    executableForList = bundledDriverOptions.newPostExecutable();
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

}

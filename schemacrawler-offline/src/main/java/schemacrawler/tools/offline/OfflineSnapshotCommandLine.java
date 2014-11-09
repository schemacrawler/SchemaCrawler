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
package schemacrawler.tools.offline;


import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.commandline.AdditionalConfigParser;
import schemacrawler.tools.commandline.CommandLine;
import schemacrawler.tools.commandline.CommandParser;
import schemacrawler.tools.commandline.ConfigParser;
import schemacrawler.tools.commandline.OutputOptionsParser;
import schemacrawler.tools.commandline.SchemaCrawlerOptionsParser;
import schemacrawler.tools.options.OutputOptions;
import sf.util.ObjectToString;

/**
 * Utility for parsing the SchemaCrawler command-line.
 *
 * @author Sualeh Fatehi
 */
public final class OfflineSnapshotCommandLine
  implements CommandLine
{

  private static final Logger LOGGER = Logger
    .getLogger(OfflineSnapshotCommandLine.class.getName());

  private final String command;
  private final Config config;
  private final SchemaCrawlerOptions schemaCrawlerOptions;
  private final OutputOptions outputOptions;
  private final OfflineSnapshotOptions offlineSnapshotOptions;

  OfflineSnapshotCommandLine(final String... args)
    throws SchemaCrawlerException
  {
    if (args == null || args.length == 0)
    {
      throw new SchemaCrawlerException("No command-line arguments provided");
    }

    String[] remainingArgs = args;

    final CommandParser commandParser = new CommandParser();
    remainingArgs = commandParser.parse(remainingArgs);
    if (!commandParser.hasOptions())
    {
      throw new SchemaCrawlerException("No command specified");
    }
    command = commandParser.getOptions().toString();

    config = new Config();

    if (remainingArgs.length > 0)
    {
      final ConfigParser configParser = new ConfigParser();
      remainingArgs = configParser.parse(remainingArgs);
      config.putAll(configParser.getOptions());
    }

    if (remainingArgs.length > 0)
    {
      final AdditionalConfigParser additionalConfigParser = new AdditionalConfigParser(config);
      remainingArgs = additionalConfigParser.parse(remainingArgs);
      config.putAll(additionalConfigParser.getOptions());
    }

    final OfflineSnapshotOptionsParser offlineSnapshotOptionsParser = new OfflineSnapshotOptionsParser(config);
    remainingArgs = offlineSnapshotOptionsParser.parse(remainingArgs);
    offlineSnapshotOptions = offlineSnapshotOptionsParser.getOptions();

    final SchemaCrawlerOptionsParser schemaCrawlerOptionsParser = new SchemaCrawlerOptionsParser(config);
    remainingArgs = schemaCrawlerOptionsParser.parse(remainingArgs);
    schemaCrawlerOptions = schemaCrawlerOptionsParser.getOptions();

    final OutputOptionsParser outputOptionsParser = new OutputOptionsParser(config);
    remainingArgs = outputOptionsParser.parse(remainingArgs);
    outputOptions = outputOptionsParser.getOptions();

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
    final OfflineSnapshotExecutable executable = new OfflineSnapshotExecutable(command);
    initialize(executable);

    executable.execute(null);
  }

  public final String getCommand()
  {
    return command;
  }

  public final Config getConfig()
  {
    return config;
  }

  public final OutputOptions getOutputOptions()
  {
    return outputOptions;
  }

  public final SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    return schemaCrawlerOptions;
  }

  private void initialize(final OfflineSnapshotExecutable executable)
  {
    if (outputOptions != null)
    {
      executable.setOutputOptions(outputOptions);
    }
    if (schemaCrawlerOptions != null)
    {
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    }
    if (offlineSnapshotOptions != null)
    {
      executable.setOfflineSnapshotOptions(offlineSnapshotOptions);
    }
    if (config != null)
    {
      executable.setAdditionalConfiguration(config);
    }
  }

}

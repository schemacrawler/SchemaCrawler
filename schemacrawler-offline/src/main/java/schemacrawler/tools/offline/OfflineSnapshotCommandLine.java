/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
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
package schemacrawler.tools.offline;


import static java.util.Objects.requireNonNull;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.commandline.AdditionalConfigOptionsParser;
import schemacrawler.tools.commandline.CommandLine;
import schemacrawler.tools.commandline.CommandParser;
import schemacrawler.tools.commandline.ConfigParser;
import schemacrawler.tools.commandline.OutputOptionsParser;
import schemacrawler.tools.commandline.SchemaCrawlerOptionsParser;
import schemacrawler.tools.options.OutputOptions;
import sf.util.commandlineparser.CommandLineUtility;

/**
 * Utility for parsing the SchemaCrawler command-line.
 *
 * @author Sualeh Fatehi
 */
public final class OfflineSnapshotCommandLine
  implements CommandLine
{

  private final String command;
  private final Config config;
  private final SchemaCrawlerOptions schemaCrawlerOptions;
  private final OutputOptions outputOptions;
  private final OutputOptions inputOptions;

  OfflineSnapshotCommandLine(final String... args)
    throws SchemaCrawlerException
  {
    requireNonNull(args);

    config = new Config();
    loadConfig(args);

    final CommandParser commandParser = new CommandParser(config);
    command = commandParser.getOptions().toString();

    final OfflineSnapshotOptionsParser offlineSnapshotOptionsParser = new OfflineSnapshotOptionsParser(config);
    inputOptions = offlineSnapshotOptionsParser.getOptions();

    final SchemaCrawlerOptionsParser schemaCrawlerOptionsParser = new SchemaCrawlerOptionsParser(config);
    schemaCrawlerOptions = schemaCrawlerOptionsParser.getOptions();

    final OutputOptionsParser outputOptionsParser = new OutputOptionsParser(config);
    outputOptions = outputOptionsParser.getOptions();

    final AdditionalConfigOptionsParser additionalConfigOptionsParser = new AdditionalConfigOptionsParser(config);
    additionalConfigOptionsParser.loadConfig();
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
    if (inputOptions != null)
    {
      executable.setInputOptions(inputOptions);
    }
    if (config != null)
    {
      executable.setAdditionalConfiguration(config);
    }
  }

  /**
   * Loads configuration from a number of sources, in order of priority.
   */
  private void loadConfig(final String[] args)
    throws SchemaCrawlerException
  {
    final Config optionsMap = CommandLineUtility.loadConfig(args);

    // 1. Load config from files, in place
    config.putAll(optionsMap);
    final ConfigParser configParser = new ConfigParser(config);
    configParser.loadConfig();

    // 2. Override/ overwrite from the command-line options
    config.putAll(optionsMap);
  }

}

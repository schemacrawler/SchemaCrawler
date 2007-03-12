/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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

package schemacrawler.main;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.tools.OutputFormat;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.operation.Operation;
import schemacrawler.tools.schematext.SchemaTextDetailType;
import sf.util.CommandLineParser;
import sf.util.Utilities;

/**
 * Parses the command line.
 * 
 * @author sfatehi
 */
public final class OptionsParser
{

  private static final String OPTION_CONFIGFILE = "configfile";
  private static final String OPTION_CONFIGOVERRIDEFILE = "configoverridefile";
  private static final String OPTION_NOINFO = "noinfo";
  private static final String OPTION_NOFOOTER = "nofooter";
  private static final String OPTION_NOHEADER = "noheader";
  private static final String OPTION_COMMAND = "command";
  private static final String OPTION_OUTPUT_FORMAT = "outputformat";
  private static final String OPTION_OUTPUT_FILE = "outputfile";
  private static final String OPTION_OUTPUT_APPEND = "append";

  /**
   * Parses the command line.
   * 
   * @param args
   *        Command line arguments
   * @return Command line options
   * @throws SchemaCrawlerException
   */
  static Options[] parseCommandLine(final String[] args)
    throws SchemaCrawlerException
  {

    final CommandLineParser parser = createCommandLineParser();
    parser.parse(args);

    final String cfgFile = parser.getStringOptionValue(OPTION_CONFIGFILE);
    final String cfgOverrideFile = parser
      .getStringOptionValue(OPTION_CONFIGOVERRIDEFILE);
    final Properties config = Utilities.loadConfig(cfgFile, cfgOverrideFile);

    final String outputFormatValue = parser
      .getStringOptionValue(OPTION_OUTPUT_FORMAT);

    final String outputFile = parser.getStringOptionValue(OPTION_OUTPUT_FILE);

    final boolean appendOutput = parser
      .getBooleanOptionValue(OPTION_OUTPUT_APPEND);

    final boolean noHeader = parser.getBooleanOptionValue(OPTION_NOHEADER);
    final boolean noFooter = parser.getBooleanOptionValue(OPTION_NOFOOTER);
    final boolean noInfo = parser.getBooleanOptionValue(OPTION_NOINFO);

    final OutputOptions masterOutputOptions = new OutputOptions(outputFormatValue,
                                                                outputFile);
    masterOutputOptions.setAppendOutput(appendOutput);
    masterOutputOptions.setNoHeader(noHeader);
    masterOutputOptions.setNoFooter(noFooter);
    masterOutputOptions.setNoInfo(noInfo);

    final String commandOptionValue = parser
      .getStringOptionValue(OPTION_COMMAND);
    if (Utilities.isBlank(commandOptionValue))
    {
      throw new SchemaCrawlerException("No SchemaCrawler command specified");
    }
    final String[] commandStrings = commandOptionValue.split(",");
    final Options[] optionCommands = createOptionsPerCommand(commandStrings,
                                                             config,
                                                             masterOutputOptions);

    return optionCommands;

  }

  private static CommandLineParser createCommandLineParser()
  {
    final CommandLineParser parser = new CommandLineParser();
    parser
      .addOption(new CommandLineParser.StringOption('g',
                                                    OPTION_CONFIGFILE,
                                                    "schemacrawler.config.properties"));
    parser
      .addOption(new CommandLineParser.StringOption('p',
                                                    OPTION_CONFIGOVERRIDEFILE,
                                                    "schemacrawler.config.override.properties"));
    parser
      .addOption(new CommandLineParser.StringOption(CommandLineParser.Option.NO_SHORT_FORM,
                                                    OPTION_COMMAND,
                                                    ""));
    parser
      .addOption(new CommandLineParser.StringOption(CommandLineParser.Option.NO_SHORT_FORM,
                                                    OPTION_OUTPUT_FORMAT,
                                                    OutputFormat.TEXT
                                                      .toString()));
    parser
      .addOption(new CommandLineParser.StringOption(CommandLineParser.Option.NO_SHORT_FORM,
                                                    OPTION_OUTPUT_FILE,
                                                    ""));
    parser
      .addOption(new CommandLineParser.BooleanOption(CommandLineParser.Option.NO_SHORT_FORM,
                                                     OPTION_OUTPUT_APPEND));
    parser
      .addOption(new CommandLineParser.BooleanOption(CommandLineParser.Option.NO_SHORT_FORM,
                                                     OPTION_NOHEADER));
    parser
      .addOption(new CommandLineParser.BooleanOption(CommandLineParser.Option.NO_SHORT_FORM,
                                                     OPTION_NOFOOTER));
    parser
      .addOption(new CommandLineParser.BooleanOption(CommandLineParser.Option.NO_SHORT_FORM,
                                                     OPTION_NOINFO));
    return parser;
  }

  private static Options[] createOptionsPerCommand(final String[] commandStrings,
                                                   final Properties config,
                                                   final OutputOptions masterOutputOptions)
  {
    final List<Options> optionCommandsList = new ArrayList<Options>();
    for (int i = 0; i < commandStrings.length; i++)
    {
      final Command command = parseCommand(config, commandStrings[i]);
      //
      final OutputOptions outputOptions = masterOutputOptions.duplicate();
      if (i == 0)
      {
        // First command - no footer
        outputOptions.setNoFooter(true);
      }
      else if (i == commandStrings.length - 1)
      {
        // Last command - no header, or info
        outputOptions.setNoHeader(true);
        outputOptions.setNoInfo(true);

        outputOptions.setAppendOutput(true);
      }
      else
      {
        // Middle command - no header, footer, or info
        outputOptions.setNoHeader(true);
        outputOptions.setNoInfo(true);
        outputOptions.setNoFooter(true);

        outputOptions.setAppendOutput(true);
      }
      //
      final Options options = new Options(config, command, outputOptions);
      //
      optionCommandsList.add(options);
    }
    final Options[] optionCommands = optionCommandsList
      .toArray(new Options[optionCommandsList.size()]);

    return optionCommands;
  }

  private static boolean isQueryOver(final String query)
  {
    boolean isQueryOver = false;
    final Set<String> keys = Utilities.extractTemplateVariables(query);
    final String[] queryOverKeys = {
        "table", "table_type"
    };
    for (final String element: queryOverKeys)
    {
      if (keys.contains(element))
      {
        isQueryOver = true;
        break;
      }
    }
    return isQueryOver;
  }

  private static Command parseCommand(final Properties config,
                                      final String commandString)
  {
    final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType
      .valueOf(commandString);
    Operation operation = Operation.valueOf(commandString);
    String query = "";
    if (schemaTextDetailType == null && operation == null)
    {
      // Assume that the command is a query
      query = config.getProperty(commandString);
      if (query == null)
      {
        throw new IllegalArgumentException("Invalid command - " + commandString);
      }
      if (isQueryOver(query))
      {
        operation = Operation.queryOverOperation();
      }
    }

    final Command command = Command.createCommand(schemaTextDetailType,
                                                  operation,
                                                  query);
    return command;
  }

  private OptionsParser()
  {

  }

}

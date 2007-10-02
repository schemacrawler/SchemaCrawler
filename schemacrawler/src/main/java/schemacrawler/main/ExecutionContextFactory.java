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

import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.tools.Command;
import schemacrawler.tools.ExecutionContext;
import schemacrawler.tools.OutputFormat;
import schemacrawler.tools.OutputOptions;
import sf.util.CommandLineParser;
import sf.util.Config;
import sf.util.Utilities;
import sf.util.CommandLineParser.BooleanOption;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;

/**
 * Parses the command line.
 * 
 * @author Sualeh Fatehi
 */
public final class ExecutionContextFactory
{

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
   * @param config
   *        Configuration
   * @return Command line options
   * @throws SchemaCrawlerException
   */
  static ExecutionContext[] createExecutionContexts(final String[] args,
                                             final Config config)
    throws SchemaCrawlerException
  {

    final CommandLineParser parser = createCommandLineParser();
    parser.parse(args);

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
    final ExecutionContext[] optionCommands = createExecutionContextsPerCommand(commandStrings,
                                                                                config,
                                                                                masterOutputOptions);

    return optionCommands;

  }

  private static CommandLineParser createCommandLineParser()
  {
    final CommandLineParser parser = new CommandLineParser();
    parser
      .addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_COMMAND, ""));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM,
                                      OPTION_OUTPUT_FORMAT,
                                      OutputFormat.text.toString()));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM,
                                      OPTION_OUTPUT_FILE,
                                      ""));
    parser.addOption(new BooleanOption(Option.NO_SHORT_FORM,
                                       OPTION_OUTPUT_APPEND));
    parser.addOption(new BooleanOption(Option.NO_SHORT_FORM, OPTION_NOHEADER));
    parser.addOption(new BooleanOption(Option.NO_SHORT_FORM, OPTION_NOFOOTER));
    parser.addOption(new BooleanOption(Option.NO_SHORT_FORM, OPTION_NOINFO));
    return parser;
  }

  private static ExecutionContext[] createExecutionContextsPerCommand(final String[] commandStrings,
                                                                      final Config config,
                                                                      final OutputOptions masterOutputOptions)
  {
    final List<ExecutionContext> executionContextsList = new ArrayList<ExecutionContext>();
    for (int i = 0; i < commandStrings.length; i++)
    {
      String commandString = commandStrings[i];
      if (commandString == null || commandString.length() == 0)
      {
        continue;
      }
      commandString = commandString.trim().toLowerCase();
      final Command command = new Command(commandString);
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
      final ExecutionContext executionContext = new ExecutionContext(command,
                                                                     config,
                                                                     outputOptions);
      //
      executionContextsList.add(executionContext);
    }
    final ExecutionContext[] executionContexts = executionContextsList
      .toArray(new ExecutionContext[executionContextsList.size()]);

    return executionContexts;
  }

  private ExecutionContextFactory()
  {

  }

}

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

import schemacrawler.crawl.Query;
import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.tools.Command;
import schemacrawler.tools.Executable;
import schemacrawler.tools.OutputFormat;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.datatext.DataTextFormatOptions;
import schemacrawler.tools.datatext.DataToolsExecutable;
import schemacrawler.tools.operation.Operation;
import schemacrawler.tools.operation.OperationExecutable;
import schemacrawler.tools.operation.OperationOptions;
import schemacrawler.tools.schematext.SchemaCrawlerExecutable;
import schemacrawler.tools.schematext.SchemaTextDetailType;
import schemacrawler.tools.schematext.SchemaTextOptions;
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
public final class ExecutableFactory
{
  /**
   * An enumeration of available tools.
   */
  private enum ToolType
  {
    /** Schema metadata to text. */
    schema_text,
    /** Operation. */
    operation,
    /** Data to text. */
    data_text;
  }

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
  static List<Executable<?>> createExecutables(final String[] args,
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
    final List<Executable<?>> executables = createExecutablesPerCommand(commandStrings,
                                                                        config,
                                                                        masterOutputOptions);

    return executables;

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

  private static List<Executable<?>> createExecutablesPerCommand(final String[] commandStrings,
                                                                 final Config config,
                                                                 final OutputOptions masterOutputOptions)
  {
    final List<Executable<?>> executables = new ArrayList<Executable<?>>();
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

      final Executable<?> executable;

      final ToolType toolType = determineToolType(command, config);
      switch (toolType)
      {
        case schema_text:
          final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType
            .valueOf(command.getName());
          final SchemaTextOptions schemaTextOptions = new SchemaTextOptions(config,
                                                                            outputOptions,
                                                                            schemaTextDetailType);
          final SchemaCrawlerExecutable schemaCrawlerExecutable = new SchemaCrawlerExecutable();
          schemaCrawlerExecutable.setToolOptions(schemaTextOptions);
          executable = schemaCrawlerExecutable;
          break;
        case operation:
          Operation operation;
          OperationOptions operationOptions;
          try
          {
            operation = Operation.valueOf(command.getName());
            operationOptions = new OperationOptions(config,
                                                    outputOptions,
                                                    operation);
          }
          catch (final IllegalArgumentException e)
          {
            final String queryName = command.getName();
            operationOptions = new OperationOptions(config,
                                                    outputOptions,
                                                    queryName);
          }
          final OperationExecutable operationExecutable = new OperationExecutable();
          operationExecutable.setToolOptions(operationOptions);
          executable = operationExecutable;
          break;
        case data_text:
          final String queryName = command.getName();
          final DataTextFormatOptions dataTextFormatOptions = new DataTextFormatOptions(config,
                                                                                        outputOptions,
                                                                                        queryName);
          final DataToolsExecutable dataToolsExecutable = new DataToolsExecutable();
          dataToolsExecutable.setToolOptions(dataTextFormatOptions);
          executable = dataToolsExecutable;
          break;
        default:
          throw new IllegalArgumentException("Could not find the tool type");
      }
      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(config);
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);

      executables.add(executable);
    }

    return executables;
  }

  private static ToolType determineToolType(final Command command,
                                            final Config config)
  {
    ToolType toolType;
    if (!command.isQuery())
    {
      toolType = ToolType.schema_text;
    }
    else
    {
      Operation operation;
      try
      {
        operation = Operation.valueOf(command.getName());
      }
      catch (final IllegalArgumentException e)
      {
        operation = null;
      }
      if (operation == null)
      {
        final Query query = new Query(command.getName(), config.get(command
          .getName()));
        if (query.isQueryOver())
        {
          toolType = ToolType.operation;
        }
        else
        {
          toolType = ToolType.data_text;
        }
      }
      else
      {
        toolType = ToolType.operation;
      }
    }
    return toolType;
  }

  private ExecutableFactory()
  {

  }

}

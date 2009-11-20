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

package schemacrawler.main;


import java.util.ArrayList;
import java.util.List;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.Executable;
import schemacrawler.tools.operation.Operation;
import schemacrawler.tools.operation.OperationExecutable;
import schemacrawler.tools.operation.OperationOptions;
import schemacrawler.tools.options.Command;
import schemacrawler.tools.options.Commands;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.schematext.SchemaCrawlerExecutable;
import schemacrawler.tools.schematext.SchemaTextDetailType;
import schemacrawler.tools.schematext.SchemaTextOptions;

/**
 * Parses the command line.
 * 
 * @author Sualeh Fatehi
 */
final class ExecutableFactory
{
  /**
   * Parses the command line.
   * 
   * @param args
   *        Command line arguments
   * @return Command line options
   * @throws SchemaCrawlerException
   */
  static List<Executable<?>> createExecutables(final SchemaCrawlerCommandLine commandLine)
    throws SchemaCrawlerException
  {
    final Config config = commandLine.getConfig();
    final OutputOptions masterOutputOptions = commandLine.getOutputOptions();
    final Commands commands = commandLine.getCommands();
    final List<Executable<?>> executables = new ArrayList<Executable<?>>();
    for (final Command command: commands)
    {
      final OutputOptions outputOptions = masterOutputOptions.duplicate();
      if (commands.isFirstCommand(command))
      {
        // First command - no footer
        outputOptions.setNoFooter(true);
      }
      else if (commands.isLastCommand(command))
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

      if (!command.isOperation())
      {
        final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType
          .valueOf(command.getName());
        final SchemaTextOptions schemaTextOptions = new SchemaTextOptions(config,
                                                                          outputOptions,
                                                                          schemaTextDetailType);
        final SchemaCrawlerExecutable schemaCrawlerExecutable = new SchemaCrawlerExecutable(command
          .getName());
        schemaCrawlerExecutable.setToolOptions(schemaTextOptions);
        executable = schemaCrawlerExecutable;
      }
      else
      {
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
        final OperationExecutable operationExecutable = new OperationExecutable(command
          .getName());
        operationExecutable.setToolOptions(operationOptions);
        executable = operationExecutable;
      }
      executable.setSchemaCrawlerOptions(commandLine.getSchemaCrawlerOptions());
      executables.add(executable);
    }

    return executables;
  }

  private ExecutableFactory()
  {

  }

}

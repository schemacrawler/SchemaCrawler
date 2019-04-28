/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.commandline.command;


import static picocli.CommandLine.Model.UsageMessageSpec.*;

import java.io.PrintStream;
import java.util.Arrays;

import picocli.CommandLine;
import schemacrawler.tools.commandline.shell.SchemaCrawlerShellCommands;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.commandline.state.StateFactory;

@CommandLine.Command(name = "help",
                     header = "Displays SchemaCrawler command-line help",
                     helpCommand = true)
public final class CommandLineHelpCommand
  implements Runnable
{

  private CommandLine.Help.Ansi ansi;
  @CommandLine.Parameters
  private String[] commands;
  private PrintStream err;
  @CommandLine.Option(names = { "-h", "--help" },
                      usageHelp = true,
                      description = "Displays SchemaCrawler command-line help")
  private boolean helpRequested;
  private PrintStream out;
  @CommandLine.Spec
  private CommandLine.Model.CommandSpec spec;

  public boolean isHelpRequested()
  {
    return helpRequested;
  }

  @Override
  public void run()
  {
    ansi = CommandLine.Help.Ansi.AUTO;
    out = System.out;
    err = System.err;

    final CommandLine parent = new CommandLine(new SchemaCrawlerShellCommands(),
                                               new StateFactory(new SchemaCrawlerShellState()));

    if (commands != null && commands.length > 0)
    {
      final CommandLine subCommand = parent.getSubcommands().get(commands[0]);
      if (subCommand != null)
      {
        subCommand.usage(out, ansi);
      }
      else
      {
        throw new CommandLine.ParameterException(parent,
                                                 "Unknown sub-command '"
                                                 + commands[0] + "'.",
                                                 null,
                                                 commands[0]);
      }
    }
    else
    {
      parent.setHelpSectionKeys(Arrays.asList(SECTION_KEY_HEADER_HEADING,
                                              SECTION_KEY_HEADER,
                                              // SECTION_KEY_SYNOPSIS_HEADING,
                                              // SECTION_KEY_SYNOPSIS,
                                              SECTION_KEY_DESCRIPTION_HEADING,
                                              SECTION_KEY_DESCRIPTION,
                                              SECTION_KEY_PARAMETER_LIST_HEADING,
                                              SECTION_KEY_PARAMETER_LIST,
                                              SECTION_KEY_OPTION_LIST_HEADING,
                                              SECTION_KEY_OPTION_LIST,
                                              SECTION_KEY_COMMAND_LIST_HEADING,
                                              SECTION_KEY_COMMAND_LIST,
                                              SECTION_KEY_FOOTER_HEADING,
                                              SECTION_KEY_FOOTER));

      for (final String command : new String[] {
        "log",
        "config-file",
        "connect",
        "filter",
        "limit",
        "grep",
        "show",
        "sort",
        "load",
        "execute"
      })
      {
        final CommandLine subCommand = parent.getSubcommands().get(command);
        if (subCommand != null)
        {
          subCommand.usage(out, ansi);
          out.println();
          out.println();
        }
        else
        {
          throw new CommandLine.ParameterException(parent,
                                                   "Unknown sub-command '"
                                                   + command + "'.",
                                                   null,
                                                   command);
        }
      }

    }
  }

}

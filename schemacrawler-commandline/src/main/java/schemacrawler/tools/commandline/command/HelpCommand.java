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


import static java.util.Objects.requireNonNull;

import java.io.PrintStream;

import picocli.CommandLine;

@CommandLine.Command(name = "help",
                     header = "Displays SchemaCrawler command-line help",
                     synopsisHeading = "%nUsage: ",
                     helpCommand = true)
public final class HelpCommand
  implements CommandLine.IHelpCommandInitializable, Runnable
{

  @CommandLine.Parameters
  private final String[] commands = new String[0];
  private CommandLine.Help.Ansi ansi;
  private PrintStream err;
  @CommandLine.Option(names = { "-h", "--help" },
                      usageHelp = true,
                      description = "Displays SchemaCrawler command-line help")
  private boolean helpRequested;
  private PrintStream out;
  private CommandLine self;

  @Override
  public void run()
  {
    final CommandLine parent = self == null? null: self.getParent();
    if (parent == null) { return; }
    if (commands.length > 0)
    {
      final CommandLine subcommand = parent.getSubcommands().get(commands[0]);
      if (subcommand != null)
      {
        subcommand.usage(out, ansi);
      }
      else
      {
        throw new CommandLine.ParameterException(parent,
                                                 "Unknown subcommand '"
                                                 + commands[0] + "'.",
                                                 null,
                                                 commands[0]);
      }
    }
    else
    {
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
        final CommandLine subcommand = parent.getSubcommands().get(command);
        if (subcommand != null)
        {
          subcommand.usage(out, ansi);
        }
        else
        {
          throw new CommandLine.ParameterException(parent,
                                                   "Unknown subcommand '"
                                                   + command + "'.",
                                                   null,
                                                   command);
        }
      }

    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void init(final CommandLine helpCommandLine,
                   final CommandLine.Help.Ansi ansi,
                   final PrintStream out,
                   final PrintStream err)
  {
    self = requireNonNull(helpCommandLine, "No help command-line provided");
    this.ansi = requireNonNull(ansi, "No ANSI settings provided");
    this.out = requireNonNull(out, "No output stream provided");
    this.err = requireNonNull(err, "No output stream provided");
  }

}

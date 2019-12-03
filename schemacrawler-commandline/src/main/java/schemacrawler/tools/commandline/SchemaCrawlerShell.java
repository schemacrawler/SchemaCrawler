/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.commandline;


import static java.util.Objects.requireNonNull;
import static picocli.CommandLine.printHelpIfRequested;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.retrievePluginOptions;

import java.io.PrintWriter;
import java.util.logging.Level;

import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import picocli.CommandLine;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.PicocliException;
import picocli.shell.jline3.PicocliJLineCompleter;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.commandline.state.StateFactory;
import sf.util.SchemaCrawlerLogger;

public final class SchemaCrawlerShell
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SchemaCrawlerShell.class.getName());

  public static void execute(final String[] args)
    throws Exception
  {
    requireNonNull(args, "No arguments provided");

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    final StateFactory stateFactory = new StateFactory(state);

    final SchemaCrawlerShellCommands commands = new SchemaCrawlerShellCommands();
    final CommandLine commandLine = newCommandLine(commands,
                                                   stateFactory,
                                                   false);

    final Terminal terminal = TerminalBuilder.builder().build();
    final LineReader reader = LineReaderBuilder.builder().terminal(terminal)
      .completer(new PicocliJLineCompleter(commandLine.getCommandSpec()))
      .parser(new DefaultParser()).build();

    while (true)
    {
      try
      {
        final String line = reader
          .readLine("schemacrawler> ", null, (MaskingCallback) null, null);
        final ParsedLine pl = reader.getParser().parse(line, 0);
        final String[] arguments = pl.words().toArray(new String[0]);

        parseAndRun(state, commandLine, arguments);
      }
      catch (final UserInterruptException e)
      {
        // Ignore
      }
      catch (final EndOfFileException e)
      {
        return;
      }
      catch (final Exception e)
      {
        System.err.println("ERROR: " + e.getMessage());
        LOGGER.log(Level.WARNING, e.getMessage(), e);
      }
    }
  }

  private static void parseAndRun(final SchemaCrawlerShellState state,
                                  final CommandLine commandLine,
                                  final String[] arguments)
    throws SchemaCrawlerException
  {

    boolean badCommand = true;

    final ParseResult parseResult = commandLine.parseArgs(arguments);
    if (printHelpIfRequested(parseResult))
    {
      return;
    }

    final Config additionalConfig = retrievePluginOptions(parseResult);
    state.addAdditionalConfiguration(additionalConfig);

    if (parseResult.hasSubcommand())
    {
      for (final CommandLine subcommandLine : parseResult.subcommand()
        .asCommandLineList())
      {
        try
        {
          final Runnable command = subcommandLine.getCommand();
          if (command != null)
          {
            LOGGER.log(Level.INFO,
                       "Running command " + command.getClass().getSimpleName());
            command.run();

            badCommand = false;
            break;
          }
        }
        catch (final PicocliException e)
        {
          Throwable cause = e.getCause();
          if (cause == null)
          {
            cause = e;
          }
          state.setLastException(cause);
          // Print command help
          final PrintWriter out = subcommandLine.getOut();
          out.println("ERROR: " + cause.getMessage());
          out.println();
          out.println("Get help using:");
          out.printf("help %s%n", subcommandLine.getCommandName());

          return;
        }
      }
    }

    if (badCommand)
    {
      System.out.println("ERROR: Bad command");
      System.out.println();
      System.out.println("Get help using:");
      System.out.println("help");
    }
  }

  private SchemaCrawlerShell()
  {
    // Prevent instantiation
  }

}

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

package schemacrawler;


import static java.util.Objects.requireNonNull;
import static us.fatehi.commandlineparser.CommandLineUtility.*;

import java.util.logging.Level;

import picocli.CommandLine;
import schemacrawler.tools.commandline.SchemaCrawlerCommandLine;
import schemacrawler.tools.commandline.SchemaCrawlerShell;
import schemacrawler.tools.commandline.command.HelpCommand;
import schemacrawler.tools.commandline.shell.InteractiveShellOptions;

/**
 * Main class that takes arguments for a database for crawling a schema.
 */
public final class Main
{

  public static void main(final String[] args)
    throws Exception
  {
    requireNonNull(args, "No arguments provided");

    applyApplicationLogLevel(Level.OFF);

    logSafeArguments(args);
    logSystemClasspath();
    logSystemProperties();

    final InteractiveShellOptions interactiveShellOptions = new InteractiveShellOptions();
    picocli.CommandLine.populateCommand(interactiveShellOptions, args);

    final boolean isInteractive = interactiveShellOptions.isInteractive();
    if (isInteractive)
    {
      SchemaCrawlerShell.execute(args);
    }
    else
    {
      if (showHelpIfRequested(args))
      {
        return;
      }
      SchemaCrawlerCommandLine.execute(args);
    }

  }

  private static boolean showHelpIfRequested(final String[] args)
  {
    final HelpCommand helpCommand = new HelpCommand();
    final CommandLine helpCommandLine = new CommandLine(helpCommand);
    helpCommandLine.setUnmatchedArgumentsAllowed(true);
    helpCommandLine.parse(args);
    if (helpCommand.isHelpRequested())
    {
      helpCommand.run();
      return true;
    }
    return false;
  }

  private Main()
  {
    // Prevent instantiation
  }

}

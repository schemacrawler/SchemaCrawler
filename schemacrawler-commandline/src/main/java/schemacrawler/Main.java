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

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.commandline.ApplicationOptions;
import schemacrawler.tools.commandline.CommandLine;
import schemacrawler.tools.commandline.SchemaCrawlerCommandLine;
import schemacrawler.tools.commandline.SchemaCrawlerHelpCommandLine;
import schemacrawler.tools.commandline.parser.ApplicationOptionsParser;
import us.fatehi.commandlineparser.CommandLineUtility;

/**
 * Main class that takes arguments for a database for crawling a schema.
 */
public final class Main
{

  public static void main(final String[] args)
    throws Exception
  {
    requireNonNull(args, "No arguments provided");

    try
    {
      applyApplicationLogLevel(Level.OFF);

      final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
      optionsParser.parse(args);
      final ApplicationOptions applicationOptions = optionsParser
        .getApplicationOptions();

      final Config argsMap = CommandLineUtility
        .parseArgs(optionsParser.getRemainder());

      applyApplicationLogLevel(applicationOptions.getApplicationLogLevel());

      logSafeArguments(args);
      logSystemClasspath();
      logSystemProperties();

      final boolean showHelp =
        args.length == 0 || args.length == 1 && Main.class.getCanonicalName()
          .equals(args[0]) || applicationOptions.isShowHelp();

      final CommandLine commandLine;
      if (showHelp)
      {
        final boolean showVersionOnly = applicationOptions.isShowVersionOnly();
        commandLine = new SchemaCrawlerHelpCommandLine(argsMap,
                                                       showVersionOnly);
      }
      else
      {
        commandLine = new SchemaCrawlerCommandLine(args);
      }
      commandLine.execute();
    }
    catch (final Throwable e)
    {
      System.err
        .printf("%s %s%n%n", Version.getProductName(), Version.getVersion());
      final String errorMessage = e.getMessage();
      if (errorMessage != null)
      {
        System.err.printf("Error: %s%n%n", errorMessage);
      }
      System.err
        .println("Re-run SchemaCrawler with just the\n-?\noption for help");
      System.err.println();
      System.err.println(
        "Or, re-run SchemaCrawler with an additional\n--log-level=CONFIG\noption for details on the error");
      logSafeArguments(args);
      logFullStackTrace(Level.SEVERE, e);
    }

  }

  private Main()
  {
    // Prevent instantiation
  }

}

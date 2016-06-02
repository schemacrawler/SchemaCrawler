/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static us.fatehi.commandlineparser.CommandLineUtility.applyApplicationLogLevel;
import static us.fatehi.commandlineparser.CommandLineUtility.logFullStackTrace;
import static us.fatehi.commandlineparser.CommandLineUtility.logSafeArguments;
import static us.fatehi.commandlineparser.CommandLineUtility.logSystemProperties;

import java.util.logging.Level;

import static java.util.Objects.requireNonNull;

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.commandline.ApplicationOptionsParser;
import schemacrawler.tools.commandline.CommandLine;
import schemacrawler.tools.commandline.SchemaCrawlerCommandLine;
import schemacrawler.tools.commandline.SchemaCrawlerHelpCommandLine;
import schemacrawler.tools.options.ApplicationOptions;
import us.fatehi.commandlineparser.CommandLineUtility;

/**
 * Main class that takes arguments for a database for crawling a schema.
 */
public final class Main
{

  public static void main(final String[] args)
    throws Exception
  {
    requireNonNull(args);

    try
    {
      applyApplicationLogLevel(Level.OFF);

      final Config argsMap = CommandLineUtility.parseArgs(args);

      final ApplicationOptionsParser applicationOptionsParser = new ApplicationOptionsParser(argsMap);
      final ApplicationOptions applicationOptions = applicationOptionsParser
        .getOptions();

      applyApplicationLogLevel(applicationOptions.getApplicationLogLevel());

      logSafeArguments(args);
      logSystemProperties();

      final boolean showHelp = args.length == 0
                               || args.length == 1 && Main.class
                                 .getCanonicalName().equals(args[0])
                               || applicationOptions.isShowHelp();

      final CommandLine commandLine;
      if (showHelp)
      {
        final boolean showVersionOnly = applicationOptions.isShowVersionOnly();
        commandLine = new SchemaCrawlerHelpCommandLine(argsMap,
                                                       showVersionOnly);
      }
      else
      {
        commandLine = new SchemaCrawlerCommandLine(argsMap);
      }
      commandLine.execute();
    }
    catch (final Throwable e)
    {
      System.err.printf("%s %s%n%n",
                        Version.getProductName(),
                        Version.getVersion());
      final String errorMessage = e.getMessage();
      System.err.print("Error: ");
      System.err.println(errorMessage);
      System.err.println();
      System.err
        .println("Re-run SchemaCrawler with just the\n-?\noption for help");
      System.err.println();
      System.err
        .println("Or, re-run SchemaCrawler with an additional\n-loglevel=CONFIG\noption for details on the error");
      logSafeArguments(args);
      logFullStackTrace(Level.SEVERE, e);
    }

  }

  private Main()
  {
    // Prevent instantiation
  }

}

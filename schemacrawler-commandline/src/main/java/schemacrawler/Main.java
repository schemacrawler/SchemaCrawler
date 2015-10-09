/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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

    final Config argsMap = CommandLineUtility.parseArgs(args);

    final ApplicationOptionsParser applicationOptionsParser = new ApplicationOptionsParser(argsMap);
    final ApplicationOptions applicationOptions = applicationOptionsParser
      .getOptions();

    applyApplicationLogLevel(applicationOptions.getApplicationLogLevel());
    logSystemProperties();
    logSafeArguments(Level.CONFIG, args);

    try
    {
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
    catch (final Exception e)
    {
      final String errorMessage = e.getMessage();
      System.err.println(errorMessage);
      System.err.println("Re-run SchemaCrawler with the\n-?\noption for help");
      System.err
        .println("Or, re-run SchemaCrawler with an additional\n-loglevel=CONFIG\noption for details on the error");
      logSafeArguments(Level.SEVERE, args);
      logFullStackTrace(Level.SEVERE, e);
    }

  }

  private Main()
  {
    // Prevent instantiation
  }

}

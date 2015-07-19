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


import static java.util.Objects.requireNonNull;
import static sf.util.commandlineparser.CommandLineUtility.applyApplicationLogLevel;
import static sf.util.commandlineparser.CommandLineUtility.flattenCommandlineArgs;
import static sf.util.commandlineparser.CommandLineUtility.logSafeArguments;
import static sf.util.commandlineparser.CommandLineUtility.logSystemProperties;

import java.util.logging.Level;

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.commandline.ApplicationOptionsParser;
import schemacrawler.tools.commandline.CommandLine;
import schemacrawler.tools.commandline.DatabaseServerTypeParser;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.options.ApplicationOptions;
import schemacrawler.tools.options.DatabaseServerType;
import sf.util.commandlineparser.CommandLineUtility;

/**
 * Main class that takes arguments for a database for crawling a schema.
 */
public final class Main
{

  public static void main(final String[] args)
    throws Exception
  {
    requireNonNull(args);

    final Config config = CommandLineUtility.loadConfig(args);

    final ApplicationOptionsParser applicationOptionsParser = new ApplicationOptionsParser(config);
    final ApplicationOptions applicationOptions = applicationOptionsParser
      .getOptions();

    applyApplicationLogLevel(applicationOptions.getApplicationLogLevel());
    logSystemProperties();
    logSafeArguments(Level.CONFIG, args);

    try
    {
      final DatabaseServerTypeParser dbServerTypeParser = new DatabaseServerTypeParser(config);
      final DatabaseServerType dbServerType = dbServerTypeParser.getOptions();
      final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
      final DatabaseConnector dbConnector = registry
        .lookupDatabaseSystemIdentifier(dbServerType
          .getDatabaseSystemIdentifier());

      final boolean showHelp = args.length == 0 || args.length == 1
                               && Main.class.getCanonicalName().equals(args[0])
                               || applicationOptions.isShowHelp();

      final CommandLine commandLine;
      if (showHelp)
      {
        final boolean showVersionOnly = applicationOptions.isShowVersionOnly();
        commandLine = dbConnector.newHelpCommandLine(args, showVersionOnly);
      }
      else
      {
        commandLine = dbConnector
          .newCommandLine(flattenCommandlineArgs(config));
      }
      commandLine.execute();
    }
    catch (final Exception e)
    {
      final String errorMessage = e.getMessage();
      System.err.println(errorMessage);
      System.err.println("Re-run SchemaCrawler with the -? option for help");
      System.err
        .println("Or, re-run SchemaCrawler with the -loglevel=CONFIG option for details on the error");
      logSafeArguments(Level.SEVERE, args);
    }

  }

  private Main()
  {
    // Prevent instantiation
  }

}

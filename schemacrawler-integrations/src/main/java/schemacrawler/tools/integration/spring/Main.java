/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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

package schemacrawler.tools.integration.spring;


import static java.util.Objects.requireNonNull;
import static us.fatehi.commandlineparser.CommandLineUtility.applyApplicationLogLevel;
import static us.fatehi.commandlineparser.CommandLineUtility.logSafeArguments;
import static us.fatehi.commandlineparser.CommandLineUtility.logSystemProperties;

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.commandline.ApplicationOptionsParser;
import schemacrawler.tools.options.ApplicationOptions;
import sf.util.Utility;
import us.fatehi.commandlineparser.CommandLineUtility;

/**
 * Main class that takes arguments for a database for crawling a schema.
 */
public final class Main
{

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   *
   * @param args
   *        Arguments passed into the program from the command-line.
   * @throws Exception
   */
  public static void main(final String[] args)
    throws Exception
  {
    requireNonNull(args);

    final Config argsMap = CommandLineUtility.parseArgs(args);

    final ApplicationOptionsParser applicationOptionsParser = new ApplicationOptionsParser(argsMap);
    final ApplicationOptions applicationOptions = applicationOptionsParser
      .getOptions();

    if (applicationOptions.isShowHelp())
    {
      final String text = Utility
        .readResourceFully("/help/SchemaCrawler.spring.txt");
      System.out.println(text);
      return;
    }

    // Spring should use JDK logging, like the rest of SchemaCrawler
    System
      .setProperty("org.apache.commons.logging.Log",
                   org.apache.commons.logging.impl.Jdk14Logger.class.getName());
    applyApplicationLogLevel(applicationOptions.getApplicationLogLevel());
    logSafeArguments(args);
    logSystemProperties();

    final SchemaCrawlerSpringCommandLine commandLine = new SchemaCrawlerSpringCommandLine(argsMap);
    commandLine.execute();
  }

  private Main()
  {
    // Prevent instantiation
  }

}

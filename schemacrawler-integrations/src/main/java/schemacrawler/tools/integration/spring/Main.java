/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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

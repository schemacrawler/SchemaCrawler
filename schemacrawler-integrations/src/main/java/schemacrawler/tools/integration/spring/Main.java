/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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


import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.tools.commandline.ApplicationOptionsParser;
import schemacrawler.tools.options.ApplicationOptions;
import sf.util.Utility;

/**
 * Main class that takes arguments for a database for crawling a schema.
 */
public final class Main
{

  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   * @throws Exception
   */
  public static void main(final String[] args)
    throws Exception
  {
    String[] remainingArgs = args;
    final ApplicationOptionsParser applicationOptionsParser = new ApplicationOptionsParser();
    remainingArgs = applicationOptionsParser.parse(remainingArgs);
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
    System.setProperty("org.apache.commons.logging.Log",
                       org.apache.commons.logging.impl.Jdk14Logger.class
                         .getName());
    applicationOptions.applyApplicationLogLevel();
    LOGGER.log(Level.CONFIG, "Command line: " + Arrays.toString(args));

    final SchemaCrawlerSpringCommandLine commandLine = new SchemaCrawlerSpringCommandLine(remainingArgs);
    commandLine.execute();
  }

  private Main()
  {
    // Prevent instantiation
  }

}

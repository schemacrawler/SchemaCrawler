/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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


import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.tools.ToolsExecutor;
import sf.util.Utilities;
import dbconnector.datasource.PropertiesDataSource;

/**
 * Main class that takes arguments for a database for crawling a schema.
 */
public final class Main
{

  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  /**
   * Internal storage for information. Read from text file.
   */
  private static String info;

  static
  {
    final byte[] text = Utilities.readFully(Main.class
      .getResourceAsStream("/schemacrawler-readme.txt"));
    info = new String(text);

  }

  private Main()
  {
  }

  private static void printUsage()
  {
    System.out.println(Version.about());
    System.out.println(info);
  }

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.<BR>
   * 
   * @param args
   *          Arguments passed into the program from the command line.
   * @throws Exception
   *           On an exception
   */
  public static void main(final String[] args)
    throws Exception
  {
    if (args.length == 0)
    {
      printUsage();
      return;
    }

    doMain(args, new ToolsExecutor());
  }

  /**
   * Executes with the command line, and a given executor. The executor
   * allows for the command line to be parsed independently of the
   * excution. The execution can integrate with other software, such as
   * Velocity.
   * 
   * @param args
   *          Command line arguments
   * @param executor
   *          Executor
   * @throws Exception
   *           On an exception
   */
  public static void doMain(final String[] args, final Executor executor)
    throws Exception
  {

    final Options[] optionCommands = OptionsParser.parseCommandLine(args);

    if (optionCommands.length > 0)
    {
      final Options firstOption = optionCommands[0];
      Utilities.setApplicationLogLevel(firstOption.getLogLevel());
      LOGGER.log(Level.CONFIG, Version.about());
      LOGGER.log(Level.CONFIG, "Commandline: " + Arrays.asList(args));
      final Properties config = firstOption.getConfig();

      for (int i = 0; i < optionCommands.length; i++)
      {
        final Options options = optionCommands[i];
        LOGGER.log(Level.CONFIG, options.toString());
        final PropertiesDataSource dataSource = dbconnector.Main
          .createDataSource(args, config);
        if (executor instanceof ToolsExecutor)
        {
          ((ToolsExecutor) executor)
            .setAdditionalConnectionConfiguration(dataSource
              .getSourceProperties());
        }
        executor.execute(options, dataSource);
      }
    }
  }

}

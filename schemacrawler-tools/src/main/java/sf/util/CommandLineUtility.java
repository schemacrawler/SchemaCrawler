/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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
package sf.util;


import java.util.Enumeration;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Utility for the command line.
 * 
 * @author sfatehi
 */
public final class CommandLineUtility
{

  /**
   * Does a quick check of the command line arguments to find any
   * commonly used help options.
   * 
   * @param args
   *        Command line arguments.
   * @param about
   *        About text.
   * @param helpResource
   *        Resource file containing help text.
   */
  public static void checkForHelp(final String[] args,
                                  final String about,
                                  final String helpResource)
  {
    boolean printUsage = false;
    if (args.length == 0)
    {
      printUsage = true;
    }
    for (final String arg: args)
    {
      if (arg.equalsIgnoreCase("-help") || arg.equalsIgnoreCase("-?")
          || arg.equalsIgnoreCase("--help"))
      {
        printUsage = true;
        break;
      }
    }
    if (printUsage)
    {
      final byte[] text = Utilities.readFully(CommandLineUtility.class
        .getResourceAsStream(helpResource));
      final String info = new String(text);

      System.out.println(about);
      System.out.println(info);
      System.exit(0);
    }
  }

  /**
   * Sets the application-wide log level.
   * 
   * @param logLevel
   *        Log level to set
   */
  public static void setApplicationLogLevel(final Level logLevel)
  {
    final LogManager logManager = LogManager.getLogManager();
    for (final Enumeration<String> loggerNames = logManager.getLoggerNames(); loggerNames
      .hasMoreElements();)
    {
      final String loggerName = loggerNames.nextElement();
      final Logger logger = logManager.getLogger(loggerName);
      logger.setLevel(null);
      final Handler[] handlers = logger.getHandlers();
      for (final Handler handler: handlers)
      {
        handler.setLevel(logLevel);
      }
    }

    final Logger rootLogger = Logger.getLogger("");
    rootLogger.setLevel(logLevel);
  }

  /**
   * Parses the command line, and sets the application log level.
   * 
   * @param args
   *        Command line arguments
   */
  public static void setLogLevel(final String[] args)
  {
    final String OPTION_loglevel = "loglevel";

    final CommandLineParser parser = new CommandLineParser();
    parser
      .addOption(new CommandLineParser.StringOption(CommandLineParser.Option.NO_SHORT_FORM,
                                                    OPTION_loglevel,
                                                    "OFF"));
    parser.parse(args);

    final String logLevelString = parser.getStringOptionValue(OPTION_loglevel);
    final Level logLevel = Level.parse(logLevelString
      .toUpperCase(Locale.ENGLISH));
    setApplicationLogLevel(logLevel);
  }

  private CommandLineUtility()
  {
    // Prevent instantiation
  }

}

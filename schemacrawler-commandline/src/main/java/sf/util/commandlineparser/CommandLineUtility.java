/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package sf.util.commandlineparser;


import static sf.util.Utility.join;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public class CommandLineUtility
{

  private static final Logger LOGGER = Logger
    .getLogger(CommandLineUtility.class.getName());

  /**
   * Sets the application-wide log level.
   */
  public static void applyApplicationLogLevel(final Level applicationLogLevel)
  {
    if (applicationLogLevel == null)
    {
      return;
    }

    final LogManager logManager = LogManager.getLogManager();
    final List<String> loggerNames = Collections.list(logManager
      .getLoggerNames());
    for (final String loggerName: loggerNames)
    {
      final Logger logger = logManager.getLogger(loggerName);
      if (logger != null)
      {
        logger.setLevel(null);
        for (final Handler handler: logger.getHandlers())
        {
          handler.setLevel(applicationLogLevel);
        }
      }
    }

    final Logger rootLogger = Logger.getLogger("");
    rootLogger.setLevel(applicationLogLevel);
  }

  public static String[] flattenCommandlineArgs(final Map<String, String> argsMap)
  {
    final List<String> argsList = new ArrayList<>();
    for (final Map.Entry<String, String> arg: argsMap.entrySet())
    {
      final String key = arg.getKey();
      final String value = arg.getValue();
      if (value != null)
      {
        argsList.add(String.format("-%s=%s", key, value));
      }
      else
      {
        argsList.add(String.format("-%s", key));
      }
    }
    final String[] args = argsList.toArray(new String[0]);
    return args;
  }

  /**
   * Loads configuration from a number of command-line.
   */
  public static Config loadConfig(final String[] args)
    throws SchemaCrawlerException
  {
    final CommandLineArgumentsParser argsParser = new CommandLineArgumentsParser(args);
    argsParser.parse();
    final Map<String, String> optionsMap = argsParser.getOptionsMap();
    // Override/ overwrite from the command-line options
    final Config config = new Config();
    config.putAll(optionsMap);
    return config;
  }

  public static void logSafeArguments(final Level level, final String[] args)
  {
    if (args == null)
    {
      return;
    }
    if (level == null || !LOGGER.isLoggable(level))
    {
      return;
    }

    final List<String> argsList = new ArrayList<>(Arrays.asList(args));
    for (final Iterator<String> iterator = argsList.iterator(); iterator
      .hasNext();)
    {
      final String arg = iterator.next();
      if (arg == null || arg.startsWith("-password="))
      {
        iterator.remove();
      }
    }

    LOGGER.log(level,
               "Command line: \n" + join(argsList, System.lineSeparator()));
  }

  public static void logSystemProperties()
  {
    if (!LOGGER.isLoggable(Level.CONFIG))
    {
      return;
    }

    final SortedMap<String, String> systemProperties = new TreeMap<>();
    for (final Entry<Object, Object> propertyValue: System.getProperties()
      .entrySet())
    {
      final String name = (String) propertyValue.getKey();
      if ((name.startsWith("java.") || name.startsWith("os."))
          && !name.endsWith(".path"))
      {
        systemProperties.put(name, (String) propertyValue.getValue());
      }
    }
    LOGGER.log(Level.CONFIG,
               "System properties: \n"
                   + join(systemProperties, System.lineSeparator()));
    LOGGER.log(Level.CONFIG,
               "Classpath: \n"
                   + join(System.getProperty("java.class.path")
                            .split(File.pathSeparator),
                          System.lineSeparator()));
  }

  private CommandLineUtility()
  {
    // Prevent instantiation
  }
}

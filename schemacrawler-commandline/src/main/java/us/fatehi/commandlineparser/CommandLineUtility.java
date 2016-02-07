/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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
package us.fatehi.commandlineparser;


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

import schemacrawler.Version;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.FormattedStringSupplier;

public class CommandLineUtility
{

  private static final Logger LOGGER = Logger
    .getLogger(CommandLineUtility.class.getName());

  /**
   * Sets the application-wide log level.
   */
  public static void applyApplicationLogLevel(final Level applicationLogLevel)
  {
    final Level logLevel;
    if (applicationLogLevel == null)
    {
      logLevel = Level.OFF;
    }
    else
    {
      logLevel = applicationLogLevel;
    }

    final LogManager logManager = LogManager.getLogManager();
    final List<String> loggerNames = Collections
      .list(logManager.getLoggerNames());
    for (final String loggerName: loggerNames)
    {
      final Logger logger = logManager.getLogger(loggerName);
      if (logger != null)
      {
        logger.setLevel(null);
        for (final Handler handler: logger.getHandlers())
        {
          handler.setLevel(logLevel);
        }
      }
    }

    final Logger rootLogger = Logger.getLogger("");
    rootLogger.setLevel(logLevel);
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

  public static void logFullStackTrace(final Level level, final Throwable t)
  {
    if (level == null || !LOGGER.isLoggable(level))
    {
      return;
    }
    if (t == null)
    {
      return;
    }

    LOGGER.log(level, t.getMessage(), t);
  }

  public static void logSafeArguments(final String[] args)
  {
    if (!LOGGER.isLoggable(Level.INFO))
    {
      return;
    }

    LOGGER.log(Level.INFO,
               new FormattedStringSupplier("%s, v%s",
                                           Version.getProductName(),
                                           Version.getVersion()));

    if (args == null)
    {
      return;
    }

    final List<String> argsList = new ArrayList<>();
    for (final Iterator<String> iterator = Arrays.asList(args)
      .iterator(); iterator.hasNext();)
    {
      final String arg = iterator.next();
      if (arg == null)
      {
        continue;
      }
      else if (arg.startsWith("-password="))
      {
        argsList.add("-password=*****");
      }
      else if (arg.startsWith("-password"))
      {
        argsList.add("-password");
        if (iterator.hasNext())
        {
          // Skip over the password
          iterator.next();
          argsList.add("*****");
        }
      }
      else
      {
        argsList.add(arg);
      }
    }

    LOGGER
      .log(Level.INFO,
           new FormattedStringSupplier("Command line: %n%s",
                                       join(argsList, System.lineSeparator())));
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

    if (LOGGER.isLoggable(Level.CONFIG))
    {
      LOGGER.log(Level.CONFIG,
                 new FormattedStringSupplier("System properties: %n%s",
                                             join(systemProperties,
                                                  System.lineSeparator())));
      LOGGER.log(Level.CONFIG,
                 new FormattedStringSupplier("Classpath: %n%s",
                                             join(System
                                               .getProperty("java.class.path")
                                               .split(File.pathSeparator),
                                                  System.lineSeparator())));
    }
  }

  /**
   * Loads configuration from a number of command-line.
   */
  public static Config parseArgs(final String[] args)
    throws SchemaCrawlerException
  {
    final CommandLineArgumentsParser argsParser = new CommandLineArgumentsParser(args);
    argsParser.parse();
    final Map<String, String> optionsMap = argsParser.getOptionsMap();
    return new Config(optionsMap);
  }

  private CommandLineUtility()
  {
    // Prevent instantiation
  }
}

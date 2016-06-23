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
package us.fatehi.commandlineparser;


import static sf.util.Utility.join;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
import sf.util.StringFormat;

public final class CommandLineUtility
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
               new StringFormat("%s, v%s",
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

    LOGGER.log(Level.INFO,
               new StringFormat("Command line: %n%s",
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
      LOGGER
        .log(Level.CONFIG,
             new StringFormat("System properties: %n%s",
                              join(systemProperties, System.lineSeparator())));
      LOGGER.log(Level.CONFIG,
                 new StringFormat("Classpath: %n%s",
                                  join(System.getProperty("java.class.path")
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
    final Config optionsMap = argsParser.getOptionsMap();
    return optionsMap;
  }

  private CommandLineUtility()
  {
    // Prevent instantiation
  }
}

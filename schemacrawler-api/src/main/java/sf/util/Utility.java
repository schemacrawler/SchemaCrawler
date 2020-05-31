/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package sf.util;


import static java.util.Objects.requireNonNull;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Utility methods.
 *
 * @author Sualeh Fatehi
 */
@UtilityMarker
public final class Utility
{

  /**
   * Sets the application-wide log level.
   *
   * @param applicationLogLevel
   *   Log level to set
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
    final List<String> loggerNames =
      Collections.list(logManager.getLoggerNames());
    for (final String loggerName : loggerNames)
    {
      final Logger logger = logManager.getLogger(loggerName);
      if (logger != null)
      {
        logger.setLevel(null);
        for (final Handler handler : logger.getHandlers())
        {
          try
          {
            handler.setEncoding("UTF-8");
          }
          catch (final UnsupportedEncodingException e)
          {
            // Ignore exception
          }
          handler.setLevel(logLevel);
        }
      }
    }

    final Logger rootLogger = Logger.getLogger("");
    rootLogger.setLevel(logLevel);

    applySlf4jLogLevel(logLevel);
    applyPicocliLogLevel(logLevel);
  }

  /**
   * @param logLevel
   *   Log level to be set
   * @see <a href="https://picocli.info/#_tracing">picocli Tracing</a>
   */
  private static void applyPicocliLogLevel(final Level logLevel)
  {
    // See
    // See
    final String picocliLogLevel;
    final String logLevelName = logLevel.getName();
    switch (logLevelName)
    {
      case "OFF":
        picocliLogLevel = "OFF";
        break;
      case "SEVERE":
      case "WARNING":
        picocliLogLevel = "WARN";
        break;
      case "CONFIG":
      case "INFO":
        picocliLogLevel = "INFO";
        break;
      default:
        picocliLogLevel = "DEBUG";
        break;
    }

    System.setProperty("picocli.trace", picocliLogLevel);
  }

  /**
   * @param logLevel
   *   Log level to be set
   * @see <a href="https://www.slf4j.org/api/org/slf4j/impl/SimpleLogger.html">SLF4J
   *   log levels</a>
   */
  private static void applySlf4jLogLevel(final Level logLevel)
  {
    final String slf4jLogLevel;
    switch (logLevel.getName())
    {
      case "OFF":
        slf4jLogLevel = "off";
        break;
      case "SEVERE":
        slf4jLogLevel = "error";
        break;
      case "WARNING":
        slf4jLogLevel = "warn";
        break;
      case "CONFIG":
        slf4jLogLevel = "debug";
        break;
      case "INFO":
        slf4jLogLevel = "info";
        break;
      default:
        slf4jLogLevel = "trace";
        break;
    }

    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", slf4jLogLevel);
  }

  public static String commonPrefix(final String string1, final String string2)
  {
    if (string1 == null || string2 == null)
    {
      return "";
    }
    final int index = indexOfDifference(string1, string2);
    if (index == -1)
    {
      return null;
    }
    else
    {
      return string1
        .substring(0, index)
        .toLowerCase();
    }
  }

  /**
   * Checks if the text contains whitespace.
   *
   * @param text
   *   Text to check.
   * @return Whether the string contains whitespace.
   */
  public static boolean containsWhitespace(final CharSequence text)
  {
    if (text == null || text.length() == 0)
    {
      return false;
    }

    for (int i = 0; i < text.length(); i++)
    {
      if (Character.isWhitespace(text.charAt(i)))
      {
        return true;
      }
    }
    return false;
  }

  public static String convertForComparison(final String text)
  {
    if (text == null || text.length() == 0)
    {
      return text;
    }

    final StringBuilder builder = new StringBuilder(text.length());
    for (int i = 0; i < text.length(); i++)
    {
      final char ch = text.charAt(i);
      if (Character.isLetterOrDigit(ch) || ch == '_' || ch == '.')
      {
        builder.append(Character.toLowerCase(ch));
      }
    }

    final String textWithoutQuotes = builder.toString();
    return textWithoutQuotes;
  }

  public static <E extends Enum<E>> E enumValue(final String value,
                                                final E defaultValue)
  {
    requireNonNull(defaultValue, "No default value provided");
    E enumValue;
    if (value == null)
    {
      enumValue = defaultValue;
    }
    else
    {
      try
      {
        Class<?> enumClass = defaultValue.getClass();
        if (enumClass.getEnclosingClass() != null)
        {
          enumClass = enumClass.getEnclosingClass();
        }
        enumValue = Enum.valueOf((Class<E>) enumClass, value);
      }
      catch (final Exception e)
      {
        enumValue = defaultValue;
      }
    }
    return enumValue;
  }

  public static <E extends Enum<E> & IdentifiedEnum> E enumValueFromId(final int value,
                                                                       final E defaultValue)
  {
    requireNonNull(defaultValue, "No default value provided");
    try
    {
      final Class<E> enumClass = (Class<E>) defaultValue.getClass();
      for (final E enumValue : EnumSet.allOf(enumClass))
      {
        if (enumValue.getId() == value)
        {
          return enumValue;
        }
      }
    }
    catch (final Exception e)
    {
      // Ignore
    }
    return defaultValue;
  }

  private static int indexOfDifference(final String string1,
                                       final String string2)
  {
    if (string1 == null || string2 == null)
    {
      return 0;
    }
    int i;
    for (i = 0; i < string1.length() && i < string2.length(); ++i)
    {
      if (string1.charAt(i) != string2.charAt(i))
      {
        break;
      }
    }
    if (i < string2.length() || i < string1.length())
    {
      return i;
    }
    return -1;
  }

  /**
   * Checks if the text is null or empty.
   *
   * @param text
   *   Text to check.
   * @return Whether the string is blank.
   */
  public static boolean isBlank(final CharSequence text)
  {
    if (text == null || text.length() == 0)
    {
      return true;
    }

    for (int i = 0; i < text.length(); i++)
    {
      if (!Character.isWhitespace(text.charAt(i)))
      {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if a class is available on the classpath.
   *
   * @param className
   *   Class to check
   * @return True if the class is available, false otherwise
   */
  public static boolean isClassAvailable(final String className)
  {
    try
    {
      Class.forName(className, false, Utility.class.getClassLoader());
      return true;
    }
    catch (final Exception e)
    {
      return false;
    }
  }

  /**
   * Checks if the text contains an integer only.
   *
   * @param text
   *   Text to check.
   * @return Whether the string is an integer.
   */
  public static boolean isIntegral(final CharSequence text)
  {
    if (text == null || text.length() == 0)
    {
      return false;
    }

    for (int i = 0; i < text.length(); i++)
    {
      final char ch = text.charAt(i);
      if (!Character.isDigit(ch) && ch != '+' && ch != '-')
      {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if the text is all lowercase.
   *
   * @param text
   *   Text to check.
   * @return Whether the string is all lowercase.
   */
  public static boolean isLowerCase(final String text)
  {
    return text != null && text.equals(text.toLowerCase());
  }

  public static String join(final Collection<String> collection,
                            final String separator)
  {
    if (collection == null || collection.isEmpty())
    {
      return null;
    }

    final StringJoiner joiner = new StringJoiner(separator);
    joiner.setEmptyValue("");
    for (final String string : collection)
    {
      joiner.add(string);
    }

    return joiner.toString();
  }

  public static String join(final Map<?, ?> map, final String separator)
  {
    if (map == null || map.isEmpty())
    {
      return null;
    }

    final StringJoiner joiner = new StringJoiner(separator);
    for (final Entry<?, ?> entry : map.entrySet())
    {
      joiner.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
    }

    return joiner.toString();
  }

  public static String toSnakeCase(final String identifier)
  {
    if (isBlank(identifier))
    {
      return identifier;
    }
    final Pattern identifyCamelCase = Pattern.compile("([A-Z])");
    final String snakeCaseIdentifier = identifyCamelCase
      .matcher(identifier)
      .replaceAll("_$1")
      .toLowerCase();
    return snakeCaseIdentifier;
  }

  private Utility()
  {
    // Prevent instantiation
  }

}

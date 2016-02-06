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
package sf.util;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Utility methods.
 *
 * @author Sualeh Fatehi
 */
public final class Utility
{

  private static final Logger LOGGER = Logger
    .getLogger(Utility.class.getName());

  public static final Predicate<String> filterOutBlank = word -> !isBlank(word);

  public static String commonPrefix(final String string1, final String string2)
  {
    final int index = indexOfDifference(string1, string2);
    if (index == -1)
    {
      return null;
    }
    else
    {
      return string1.substring(0, index).toLowerCase();
    }
  }

  /**
   * Checks if the text contains whitespace.
   *
   * @param text
   *        Text to check.
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
    final String textWithoutQuotes;
    if (!isBlank(text))
    {
      final char[] charArray = text.toCharArray();
      final StringBuilder builder = new StringBuilder(text.length());
      for (final char ch: charArray)
      {
        if (Character.isLetterOrDigit(ch) || ch == '_' || ch == '.')
        {
          builder.append(Character.toLowerCase(ch));
        }
      }
      textWithoutQuotes = builder.toString();
    }
    else
    {
      textWithoutQuotes = text;
    }
    return textWithoutQuotes;
  }

  /**
   * Reads the stream fully, and writes to the writer.
   *
   * @param reader
   *        Reader to read.
   * @return Byte array
   */
  public static void copy(final Reader reader, final Writer writer)
  {
    if (reader == null)
    {
      LOGGER.log(Level.WARNING, "Cannot read null reader");
      return;
    }
    if (writer == null)
    {
      LOGGER.log(Level.WARNING, "Cannot write null writer");
      return;
    }

    final char[] buffer = new char[0x10000];
    try
    {
      // Do not close resources - that is the responsibility of the
      // caller
      final Reader bufferedReader = new BufferedReader(reader, buffer.length);
      final BufferedWriter bufferedWriter = new BufferedWriter(writer,
                                                               buffer.length);

      int read;
      do
      {
        read = bufferedReader.read(buffer, 0, buffer.length);
        if (read > 0)
        {
          bufferedWriter.write(buffer, 0, read);
        }
      } while (read >= 0);

      bufferedWriter.flush();
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    }
  }

  /**
   * Checks if the text is null or empty.
   *
   * @param text
   *        Text to check.
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
   * Checks if the text is all lowercase.
   *
   * @param text
   *        Text to check.
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

    final StringBuilder buffer = new StringBuilder(1024);
    for (final Iterator<String> iterator = collection.iterator(); iterator
      .hasNext();)
    {
      buffer.append(iterator.next());
      if (iterator.hasNext())
      {
        buffer.append(separator);
      }
    }

    return buffer.toString();
  }

  public static String join(final Map<String, String> map,
                            final String separator)
  {
    if (map == null || map.isEmpty())
    {
      return null;
    }

    final StringBuilder buffer = new StringBuilder(1024);
    final Set<Entry<String, String>> entrySet = map.entrySet();
    for (final Iterator<Entry<String, String>> iterator = entrySet
      .iterator(); iterator.hasNext();)
    {
      final Entry<String, String> entry = iterator.next();
      buffer.append(entry.getKey()).append("=").append(entry.getValue());
      if (iterator.hasNext())
      {
        buffer.append(separator);
      }
    }

    return buffer.toString();
  }

  public static String join(final String[] collection, final String separator)
  {
    return join(Arrays.asList(collection), separator);
  }

  public static String readFully(final InputStream stream)
  {
    if (stream == null)
    {
      return null;
    }
    final Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
    return readFully(reader);
  }

  /**
   * Reads the stream fully, and returns a byte array of data.
   *
   * @param reader
   *        Reader to read.
   * @return Byte array
   */
  public static String readFully(final Reader reader)
  {
    if (reader == null)
    {
      LOGGER.log(Level.WARNING, "Cannot read null reader");
      return "";
    }

    try
    {
      final StringWriter writer = new StringWriter();
      copy(reader, writer);
      writer.close();
      return writer.toString();
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      return "";
    }

  }

  public static String readResourceFully(final String resource)
  {
    return readFully(Utility.class.getResourceAsStream(resource));
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

  private Utility()
  { // Prevent instantiation
  }

}

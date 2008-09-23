/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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

package schemacrawler.tools.util;


import java.io.PrintWriter;

/**
 * Utility.
 * 
 * @author Sualeh Fatehi
 */
public final class FormatUtils
{

  /** Maximum output line length */
  public static final int MAX_LINE_LENGTH = 72;

  private static final char QUOTE = '\"';

  private static final char SEPARATOR = ',';

  /**
   * Enclose the value in quotes and escape the quote and comma
   * characters that are inside.
   * 
   * @param value
   *        needs to be escaped and quoted
   * @return the value, escaped and quoted.
   */
  public static String escapeAndQuoteForExcelCsv(final String text)
  {
    final String value = String.valueOf(text);
    final int length = value.length();
    if (length == 0)
    {
      return "\"\"";
    }

    if (value.indexOf(SEPARATOR) < 0 && value.indexOf(QUOTE) < 0)
    {
      return value;
    }

    final StringBuffer sb = new StringBuffer(length);
    sb.append(QUOTE);
    for (int i = 0; i < length; i++)
    {
      final char c = value.charAt(i);
      if (c == QUOTE)
      {
        sb.append(QUOTE + QUOTE);
      }
      else
      {
        sb.append(c);
      }
    }
    sb.append(QUOTE);

    return sb.toString();

  }

  /**
   * Right justifies the string in given field length.
   * 
   * @param string
   *        String to right justify
   * @param len
   *        Length of the field
   * @return Justified string
   */
  public static String padLeft(final String string, final int len)
  {
    final StringBuffer buffer = new StringBuffer();
    if (string != null)
    {
      buffer.append(string);
    }
    while (buffer.length() < len)
    {
      buffer.insert(0, ' ');
    }
    return buffer.toString();
  }

  /**
   * Left justifies the string in given field length.
   * 
   * @param string
   *        String to right justify
   * @param len
   *        Length of the field
   * @return Justified string
   */
  public static String padRight(final String string, final int len)
  {
    final StringBuffer buffer = new StringBuffer();
    if (string != null)
    {
      buffer.append(string);
    }
    while (buffer.length() < len)
    {
      buffer.append(' ');
    }
    return buffer.toString();
  }

  /**
   * Prints information.
   * 
   * @param object
   *        Object to print
   * @param out
   *        Output writer
   */
  public static void printHeaderObject(final Object object,
                                       final PrintWriter out)
  {
    out.println(repeat("-", MAX_LINE_LENGTH));
    out.println(object.toString());
    out.println(repeat("-", MAX_LINE_LENGTH));
    out.println();
    out.println();
    out.flush();
  }

  /**
   * Repeats a string.
   * 
   * @param string
   *        String to repeat
   * @param count
   *        Number of times to repeat
   * @return String with repetitions
   */
  public static String repeat(final String string, final int count)
  {

    String repeated = "";

    if (string != null && count >= 1)
    {
      final StringBuffer stringbuffer = new StringBuffer(string.length()
                                                         * count);
      for (int i = 0; i < count; i++)
      {
        stringbuffer.append(string);
      }
      repeated = stringbuffer.toString();
    }

    return repeated;

  }

  private FormatUtils()
  {
  }

}

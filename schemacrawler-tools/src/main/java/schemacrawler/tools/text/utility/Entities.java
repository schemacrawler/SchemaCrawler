/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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
package schemacrawler.tools.text.utility;


/**
 * XML escapes entities.
 *
 * @author Sualeh Fatehi
 */
public final class Entities
{

  /**
   * XML escapes the characters in some text.
   *
   * @param text
   *        Text to escape.
   * @return XML-escaped text
   */
  public static String escapeForXMLAttribute(final String text)
  {
    final StringBuilder buffer = new StringBuilder(text.length() * 2);
    for (int i = 0; i < text.length(); ++i)
    {
      final char ch = text.charAt(i);
      switch (ch)
      {
        case 34:
          buffer.append("&quot;");
          break;
        case 62:
          buffer.append("&gt;");
          break;
        case 38:
          buffer.append("&amp;");
          break;
        case 60:
          buffer.append("&lt;");
          break;
        case 39:
          buffer.append("&apos;");
          break;
        default:
          buffer.append(ch);
          break;
      }
    }
    return buffer.toString();
  }

  /**
   * XML escapes the characters in some text.
   *
   * @param text
   *        Text to escape.
   * @return XML-escaped text
   */
  public static String escapeForXMLElement(final String text)
  {
    final StringBuilder buffer = new StringBuilder(text.length() * 2);
    for (int i = 0; i < text.length(); ++i)
    {
      final char ch = text.charAt(i);
      switch (ch)
      {
        case 62:
          buffer.append("&gt;");
          break;
        case 38:
          buffer.append("&amp;");
          break;
        case 60:
          buffer.append("&lt;");
          break;
        default:
          buffer.append(ch);
          break;
      }
    }
    return buffer.toString();
  }

}

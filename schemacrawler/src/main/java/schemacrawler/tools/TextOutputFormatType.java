/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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

package schemacrawler.tools;


import java.io.Serializable;

/**
 * Enumeration for text format type.
 */
public final class TextOutputFormatType
  implements Serializable
{

  /**
   * Text formatting.
   */
  public static final TextOutputFormatType TEXT = new TextOutputFormatType("text");

  /**
   * HTML formatting.
   */
  public static final TextOutputFormatType HTML = new TextOutputFormatType("html");

  /**
   * CSV formatting.
   */
  public static final TextOutputFormatType CSV = new TextOutputFormatType("csv");

  private static final long serialVersionUID = 7312561736857867298L;

  private static final TextOutputFormatType[] TEXT_FORMAT_TYPE_ALL = {
      TEXT, HTML, CSV
  };

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param outputFormatTypeString
   *        String value of table type
   * @return Enumeration value
   */
  public static TextOutputFormatType valueOf(final String outputFormatTypeString)
  {

    TextOutputFormatType outputFormatType = null;

    for (final TextOutputFormatType element: TEXT_FORMAT_TYPE_ALL)
    {
      if (element.toString().equalsIgnoreCase(outputFormatTypeString))
      {
        outputFormatType = element;
        break;
      }
    }

    return outputFormatType;

  }

  private final String name;

  private TextOutputFormatType(final String name)
  {
    this.name = name;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    return name;
  }

}

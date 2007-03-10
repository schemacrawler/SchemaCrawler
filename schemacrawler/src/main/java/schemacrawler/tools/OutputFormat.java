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
public final class OutputFormat
  implements Serializable
{

  /**
   * Text formatting.
   */
  public static final OutputFormat TEXT = new OutputFormat("text");

  /**
   * HTML formatting.
   */
  public static final OutputFormat HTML = new OutputFormat("html");

  /**
   * CSV formatting.
   */
  public static final OutputFormat CSV = new OutputFormat("csv");

  /**
   * Other formatting.
   */
  public static final OutputFormat OTHER = new OutputFormat("other");

  private static final long serialVersionUID = 7312561736857867298L;

  private static final OutputFormat[] TEXT_FORMAT_TYPE_ALL = {
      TEXT, HTML, CSV, OTHER
  };

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param outputFormatTypeString
   *        String value of table type
   * @return Enumeration value
   */
  public static OutputFormat valueOf(final String outputFormatTypeString)
  {

    OutputFormat outputFormatType = OTHER;

    for (final OutputFormat element: TEXT_FORMAT_TYPE_ALL)
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

  private OutputFormat(final String name)
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

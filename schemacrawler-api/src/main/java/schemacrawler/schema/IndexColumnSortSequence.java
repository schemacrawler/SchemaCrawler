/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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

package schemacrawler.schema;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An enumeration wrapper around index sort sequences.
 */
public enum IndexColumnSortSequence
{

  /**
   * Unknown
   */
  unknown("unknown"),
  /**
   * Ascending.
   */
  ascending("A"),
  /**
   * Descending.
   */
  descending("D");

  private static final Logger LOGGER = Logger
    .getLogger(IndexColumnSortSequence.class.getName());

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param code
   *        Sort sequence code.
   * @return Enumeration value
   */
  public static IndexColumnSortSequence valueOfFromCode(final String code)
  {
    for (final IndexColumnSortSequence type: IndexColumnSortSequence.values())
    {
      if (type.getCode().equalsIgnoreCase(code))
      {
        return type;
      }
    }
    LOGGER.log(Level.FINE, "Unknown code  " + code);
    return unknown;
  }

  private final String code;

  private IndexColumnSortSequence(final String code)
  {
    this.code = code;
  }

  /**
   * Index sort sequence code.
   * 
   * @return Index sort sequence code
   */
  String getCode()
  {
    return code;
  }

}

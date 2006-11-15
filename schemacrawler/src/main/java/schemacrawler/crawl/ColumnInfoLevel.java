/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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

package schemacrawler.crawl;


/**
 * Enumeration for level of column detail.
 */
public final class ColumnInfoLevel
{

  /**
   * No column detail.
   */
  public static final ColumnInfoLevel NONE = new ColumnInfoLevel("NONE");

  /**
   * Basic column detail.
   */
  public static final ColumnInfoLevel BASIC = new ColumnInfoLevel("BASIC");

  /**
   * Verbose column detail.
   */
  public static final ColumnInfoLevel VERBOSE = new ColumnInfoLevel("VERBOSE");

  private static final ColumnInfoLevel[] COLUMN_INFO_LEVEL_ALL = {
      NONE, BASIC, VERBOSE
  };

  private final String levelName;

  private ColumnInfoLevel(final String name)
  {
    levelName = name;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  public String toString()
  {
    return levelName;
  }

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param columnInfoLevelString
   *        String value of table type
   * @return Enumeration value
   */
  public static ColumnInfoLevel valueOf(final String columnInfoLevelString)
  {

    ColumnInfoLevel columnInfoLevel = null;

    for (int i = 0; i < COLUMN_INFO_LEVEL_ALL.length; i++)
    {
      if (COLUMN_INFO_LEVEL_ALL[i].toString()
        .equalsIgnoreCase(columnInfoLevelString))
      {
        columnInfoLevel = COLUMN_INFO_LEVEL_ALL[i];
        break;
      }
    }

    return columnInfoLevel;

  }

}

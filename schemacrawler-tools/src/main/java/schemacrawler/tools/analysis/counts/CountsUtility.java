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
package schemacrawler.tools.analysis.counts;


import static java.util.Objects.requireNonNull;

import schemacrawler.schema.Table;

public class CountsUtility
{

  private static final int UNKNOWN_TABLE_ROW_COUNT = -1;
  private static final String TABLE_ROW_COUNT_KEY = "schemacrawler.table.count";

  public static final long getRowCount(final Table table)
  {
    if (table == null)
    {
      return UNKNOWN_TABLE_ROW_COUNT;
    }

    final long tableCount = table
      .getAttribute(TABLE_ROW_COUNT_KEY, Long.valueOf(UNKNOWN_TABLE_ROW_COUNT));
    return tableCount;
  }

  /**
   * Message format for the counts.
   *
   * @param number
   *        Number value in the message
   * @return Message format for the counts
   */
  public static String getRowCountMessage(final Number number)
  {
    requireNonNull(number, "No number provided");
    final long longValue = number.longValue();
    if (longValue <= 0)
    {
      return "empty";
    }
    else
    {
      return String.format("%,d rows", longValue);
    }
  }

  public static final String getRowCountMessage(final Table table)
  {
    return getRowCountMessage(getRowCount(table));
  }

  public static final boolean hasRowCount(final Table table)
  {
    return table != null && table.hasAttribute(TABLE_ROW_COUNT_KEY);
  }

  static void addRowCountToTable(final Table table, final long rowCount)
  {
    if (table != null)
    {
      if (rowCount >= 0)
      {
        table.setAttribute(TABLE_ROW_COUNT_KEY, rowCount);
      }
      else
      {
        table.removeAttribute(TABLE_ROW_COUNT_KEY);
      }
    }
  }

  private CountsUtility()
  {
    // Prevent instantiation
  }

}

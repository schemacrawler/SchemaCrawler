/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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


import schemacrawler.schema.TableReference;

public class CountsUtility
{

  static final int UNKNOWN_TABLE_COUNT = -1;
  private static final String TABLE_COUNTS_KEY = "schemacrawler.table.count";

  public static final long getCount(final TableReference table)
  {
    if (table == null)
    {
      return UNKNOWN_TABLE_COUNT;
    }

    final long tableCount = table.getAttribute(TABLE_COUNTS_KEY, Long
      .valueOf(UNKNOWN_TABLE_COUNT));
    return tableCount;
  }

  static void addCountToTable(final TableReference table, final long tableCount)
  {
    if (table != null)
    {
      if (tableCount >= 0)
      {
        table.setAttribute(TABLE_COUNTS_KEY, tableCount);
      }
      else
      {
        table.removeAttribute(TABLE_COUNTS_KEY);
      }
    }
  }

  private CountsUtility()
  {
  }

}

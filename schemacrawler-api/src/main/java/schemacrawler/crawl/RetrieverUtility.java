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
package schemacrawler.crawl;


import static sf.util.Utility.isBlank;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class RetrieverUtility
{

  /**
   * Reads a single column result set as a list.
   *
   * @param results
   *        Result set
   * @return List
   * @throws SQLException
   *         On an exception
   */
  static List<String> readResultsVector(final ResultSet results)
    throws SQLException
  {
    final List<String> values = new ArrayList<>();
    if (results == null)
    {
      return values;
    }

    try
    {
      while (results.next())
      {
        final String value = results.getString(1);
        if (!results.wasNull() && !isBlank(value))
        {
          values.add(value.trim());
        }
      }
    }
    finally
    {
      results.close();
    }
    return values;
  }

  private RetrieverUtility()
  {
    // Cannot instantitate
  }

}

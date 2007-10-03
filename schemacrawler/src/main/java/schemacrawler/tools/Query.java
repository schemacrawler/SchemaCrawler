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
import java.util.Set;

import sf.util.Utilities;

/**
 * A SQL query. May be parameterized with ant-like variable references.
 * 
 * @author sfatehi
 */
public class Query
  implements Serializable
{

  private static final long serialVersionUID = 2820769346069413473L;

  private final String name;
  private final String query;

  /**
   * Definition of a query, including a name, and parameterized or
   * regular SQL.
   * 
   * @param name
   *        Query name.
   * @param query
   *        Query SQL.
   */
  public Query(final String name, final String query)
  {
    if (name == null || name.length() == 0)
    {
      throw new IllegalArgumentException("No name provided for the query");
    }
    this.name = name;
    if (query == null || query.length() == 0)
    {
      throw new IllegalArgumentException("No SQL provided for query '" + name
                                         + "'");
    }
    this.query = query;
  }

  /**
   * @return the name
   */
  public String getName()
  {
    return name;
  }

  /**
   * @return the query
   */
  public String getQuery()
  {
    return query;
  }

  boolean isQueryOver()
  {
    boolean isQueryOver = false;
    final Set<String> keys = Utilities.extractTemplateVariables(query);
    final String[] queryOverKeys = {
        "table", "table_type"
    };
    for (final String element: queryOverKeys)
    {
      if (keys.contains(element))
      {
        isQueryOver = true;
        break;
      }
    }
    return isQueryOver;
  }

}

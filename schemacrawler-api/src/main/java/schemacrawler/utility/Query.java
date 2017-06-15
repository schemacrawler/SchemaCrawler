/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.utility;


import static sf.util.TemplatingUtility.extractTemplateVariables;
import static sf.util.Utility.isBlank;

import java.io.Serializable;
import java.util.Set;

/**
 * A SQL query. May be parameterized with ant-like variable references.
 *
 * @author sfatehi
 */
public final class Query
  implements Serializable
{

  private static final long serialVersionUID = 2820769346069413473L;

  private final boolean hasName;

  private final String name;

  private final String query;
  private final boolean throwSQLException;

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
    this(name, query, false);
  }

  /**
   * Definition of a query, including a name, and parameterized or
   * regular SQL.
   *
   * @param name
   *        Query name.
   * @param query
   *        Query SQL.
   * @param throwSQLException
   *        Whether the query should throw a SQL exception on an error.
   */
  public Query(final String name,
               final String query,
               final boolean throwSQLException)
  {
    final boolean isNameProvided = !isBlank(name);
    final boolean isQueryProvided = !isBlank(query);
    if (isNameProvided && isQueryProvided)
    {
      this.name = name;
      this.query = query;
      hasName = true;
    }
    else if (isNameProvided && !isQueryProvided)
    {
      this.name = this.query = name;
      hasName = false;
    }
    else
    {
      throw new IllegalArgumentException("No SQL found for query");
    }
    this.throwSQLException = throwSQLException;
  }

  /**
   * Gets the query name.
   *
   * @return Query name
   */
  public String getName()
  {
    return name;
  }

  /**
   * Gets the query SQL.
   *
   * @return Query SQL
   */
  public String getQuery()
  {
    return query;
  }

  /**
   * Whether a query name was provided.
   *
   * @return Whether a query name was provided
   */
  public boolean hasName()
  {
    return hasName;
  }

  /**
   * Determines if this query has substitutable parameters, and whether
   * it should be run once for each table.
   *
   * @return If the query is to be run over each table
   */
  public boolean isQueryOver()
  {
    final Set<String> keys = extractTemplateVariables(query);
    return keys.contains("table");
  }

  /**
   * Whether the query should throw a SQL exception on an error during
   * execution.
   *
   * @return Whether the query should throw a SQL exception on an error
   */
  public boolean isThrowSQLException()
  {
    return throwSQLException;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return String.format("-- \"%s\"%n%s", name, query);
  }

}

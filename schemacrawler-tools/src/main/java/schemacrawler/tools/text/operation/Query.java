/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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

package schemacrawler.tools.text.operation;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import schemacrawler.crawl.JavaSqlType.JavaSqlTypeGroup;
import schemacrawler.crawl.JavaSqlTypesUtility;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import sf.util.TemplatingUtility;
import sf.util.Utility;

/**
 * A SQL query. May be parameterized with ant-like variable references.
 * 
 * @author sfatehi
 */
final class Query
  implements Serializable
{

  private static final long serialVersionUID = 2820769346069413473L;

  private static String getOrderByColumnsListAsString(final Table table)
  {
    final List<Column> columns = table.getColumns();
    final StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < columns.size(); i++)
    {
      final Column column = columns.get(i);
      final JavaSqlTypeGroup javaSqlTypeGroup = JavaSqlTypesUtility
        .lookupSqlDataType(column.getColumnDataType().getType()).getJavaSqlTypeGroup();
      if (javaSqlTypeGroup != JavaSqlTypeGroup.large_object)
      {
        if (i > 0)
        {
          buffer.append(", ");
        }
        buffer.append(column.getName());
      }
    }
    return buffer.toString();
  }

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
  Query(final String name, final String query)
  {
    final boolean isNameProvided = !Utility.isBlank(name);
    final boolean isQueryProvided = !Utility.isBlank(query);
    if (isNameProvided && isQueryProvided)
    {
      this.name = name;
      this.query = query;
    }
    else if (isNameProvided && !isQueryProvided)
    {
      this.name = this.query = name;
    }
    else
    {
      throw new IllegalArgumentException("No SQL found for query");
    }
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
    return TemplatingUtility.expandTemplate(query);
  }

  /**
   * Determines if this query has substitutable parameters, and whether
   * it should be run once for each table.
   * 
   * @return If the query is to be run over each table
   */
  public boolean isQueryOver()
  {
    final Set<String> keys = TemplatingUtility.extractTemplateVariables(query);
    return keys.contains("table");
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return name + ":" + query;
  }

  /**
   * Gets the query with parameters substituted.
   * 
   * @param table
   *        Table information
   * @return Ready-to-execute quer
   */
  String getQueryForTable(final Table table)
  {
    final Map<String, String> tableProperties = new HashMap<String, String>();
    if (table != null)
    {
      if (table.getSchema() != null)
      {
        tableProperties.put("schema", table.getSchema().getFullName());
      }
      tableProperties.put("table", table.getFullName());
      tableProperties.put("columns", table.getColumnsListAsString());
      tableProperties.put("orderbycolumns",
                          getOrderByColumnsListAsString(table));
      tableProperties.put("tabletype", table.getTableType().toString());
    }

    String sql = query;
    sql = TemplatingUtility.expandTemplate(sql, tableProperties);
    sql = TemplatingUtility.expandTemplate(sql);

    return sql;
  }

}

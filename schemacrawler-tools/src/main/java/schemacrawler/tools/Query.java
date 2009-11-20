/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import schemacrawler.crawl.JavaSqlTypesUtility;
import schemacrawler.crawl.JavaSqlType.JavaSqlTypeGroup;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Config;
import sf.util.Utility;

/**
 * A SQL query. May be parameterized with ant-like variable references.
 * 
 * @author sfatehi
 */
public final class Query
  implements Serializable
{

  private static final long serialVersionUID = 2820769346069413473L;

  /**
   * Interpolate substrings into system property values. Substrings of
   * the form ${<i>propname</i>} are interpolated into the text of the
   * system property whose key matches <i>propname</i>. For example,
   * expandProperties("hello.${user.name}.world") is "hello.foo.world"
   * when called by a user named "foo". Property substrings can be
   * nested. References to nonexistent system properties are
   * interpolated to an empty string.
   * 
   * @param template
   *        Template
   * @return Expanded template
   */
  private static String expandTemplateFromProperties(final String template)
  {
    return expandTemplateFromProperties(template, new Config(System
      .getProperties()));
  }

  /**
   * Interpolate substrings into property values. Substrings of the form
   * ${<i>propname</i>} are interpolated into the text of the system
   * property whose key matches <i>propname</i>. For example,
   * expandProperties("hello.${user.name}.world") is "hello.foo.world"
   * when called by a user named "foo". Property substrings can be
   * nested. References to nonexistent system properties are
   * interpolated to an empty string.
   * 
   * @param template
   *        Template
   * @param properties
   *        Properties to substitute in the template
   * @return Expanded template
   */
  private static String expandTemplateFromProperties(final String template,
                                                     final Map<String, String> properties)
  {

    if (template == null)
    {
      return null;
    }

    String expandedTemplate = template;
    for (int left; (left = expandedTemplate.indexOf("${")) >= 0;)
    {
      final int inner = expandedTemplate.indexOf("${", left + 2);
      final int right = expandedTemplate.indexOf("}", left + 2);
      if (inner >= 0 && inner < right)
      {
        // Evaluate nested property value
        expandedTemplate = expandedTemplate.substring(0, inner)
                           + expandTemplateFromProperties(expandedTemplate
                             .substring(inner));
      }
      else if (right >= 0)
      {
        // Evaluate this property value
        final String propertyKey = expandedTemplate.substring(left + 2, right);
        String propertyValue = properties.get(propertyKey);
        if (propertyValue == null)
        {
          propertyValue = "";
        }
        expandedTemplate = expandedTemplate.substring(0, left) + propertyValue
                           + expandedTemplate.substring(right + 1);
      }
      else
      {
        // Unmatched left delimiter - ignore
        break;
      }
    }

    return expandedTemplate;

  }

  /**
   * Gets a list of template variables.
   * 
   * @param template
   *        Template to extract variables from
   * @return Set of variables
   */
  private static Set<String> extractTemplateVariables(final String template)
  {

    if (template == null)
    {
      return new HashSet<String>();
    }

    String shrunkTemplate = template;
    final Set<String> keys = new HashSet<String>();
    for (int left; (left = shrunkTemplate.indexOf("${")) >= 0;)
    {
      final int right = shrunkTemplate.indexOf("}", left + 2);
      if (right >= 0)
      {
        final String propertyKey = shrunkTemplate.substring(left + 2, right);
        keys.add(propertyKey);
        // Destroy key, so we can find the next one
        shrunkTemplate = shrunkTemplate.substring(0, left) + ""
                         + shrunkTemplate.substring(right + 1);
      }
    }

    return keys;
  }

  private static String getOrderByColumnsListAsString(final Table table)
  {
    final Column[] columnsArray = table.getColumns();
    final StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < columnsArray.length; i++)
    {
      final Column column = columnsArray[i];
      final JavaSqlTypeGroup javaSqlTypeGroup = JavaSqlTypesUtility
        .lookupSqlDataType(column.getType().getType()).getJavaSqlTypeGroup();
      if (javaSqlTypeGroup != JavaSqlTypeGroup.binary)
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
  public Query(final String name, final String query)
  {
    if (Utility.isBlank(name))
    {
      throw new IllegalArgumentException("No name provided for the query");
    }
    this.name = name;

    if (Utility.isBlank(query))
    {
      throw new IllegalArgumentException("No SQL provided for query '" + name
                                         + "'");
    }
    this.query = query;
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
    return expandTemplateFromProperties(query);
  }

  /**
   * Gets the query with parameters substituted.
   * 
   * @param table
   *        Table information
   * @return Ready-to-execute quer
   */
  public String getQueryForTable(final Table table)
  {
    final Map<String, String> tableProperties = new HashMap<String, String>();
    if (table != null)
    {
      if (table.getSchema() != null)
      {
        tableProperties.put("catalog", table.getSchema().getParent().getName());
        tableProperties.put("schema", table.getSchema().getName());
      }
      tableProperties.put("table", table.getFullName());
      tableProperties.put("columns", table.getColumnsListAsString());
      tableProperties.put("orderbycolumns",
                          getOrderByColumnsListAsString(table));
      tableProperties.put("tabletype", table.getType().toString());
    }

    String sql = query;
    sql = expandTemplateFromProperties(sql, tableProperties);
    sql = expandTemplateFromProperties(sql);

    return sql;
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
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return name + ":" + query;
  }

}

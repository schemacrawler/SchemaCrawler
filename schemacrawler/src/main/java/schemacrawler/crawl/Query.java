/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.crawl;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import schemacrawler.schema.Table;

/**
 * A SQL query. May be parameterized with ant-like variable references.
 * 
 * @author sfatehi
 */
public class Query
  implements Serializable
{

  private static final long serialVersionUID = 2820769346069413473L;

  private static final String NEWLINE = System.getProperty("line.separator");

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
    return expandTemplateFromProperties(template, System.getProperties());
  }

  /**
   * Interpolate substrings into property values. Substrings of the form ${<i>propname</i>}
   * are interpolated into the text of the system property whose key
   * matches <i>propname</i>. For example,
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
                                                     final Properties properties)
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
        Object propertyValue = properties.get(propertyKey);
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
        // Destroy key, so we can find teh next one
        shrunkTemplate = shrunkTemplate.substring(0, left) + ""
                         + shrunkTemplate.substring(right + 1);
      }
    }

    return keys;
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
    final Properties tableProperties = new Properties();
    if (table != null)
    {
      if (table.getCatalogName() != null)
      {
        tableProperties.setProperty("catalog", table.getCatalogName());
      }
      if (table.getSchemaName() != null)
      {
        tableProperties.setProperty("schema", table.getSchemaName());
      }
      tableProperties.setProperty("table", table.getFullName());
      tableProperties.setProperty("columns", table.getColumnsListAsString());
      tableProperties.setProperty("tabletype", table.getType().toString());
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
    return name + ":" + NEWLINE + query;
  }

}

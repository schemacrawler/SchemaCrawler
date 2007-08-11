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
package schemacrawler.crawl;


import java.util.Properties;

import schemacrawler.schema.Table;
import sf.util.Utilities;

/**
 * Utilities.
 * 
 * @author Sualeh Fatehi
 */
public final class CrawlerUtililties
{
  /**
   * Expands placeholders in the template SQL from the table properties.
   * 
   * @param templateSql
   *        Template SQL
   * @param table
   *        Table
   * @return Expanded SQL
   */
  public static String expandSqlForTable(final String templateSql,
                                         final Table table)
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

    String sql = templateSql;
    sql = Utilities.expandTemplateFromProperties(sql, tableProperties);
    sql = Utilities.expandTemplateFromProperties(sql);

    return sql;

  }

  private CrawlerUtililties()
  {
    // Prevent instantiation
  }

}

/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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
package schemacrawler.tools.linter;


import java.util.ArrayList;
import java.util.List;

import schemacrawler.crawl.JavaSqlType.JavaSqlTypeGroup;
import schemacrawler.crawl.JavaSqlTypesUtility;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.LintSeverity;

public class LinterTooManyLobs
  extends BaseLinter
{

  public LinterTooManyLobs()
  {
    setLintSeverity(LintSeverity.low);
  }

  @Override
  public String getDescription()
  {
    return getSummary();
  }

  @Override
  public String getSummary()
  {
    return "too many binary objects";
  }

  @Override
  protected void lint(final Table table)
  {
    if (table == null)
    {
      throw new IllegalArgumentException("No table provided");
    }

    final List<Column> lobColumns = findLobColumns(table.getColumns());
    if (lobColumns.size() > 1)
    {
      final Column[] columns = lobColumns
        .toArray(new Column[lobColumns.size()]);
      addLint(table, getSummary(), columns);
    }
  }

  private List<Column> findLobColumns(final Column[] columns)
  {
    final List<Column> lobColumns = new ArrayList<Column>();
    for (final Column column: columns)
    {
      final JavaSqlTypeGroup javaSqlTypeGroup = JavaSqlTypesUtility
        .lookupSqlDataType(column.getType().getType()).getJavaSqlTypeGroup();
      if (javaSqlTypeGroup == JavaSqlTypeGroup.large_object)
      {
        lobColumns.add(column);
      }
    }
    return lobColumns;
  }

}

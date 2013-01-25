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

  private static final int MAX_LOBS_IN_TABLE = 1;

  public LinterTooManyLobs()
  {
    setSeverity(LintSeverity.low);
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

    final ArrayList<Column> lobColumns = findLobColumns(table.getColumns());
    if (lobColumns.size() > MAX_LOBS_IN_TABLE)
    {
      addLint(table, getSummary(), lobColumns);
    }
  }

  private ArrayList<Column> findLobColumns(final List<Column> columns)
  {
    final ArrayList<Column> lobColumns = new ArrayList<Column>();
    for (final Column column: columns)
    {
      final JavaSqlTypeGroup javaSqlTypeGroup = JavaSqlTypesUtility
        .lookupSqlDataType(column.getColumnDataType().getType()).getJavaSqlTypeGroup();
      if (javaSqlTypeGroup == JavaSqlTypeGroup.large_object)
      {
        lobColumns.add(column);
      }
    }
    return lobColumns;
  }

}

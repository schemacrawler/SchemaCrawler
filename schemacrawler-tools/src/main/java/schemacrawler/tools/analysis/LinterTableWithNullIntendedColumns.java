/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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
package schemacrawler.tools.analysis;


import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import sf.util.ObjectToString;
import sf.util.Utility;

public class LinterTableWithNullIntendedColumns
  extends BaseLinter<Table>
{

  public void lint(final Table table)
  {
    if (table != null)
    {
      final Column[] nullDefaultValueMayBeIntendedColumns = findNullDefaultValueMayBeIntendedColumns(table
        .getColumns());
      if (nullDefaultValueMayBeIntendedColumns.length > 0)
      {
        addLint(table, new Lint("columns where NULL may be intended",
          nullDefaultValueMayBeIntendedColumns)
        {

          private static final long serialVersionUID = 4306137113072609086L;

          @Override
          public String getLintValueAsString()
          {
            final List<String> columnNames = new ArrayList<String>();
            for (final Column column: nullDefaultValueMayBeIntendedColumns)
            {
              columnNames.add(column.getName());
            }
            return ObjectToString.toString(columnNames);
          }
        });
      }
    }
  }

  private Column[] findNullDefaultValueMayBeIntendedColumns(final Column[] columns)
  {
    final List<Column> nullDefaultValueMayBeIntendedColumns = new ArrayList<Column>();
    for (final Column column: columns)
    {
      final String columnDefaultValue = column.getDefaultValue();
      if (!Utility.isBlank(columnDefaultValue)
          && columnDefaultValue.trim().equalsIgnoreCase("NULL"))
      {
        nullDefaultValueMayBeIntendedColumns.add(column);
      }
    }
    return nullDefaultValueMayBeIntendedColumns
      .toArray(new Column[nullDefaultValueMayBeIntendedColumns.size()]);
  }

}

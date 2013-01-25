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

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;

public class LinterTableWithQuotedNames
  extends BaseLinter
{

  @Override
  public String getDescription()
  {
    return getSummary();
  }

  @Override
  public String getSummary()
  {
    return "spaces in name, or reserved word";
  }

  @Override
  protected void lint(final Table table)
  {
    if (table == null)
    {
      throw new IllegalArgumentException("No table provided");
    }

    final List<String> spacesInNamesList = findColumnsWithQuotedNames(table
      .getColumns());
    final String tableName = table.getName();
    if (isQuotedName(tableName))
    {
      spacesInNamesList.add(0, tableName);
    }
    for (final String spacesInName: spacesInNamesList)
    {
      addLint(table, getSummary(), spacesInName);
    }
  }

  private List<String> findColumnsWithQuotedNames(final List<Column> columns)
  {
    final List<String> columnsWithQuotedNames = new ArrayList<String>();
    for (final Column column: columns)
    {
      final String columnName = column.getName();
      if (isQuotedName(columnName))
      {
        columnsWithQuotedNames.add(columnName);
      }
    }
    return columnsWithQuotedNames;
  }

  private boolean isQuotedName(final String name)
  {
    final int nameLength = name.length();
    final char[] namechars = new char[nameLength];
    name.getChars(0, nameLength, namechars, 0);
    return !Character.isJavaIdentifierStart(namechars[0])
           && namechars[0] == namechars[nameLength - 1];
  }

}

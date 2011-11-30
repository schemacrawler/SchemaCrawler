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
package schemacrawler.tools.linter;


import static schemacrawler.tools.lint.LintUtility.columns;
import static schemacrawler.tools.lint.LintUtility.foreignKeyColumns;
import static schemacrawler.tools.lint.LintUtility.listStartsWith;

import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.LintSeverity;

public class LinterForeignKeyWithNoIndices
  extends BaseLinter
{

  public LinterForeignKeyWithNoIndices()
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
    return "foreign key with no index";
  }

  @Override
  protected void lint(final Table table)
  {
    if (table == null)
    {
      throw new IllegalArgumentException("No table provided");
    }

    final List<ForeignKey> foreignKeysWithoutIndices = findForeignKeysWithoutIndices(table);
    for (final ForeignKey foreignKey: foreignKeysWithoutIndices)
    {
      addLint(table, getSummary(), foreignKey);
    }
  }

  private List<ForeignKey> findForeignKeysWithoutIndices(final Table table)
  {
    final List<ForeignKey> foreignKeysWithoutIndices = new ArrayList<ForeignKey>();
    if (!(table instanceof View))
    {
      final List<List<String>> indexColumnsList = new ArrayList<List<String>>();
      for (final Index index: table.getIndices())
      {
        final List<String> indexColumns = columns(index);
        indexColumnsList.add(indexColumns);
      }

      final ForeignKey[] importedForeignKeys = table.getImportedForeignKeys();
      for (final ForeignKey foreignKey: importedForeignKeys)
      {
        final List<String> foreignKeyColumns = foreignKeyColumns(foreignKey);
        boolean hasIndex = false;
        for (final List<String> indexColumns: indexColumnsList)
        {
          if (listStartsWith(indexColumns, foreignKeyColumns))
          {
            hasIndex = true;
            break;
          }
        }
        if (!hasIndex)
        {
          foreignKeysWithoutIndices.add(foreignKey);
        }
      }
    }
    return foreignKeysWithoutIndices;
  }

}

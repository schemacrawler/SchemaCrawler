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

import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.LintSeverity;

public class LinterForeignKeyMismatch
  extends BaseLinter
{

  public LinterForeignKeyMismatch()
  {
    setSeverity(LintSeverity.high);
  }

  @Override
  public String getDescription()
  {
    return getSummary();
  }

  @Override
  public String getSummary()
  {
    return "foreign key and primary key have different data types";
  }

  @Override
  protected void lint(final Table table)
  {
    if (table == null)
    {
      throw new IllegalArgumentException("No table provided");
    }

    final List<ForeignKey> mismatchedForeignKeys = findMismatchedForeignKeys(table);
    for (final ForeignKey foreignKey: mismatchedForeignKeys)
    {
      addLint(table, getSummary(), foreignKey);
    }
  }

  private List<ForeignKey> findMismatchedForeignKeys(final Table table)
  {
    final List<ForeignKey> mismatchedForeignKeys = new ArrayList<ForeignKey>();
    if (table != null && !(table instanceof View))
    {
      final ForeignKey[] importedForeignKeys = table.getImportedForeignKeys();
      for (final ForeignKey foreignKey: importedForeignKeys)
      {
        final ForeignKeyColumnMap[] columnPairs = foreignKey.getColumnPairs();
        for (final ForeignKeyColumnMap columnPair: columnPairs)
        {
          final Column pkColumn = columnPair.getPrimaryKeyColumn();
          final Column fkColumn = columnPair.getForeignKeyColumn();
          if (!pkColumn.getType().equals(fkColumn.getType())
              || pkColumn.getSize() != fkColumn.getSize())
          {
            mismatchedForeignKeys.add(foreignKey);
            break;
          }
        }
      }
    }
    return mismatchedForeignKeys;
  }
}

/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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


import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.lint.LintUtility.listStartsWith;
import static schemacrawler.utility.MetaDataUtility.allIndexCoumnNames;
import static schemacrawler.utility.MetaDataUtility.foreignKeyColumnNames;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.LintSeverity;

public class LinterForeignKeyWithNoIndexes
  extends BaseLinter
{

  public LinterForeignKeyWithNoIndexes()
  {
    setSeverity(LintSeverity.low);
  }

  @Override
  public String getSummary()
  {
    return "foreign key with no index";
  }

  @Override
  protected void lint(final Table table, final Connection connection)
  {
    requireNonNull(table, "No table provided");

    final List<ForeignKey> foreignKeysWithoutIndexes = findForeignKeysWithoutIndexes(table);
    for (final ForeignKey foreignKey: foreignKeysWithoutIndexes)
    {
      addLint(table, getSummary(), foreignKey);
    }
  }

  private List<ForeignKey> findForeignKeysWithoutIndexes(final Table table)
  {
    final List<ForeignKey> foreignKeysWithoutIndexes = new ArrayList<>();
    if (!(table instanceof View))
    {
      final Collection<List<String>> allIndexCoumns = allIndexCoumnNames(table);
      for (final ForeignKey foreignKey: table.getImportedForeignKeys())
      {
        final List<String> foreignKeyColumns = foreignKeyColumnNames(foreignKey);
        boolean hasIndex = false;
        for (final List<String> indexColumns: allIndexCoumns)
        {
          if (listStartsWith(indexColumns, foreignKeyColumns))
          {
            hasIndex = true;
            break;
          }
        }
        if (!hasIndex)
        {
          foreignKeysWithoutIndexes.add(foreignKey);
        }
      }
    }
    return foreignKeysWithoutIndexes;
  }

}

/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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

import java.sql.Connection;

import schemacrawler.filter.TableTypesFilter;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.LintSeverity;

public class LinterTableWithPrimaryKeyNotFirst
  extends BaseLinter
{

  public LinterTableWithPrimaryKeyNotFirst()
  {
    setSeverity(LintSeverity.low);
    setTableTypesFilter(new TableTypesFilter("TABLE"));
  }

  @Override
  public String getSummary()
  {
    return "primary key not first";
  }

  @Override
  protected void lint(final Table table, final Connection connection)
  {
    requireNonNull(table, "No table provided");

    final PrimaryKey primaryKey = table.getPrimaryKey();
    if (primaryKey == null)
    {
      return;
    }

    for (final IndexColumn indexColumn: primaryKey.getColumns())
    {
      if (indexColumn.getIndexOrdinalPosition() != indexColumn
        .getOrdinalPosition())
      {
        addTableLint(table, getSummary());
        break;
      }
    }
  }

}

/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.tools.linter;


import static java.util.Objects.requireNonNull;

import java.sql.Connection;

import schemacrawler.filter.TableTypesFilter;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;

public class LinterUselessSurrogateKey
  extends BaseLinter
{

  public LinterUselessSurrogateKey()
  {
    setTableTypesFilter(new TableTypesFilter("TABLE"));
  }

  @Override
  public String getSummary()
  {
    return "useless surrogate key";
  }

  @Override
  protected void lint(final Table table, final Connection connection)
  {
    requireNonNull(table, "No table provided");

    if (hasUselessSurrogateKey(table))
    {
      addTableLint(table, getSummary(), table.getPrimaryKey());
    }
  }

  private boolean hasUselessSurrogateKey(final Table table)
  {

    if (table.getPrimaryKey() != null)
    {
      boolean hasUselessSurrogateKey = true;
      for (final Column column: getColumns(table))
      {
        if (column.isPartOfPrimaryKey())
        {
          continue;
        }
        if (!column.isPartOfForeignKey())
        {
          hasUselessSurrogateKey = false;
          break;
        }
      }
      return hasUselessSurrogateKey;
    }

    return false;
  }

}

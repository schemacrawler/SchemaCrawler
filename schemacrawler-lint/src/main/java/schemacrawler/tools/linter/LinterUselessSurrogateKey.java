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


import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.tools.lint.BaseLinter;

public class LinterUselessSurrogateKey
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
    return "useless surrogate key";
  }

  @Override
  protected void lint(final Table table)
  {
    if (table == null)
    {
      throw new IllegalArgumentException("No table provided");
    }

    if (hasUselessSurrogateKey(table))
    {
      addLint(table, getSummary(), table.getPrimaryKey());
    }
  }

  private boolean hasUselessSurrogateKey(final Table table)
  {

    if (!(table instanceof View) && table.getPrimaryKey() != null)
    {
      boolean hasUselessSurrogateKey = true;
      for (final Column column: table.getColumns())
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

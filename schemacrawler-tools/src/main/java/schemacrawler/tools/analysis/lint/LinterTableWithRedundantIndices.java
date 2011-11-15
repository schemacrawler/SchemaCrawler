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
package schemacrawler.tools.analysis.lint;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import schemacrawler.schema.Index;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;

public class LinterTableWithRedundantIndices
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
    return "redundant indices";
  }

  @Override
  public void lint(final Table table)
  {
    if (table != null && !(table instanceof View))
    {
      final Index[] indices = table.getIndices();
      if (indices.length > 0)
      {
        Map<Index, List<String>> indexColumns = new HashMap<Index, List<String>>(indices.length);
        for (Index index: indices)
        {
          indexColumns.put(index, LintUtility.columns(index));
        }

        addLint(table, getSummary(), true);
      }
    }
  }

}

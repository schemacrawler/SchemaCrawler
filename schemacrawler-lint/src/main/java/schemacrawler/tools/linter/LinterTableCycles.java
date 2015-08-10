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

import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import sf.util.graph.DirectedGraph;

public class LinterTableCycles
  extends BaseLinter
{

  private DirectedGraph<Table> tablesGraph;

  @Override
  public String getSummary()
  {
    return "cycles in table relationships";
  }

  @Override
  protected void end()
  {
    requireNonNull(tablesGraph, "Not initialized");

    if (tablesGraph.containsCycle())
    {
      addCatalogLint(getSummary(), Boolean.TRUE);
    }

    tablesGraph = null;

    super.end();
  }

  @Override
  protected void lint(final Table table)
  {
    requireNonNull(table, "No table provided");
    requireNonNull(tablesGraph, "Not initialized");

    tablesGraph.addVertex(table);
    for (final ForeignKey foreignKey: table.getForeignKeys())
    {
      for (final ForeignKeyColumnReference columnReference: foreignKey)
      {
        tablesGraph
          .addDirectedEdge(columnReference.getPrimaryKeyColumn().getParent(),
                           columnReference.getForeignKeyColumn().getParent());
      }
    }

  }

  @Override
  protected void start()
  {
    super.start();

    tablesGraph = new DirectedGraph<>();
  }

}

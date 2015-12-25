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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import sf.util.graph.DirectedGraph;
import sf.util.graph.TarjanStronglyConnectedComponentFinder;

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

    final Collection<List<Table>> sccs = new TarjanStronglyConnectedComponentFinder<>(tablesGraph)
      .detectCycles();
    if (!sccs.isEmpty())
    {
      for (final List<Table> list: sccs)
      {
        addCatalogLint(getSummary(), new ArrayList<>(list));
      }
    }

    tablesGraph = null;

    super.end();
  }

  @Override
  protected void lint(final Table table, final Connection connection)
  {
    requireNonNull(table, "No table provided");
    requireNonNull(tablesGraph, "Not initialized");

    tablesGraph.addVertex(table);
    for (final ForeignKey foreignKey: table.getForeignKeys())
    {
      for (final ForeignKeyColumnReference columnReference: foreignKey)
      {
        tablesGraph.addEdge(columnReference.getPrimaryKeyColumn().getParent(),
                            columnReference.getForeignKeyColumn().getParent());
      }
    }

  }

  @Override
  protected void start()
  {
    super.start();

    tablesGraph = new DirectedGraph<>(getId());
  }

}

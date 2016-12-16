/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
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
  protected void end(final Connection connection)
    throws SchemaCrawlerException
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

    super.end(connection);
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
  protected void start(final Connection connection)
    throws SchemaCrawlerException
  {
    super.start(connection);

    tablesGraph = new DirectedGraph<>(getLinterId());
  }

}

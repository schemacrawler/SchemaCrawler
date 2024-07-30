/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.Linter;
import us.fatehi.utility.graph.DirectedGraph;
import us.fatehi.utility.graph.TarjanStronglyConnectedComponentFinder;
import us.fatehi.utility.property.PropertyName;

public class LinterProviderTableCycles extends BaseLinterProvider {

  private static final long serialVersionUID = -7901644028908017034L;

  public LinterProviderTableCycles() {
    super(LinterTableCycles.class.getName());
  }

  @Override
  public Linter newLinter() {
    return new LinterTableCycles(getPropertyName());
  }
}

class LinterTableCycles extends BaseLinter {

  private DirectedGraph<Table> tablesGraph;

  LinterTableCycles(final PropertyName propertyName) {
    super(propertyName);
  }

  @Override
  public String getSummary() {
    return "cycles in table relationships";
  }

  @Override
  protected void end(final Connection connection) {
    requireNonNull(tablesGraph, "Not initialized");

    final Collection<List<Table>> sccs =
        new TarjanStronglyConnectedComponentFinder<>(tablesGraph).detectCycles();
    if (!sccs.isEmpty()) {
      for (final List<Table> list : sccs) {
        addCatalogLint(getSummary(), new ArrayList<>(list));
      }
    }

    tablesGraph = null;

    super.end(connection);
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    requireNonNull(table, "No table provided");
    requireNonNull(tablesGraph, "Not initialized");

    tablesGraph.addVertex(table);
    for (final ForeignKey foreignKey : table.getForeignKeys()) {
      // Add edges for tables that are limited using limit options
      // That is, do not consider partial tables which are excluded by the limit
      final Table pkTable = foreignKey.getPrimaryKeyTable();
      final Table fkTable = foreignKey.getForeignKeyTable();
      if (!(pkTable instanceof PartialDatabaseObject)
          && !(fkTable instanceof PartialDatabaseObject)) {
        tablesGraph.addEdge(pkTable, fkTable);
      }
    }
  }

  @Override
  protected void start(final Connection connection) {
    super.start(connection);

    tablesGraph = new DirectedGraph<>(getLinterId());
  }
}

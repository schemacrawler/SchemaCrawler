/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.crawl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import java.util.logging.Logger;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import us.fatehi.utility.graph.DirectedGraph;
import us.fatehi.utility.graph.GraphException;
import us.fatehi.utility.graph.SimpleTopologicalSort;

final class TablesGraph extends DirectedGraph<Table> {

  private static final Logger LOGGER = Logger.getLogger(TablesGraph.class.getName());

  TablesGraph(final NamedObjectList<MutableTable> tables) {
    super("catalog");

    if (tables == null) {
      return;
    }

    for (final Table table : tables) {
      addVertex(table);
      for (final ForeignKey foreignKey : table.getForeignKeys()) {
        for (final ColumnReference columnRef : foreignKey) {
          addEdge(
              columnRef.getPrimaryKeyColumn().getParent(),
              columnRef.getForeignKeyColumn().getParent());
        }
      }
    }
  }

  /** Set the sort order for tables and views. */
  void setTablesSortIndexes() {
    try {
      final List<Table> sortedTables = topologicalSort();
      final List<View> sortedViews = new ArrayList<>();
      int sortIndex = 0;
      for (final Table table : sortedTables) {
        if (table instanceof View) {
          sortedViews.add((View) table);
        } else if (table instanceof MutableTable) {
          ((MutableTable) table).setSortIndex(sortIndex);
          sortIndex++;
        }
      }
      for (final View view : sortedViews) {
        if (view instanceof MutableView) {
          ((MutableView) view).setSortIndex(sortIndex);
          sortIndex++;
        }
      }
    } catch (final GraphException e) {
      LOGGER.log(Level.CONFIG, e.getMessage());
    }
  }

  private List<Table> topologicalSort() throws GraphException {
    return new SimpleTopologicalSort<>(this).topologicalSort();
  }
}

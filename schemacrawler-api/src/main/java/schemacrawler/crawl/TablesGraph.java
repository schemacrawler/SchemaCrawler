/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static schemacrawler.utility.MetaDataUtility.isView;

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
        if (isView(table)) {
          sortedViews.add((View) table);
        } else if (table instanceof MutableTable mutableTable) {
          mutableTable.setSortIndex(sortIndex);
          sortIndex++;
        }
      }
      for (final View view : sortedViews) {
        if (view instanceof MutableView mutableView) {
          mutableView.setSortIndex(sortIndex);
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

package schemacrawler.crawl;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.utility.DirectedGraph;
import schemacrawler.utility.GraphException;

final class TablesGraph
  extends DirectedGraph<MutableTable>
{

  private static final Logger LOGGER = Logger.getLogger(TablesGraph.class
    .getName());

  TablesGraph(final NamedObjectList<MutableTable> tables)
  {
    if (tables == null)
    {
      return;
    }

    for (final MutableTable table: tables)
    {
      addVertex(table);
      final ForeignKey[] foreignKeys = table.getForeignKeys();
      for (final ForeignKey foreignKey: foreignKeys)
      {
        final ForeignKeyColumnMap[] columnPairs = foreignKey.getColumnPairs();
        for (final ForeignKeyColumnMap columnPair: columnPairs)
        {
          addDirectedEdge((MutableTable) columnPair.getPrimaryKeyColumn()
            .getParent(), (MutableTable) columnPair.getForeignKeyColumn()
            .getParent());
        }
      }
    }

  }

  /**
   * Set the sort order for tables and views.
   */
  void setTablesSortIndices()
  {
    try
    {
      final List<MutableTable> sortedTables = topologicalSort();
      final List<MutableView> sortedViews = new ArrayList<MutableView>();
      int sortIndex = 0;
      for (final MutableTable table: sortedTables)
      {
        if (table instanceof MutableView)
        {
          sortedViews.add((MutableView) table);
        }
        else
        {
          table.setSortIndex(sortIndex);
          sortIndex++;
        }
      }
      for (final MutableView view: sortedViews)
      {
        view.setSortIndex(sortIndex);
        sortIndex++;
      }
    }
    catch (GraphException e)
    {
      LOGGER.log(Level.CONFIG, e.getMessage());
    }
  }

}

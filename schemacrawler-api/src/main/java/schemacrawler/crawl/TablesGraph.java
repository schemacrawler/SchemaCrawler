package schemacrawler.crawl;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import sf.util.GraphException;
import sf.util.graph.DirectedGraph;

final class TablesGraph
  extends DirectedGraph<Table>
{

  private static final Logger LOGGER = Logger.getLogger(TablesGraph.class
    .getName());

  TablesGraph(final NamedObjectList<MutableTable> tables)
  {
    if (tables == null)
    {
      return;
    }

    for (final Table table: tables)
    {
      addVertex(table);
      for (final ForeignKey foreignKey: table.getForeignKeys())
      {
        for (final ForeignKeyColumnReference columnRef: foreignKey)
        {
          addDirectedEdge(columnRef.getPrimaryKeyColumn().getParent(),
                          columnRef.getForeignKeyColumn().getParent());
        }
      }
    }

  }

  /**
   * Set the sort order for tables and views.
   */
  void setTablesSortIndexes()
  {
    try
    {
      final List<Table> sortedTables = topologicalSort();
      final List<View> sortedViews = new ArrayList<>();
      int sortIndex = 0;
      for (final Table table: sortedTables)
      {
        if (table instanceof View)
        {
          sortedViews.add((View) table);
        }
        else if (table instanceof MutableTable)
        {
          ((MutableTable) table).setSortIndex(sortIndex);
          sortIndex++;
        }
      }
      for (final View view: sortedViews)
      {
        if (view instanceof MutableView)
        {
          ((MutableView) view).setSortIndex(sortIndex);
          sortIndex++;
        }
      }
    }
    catch (final GraphException e)
    {
      LOGGER.log(Level.CONFIG, e.getMessage());
    }
  }

}

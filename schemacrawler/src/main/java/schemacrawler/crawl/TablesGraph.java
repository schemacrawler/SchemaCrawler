package schemacrawler.crawl;


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

  void setTablesSortIndices()
  {
    // Set the sort order
    try
    {
      final List<MutableTable> sortedTables = topologicalSort();
      for (int i = 0; i < sortedTables.size(); i++)
      {
        sortedTables.get(i).setSortIndex(i);
      }
    }
    catch (GraphException e)
    {
      LOGGER.log(Level.CONFIG, e.getMessage());
    }
  }

}

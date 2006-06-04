/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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

package schemacrawler.tools.integration.jung;


import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.ISOMLayout;
import edu.uci.ics.jung.visualization.Layout;

/**
 * JUNG utilities for SchemaCrawler.
 * 
 * @author Sualeh Fatehi
 */
public final class JungUtil
{

  /**
   * Prevent instantiation.
   */
  private JungUtil()
  {

  }

  /**
   * Saves a schema graph as a JPEG file.
   * 
   * @param graph
   *          Schema graph
   * @param file
   *          Output JPEG file
   * @param size
   *          Image size
   * @throws IOException
   *           On an exception
   */
  public static void saveGraphJpeg(final Graph graph, final File file,
                                   final Dimension size)
    throws IOException
  {
    Layout layout = new ISOMLayout(graph);
    JpgVisualizationViewer vv = new JpgVisualizationViewer(layout);
    vv.save(file, size);
  }

  /**
   * Turns the schema into a directed graph.
   * 
   * @param schema
   *          Schema
   * @return Graph
   */
  public static Graph makeSchemaGraph(final Schema schema)
  {
    Graph graph = new DirectedSparseGraph();
    Map verticesMap = new HashMap();
    final Table[] tables = schema.getTables();
    mapTablesAndColumns(graph, tables, verticesMap);
    mapForeignKeys(graph, tables, verticesMap);
    return graph;
  }

  private static void mapForeignKeys(final Graph graph, final Table[] tables,
                                     final Map verticesMap)
  {
    // Make edges for each foreign key
    Map columnPairMap = new HashMap();
    for (int i = 0; i < tables.length; i++)
    {
      final Table table = tables[i];
      final ForeignKey[] foreignKeys = table.getForeignKeys();
      for (int j = 0; j < foreignKeys.length; j++)
      {
        ForeignKey foreignKey = foreignKeys[j];
        final ForeignKeyColumnMap[] columnPairs = foreignKey.getColumnPairs();
        for (int k = 0; k < columnPairs.length; k++)
        {
          ForeignKeyColumnMap columnPair = columnPairs[k];
          if (!columnPairMap.containsKey(columnPair.getFullName()))
          {
            columnPairMap.put(columnPair.getFullName(), columnPair);
            Column fromColumn = (Column) columnPair.getPrimaryKeyColumn();
            Column toColumn = (Column) columnPair.getForeignKeyColumn();
            ColumnVertex fromColumnVertex = (ColumnVertex) verticesMap
              .get(fromColumn);
            ColumnVertex toColumnVeretx = (ColumnVertex) verticesMap
              .get(toColumn);
            final ForeignKeyEdge foreignKeyEdge = new ForeignKeyEdge(
                fromColumnVertex, toColumnVeretx);
            graph.addEdge(foreignKeyEdge);
          }
        }
      }
    }
  }

  private static void mapTablesAndColumns(final Graph graph,
                                          final Table[] tables,
                                          final Map verticesMap)
  {
    for (int i = 0; i < tables.length; i++)
    {
      final Table table = tables[i];
      final TableVertex tableVertex = new TableVertex(table);
      graph.addVertex(tableVertex);
      verticesMap.put(table, tableVertex);
      final Column[] columns = table.getColumns();
      for (int j = 0; j < columns.length; j++)
      {
        final Column column = columns[j];
        final ColumnVertex columnVertex = new ColumnVertex(column);
        graph.addVertex(columnVertex);
        verticesMap.put(column, columnVertex);
        final ColumnEdge columnEdge = new ColumnEdge(tableVertex, columnVertex);
        graph.addEdge(columnEdge);
      }
    }
  }

}

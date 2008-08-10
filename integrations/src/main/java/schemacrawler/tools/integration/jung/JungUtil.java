/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.NamedObject;
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
   * Turns the schema into a directed graph.
   * 
   * @param schema
   *        Schema
   * @return Graph
   */
  public static Graph makeSchemaGraph(final Catalog catalog)
  {
    final Graph graph = new DirectedSparseGraph();
    final Map<NamedObject, SchemaGraphVertex> verticesMap = new HashMap<NamedObject, SchemaGraphVertex>();

    final List<Table> tablesList = new ArrayList<Table>();
    if (catalog != null)
    {
      for (Schema schema: catalog.getSchemas())
      {
        tablesList.addAll(Arrays.asList(schema.getTables()));
      }
    }

    final Table[] tables = tablesList.toArray(new Table[0]);
    mapTablesAndColumns(graph, tables, verticesMap);
    mapForeignKeys(graph, tables, verticesMap);
    return graph;
  }

  private static void mapForeignKeys(final Graph graph,
                                     final Table[] tables,
                                     final Map<NamedObject, SchemaGraphVertex> verticesMap)
  {
    final Map<String, ForeignKeyColumnMap> columnPairMap = new HashMap<String, ForeignKeyColumnMap>();
    for (final Table table: tables)
    {
      for (final ForeignKey foreignKey: table.getForeignKeys())
      {
        for (final ForeignKeyColumnMap columnPair: foreignKey.getColumnPairs())
        {
          if (!columnPairMap.containsKey(columnPair.getFullName()))
          {
            columnPairMap.put(columnPair.getFullName(), columnPair);
            final Column fromColumn = columnPair.getPrimaryKeyColumn();
            final Column toColumn = columnPair.getForeignKeyColumn();
            final ColumnVertex fromColumnVertex = (ColumnVertex) verticesMap
              .get(fromColumn);
            final ColumnVertex toColumnVertex = (ColumnVertex) verticesMap
              .get(toColumn);
            final ForeignKeyEdge foreignKeyEdge = new ForeignKeyEdge(fromColumnVertex,
                                                                     toColumnVertex);
            graph.addEdge(foreignKeyEdge);
          }
        }
      }
    }
  }

  private static void mapTablesAndColumns(final Graph graph,
                                          final Table[] tables,
                                          final Map<NamedObject, SchemaGraphVertex> verticesMap)
  {
    for (final Table table: tables)
    {
      final TableVertex tableVertex = new TableVertex(table);
      graph.addVertex(tableVertex);
      verticesMap.put(table, tableVertex);
      for (final Column column: table.getColumns())
      {
        final ColumnVertex columnVertex = new ColumnVertex(column);
        graph.addVertex(columnVertex);
        verticesMap.put(column, columnVertex);
        final ColumnEdge columnEdge = new ColumnEdge(tableVertex, columnVertex);
        graph.addEdge(columnEdge);
      }
    }
  }

  /**
   * Saves a schema graph as a JPEG file.
   * 
   * @param graph
   *        Schema graph
   * @param file
   *        Output JPEG file
   * @param size
   *        Image size
   * @throws IOException
   *         On an exception
   */
  public static void saveGraphJpeg(final Graph graph,
                                   final File file,
                                   final Dimension size)
    throws IOException
  {
    final Layout layout = new ISOMLayout(graph);
    final JpgVisualizationViewer vv = new JpgVisualizationViewer(layout);
    vv.save(file, size);
  }

  /**
   * Prevent instantiation.
   */
  private JungUtil()
  {

  }

}

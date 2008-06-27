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


import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;

import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.ConstantVertexFontFunction;
import edu.uci.ics.jung.graph.decorators.EdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.VertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.VertexStringer;
import edu.uci.ics.jung.visualization.PluggableRenderer;

final class SchemaCrawlerRenderer
  extends PluggableRenderer
{

  private static final class SchemaCrawlerEdgePaintFunction
    implements EdgePaintFunction
  {
    /**
     * {@inheritDoc}
     */
    public Paint getDrawPaint(final Edge edge)
    {
      return ((SchemaGraphEdge) edge).getDrawPaint();
    }

    /**
     * {@inheritDoc}
     */
    public Paint getFillPaint(final Edge edge)
    {
      return ((SchemaGraphEdge) edge).getFillPaint();
    }
  }

  private static final class SchemaCrawlerVertexPaintFunction
    implements VertexPaintFunction
  {
    /**
     * {@inheritDoc}
     */
    public Paint getDrawPaint(final Vertex vertex)
    {
      return ((SchemaGraphVertex) vertex).getDrawPaint();
    }

    /**
     * {@inheritDoc}
     */
    public Paint getFillPaint(final Vertex vertex)
    {
      return ((SchemaGraphVertex) vertex).getFillPaint();
    }
  }

  private static final class SchemaCrawlerVertexShapeFunction
    implements VertexShapeFunction
  {
    /**
     * {@inheritDoc}
     */
    public Shape getShape(final Vertex vertex)
    {
      return ((SchemaGraphVertex) vertex).getShape();
    }
  }

  private static final class SchemaCrawlerVertexStringer
    implements VertexStringer
  {
    /**
     * {@inheritDoc}
     */
    public String getLabel(final ArchetypeVertex vertex)
    {
      return ((SchemaGraphVertex) vertex).getLabel();
    }
  }

  private static final int FONT_SIZE = 9;

  /**
   * Constructs a new SchemaCrawler renderer, with customizations.
   */
  public SchemaCrawlerRenderer()
  {
    vertexStringer = new SchemaCrawlerVertexStringer();
    vertexPaintFunction = new SchemaCrawlerVertexPaintFunction();
    vertexShapeFunction = new SchemaCrawlerVertexShapeFunction();
    vertexFontFunction = new ConstantVertexFontFunction(new Font("Helvetica",
                                                                 Font.PLAIN,
                                                                 FONT_SIZE));
    edgePaintFunction = new SchemaCrawlerEdgePaintFunction();
  }

}

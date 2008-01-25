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


import java.awt.Paint;
import java.awt.Shape;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.ConstantVertexAspectRatioFunction;
import edu.uci.ics.jung.graph.decorators.ConstantVertexSizeFunction;
import edu.uci.ics.jung.visualization.VertexShapeFactory;

interface SchemaGraphVertex
  extends Vertex
{

  /** Vertex shape factory */
  VertexShapeFactory VERTEX_SHAPE_FACTORY = new VertexShapeFactory(new ConstantVertexSizeFunction(10),
                                                                   new ConstantVertexAspectRatioFunction(0.8f));

  /**
   * Gets the drawing paint object.
   * 
   * @return Drawing paint object
   */
  Paint getDrawPaint();

  /**
   * Gets the fill paint object.
   * 
   * @return Fill paint object
   */
  Paint getFillPaint();

  /**
   * Gets the label.
   * 
   * @return Label
   */
  String getLabel();

  /**
   * Gets the shape.
   * 
   * @return Shape
   */
  Shape getShape();

}

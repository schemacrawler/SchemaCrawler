/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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


import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;

import schemacrawler.schema.Column;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

final class ColumnVertex
  extends DirectedSparseVertex
  implements SchemaGraphVertex
{

  private final Column column;

  ColumnVertex(final Column namedObject)
  {
    column = namedObject;
  }

  /**
   * {@inheritDoc}
   */
  public String getLabel()
  {
    return column.getName();
  }

  /**
   * {@inheritDoc}
   */
  public Paint getFillPaint()
  {
    if (column.isPartOfPrimaryKey())
    {
      return Color.GREEN.darker();
    }
    else
    {
      return Color.BLUE.darker();
    }
  }

  /**
   * {@inheritDoc}
   */
  public Paint getDrawPaint()
  {
    return Color.GRAY;
  }

  /**
   * {@inheritDoc}
   */
  public Shape getShape()
  {
    return VERTEX_SHAPE_FACTORY.getEllipse(this);
  }

}

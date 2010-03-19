/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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
package schemacrawler.tools.integration.graph;


import java.awt.Color;
import java.io.Serializable;

public final class PastelColor
  implements Serializable
{

  private static final long serialVersionUID = 7256039994498918504L;

  private static final double FACTOR = 0.87;

  private static int colorValue()
  {
    final int colorBase = 180;
    return (int) (Math.random() * (255 - colorBase) + colorBase);
  }

  private final Color color;

  public PastelColor()
  {
    color = new Color(colorValue(), colorValue(), colorValue());
  }

  private PastelColor(final Color color)
  {
    this.color = color;
  }

  public Color getColor()
  {
    return color;
  }

  public PastelColor shade()
  {
    return new PastelColor(new Color(Math.max((int) (color.getRed() * FACTOR),
                                              0), Math.max((int) (color
      .getGreen() * FACTOR), 0), Math.max((int) (color.getBlue() * FACTOR), 0)));
  }

  public PastelColor tint()
  {
    return new PastelColor(new Color(Math.min((int) (color.getRed() / FACTOR),
                                              255), Math.min((int) (color
      .getGreen() / FACTOR), 255), Math.min((int) (color.getBlue() / FACTOR),
                                            255)));
  }

  @Override
  public String toString()
  {
    return "#" + Integer.toHexString(color.getRGB()).substring(2).toUpperCase();
  }

}

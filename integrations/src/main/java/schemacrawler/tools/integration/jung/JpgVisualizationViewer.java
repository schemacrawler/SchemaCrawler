/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;

final class JpgVisualizationViewer
  extends VisualizationViewer
{

  private static final long serialVersionUID = 1600814850469256520L;

  /**
   * Constructor with a viewer with a SchemaCrawler renderer.
   * 
   * @param layout
   *        Layout
   */
  public JpgVisualizationViewer(final Layout layout)
  {
    super(layout, new SchemaCrawlerRenderer());
  }

  /**
   * Saves the graph as an image.
   * 
   * @param file
   *        Image file name
   * @param size
   *        Image size
   * @throws IOException
   *         On an exception
   */
  public void save(final File file, final Dimension size)
    throws IOException
  {

    final BufferedImage bufferedImage = new BufferedImage(size.width,
                                                          size.height,
                                                          BufferedImage.TYPE_INT_RGB);

    // use double buffering until now
    // turn it off to capture
    setDoubleBuffered(false);

    // Create a graphics contents on the buffered image
    final Graphics2D g2d = bufferedImage.createGraphics();

    // Draw graphics
    g2d.setColor(Color.WHITE);
    g2d.fillRect(0, 0, size.width, size.height);

    paintComponent(g2d);

    // Save the BufferedImage
    file.getAbsoluteFile().getParentFile().mkdirs();
    ImageIO.write(bufferedImage, "jpg", file);

    // turn double buffering back on
    setDoubleBuffered(true);

  }

}

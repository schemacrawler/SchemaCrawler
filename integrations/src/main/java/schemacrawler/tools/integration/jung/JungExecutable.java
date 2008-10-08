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

import javax.sql.DataSource;

import schemacrawler.schema.Catalog;
import schemacrawler.tools.integration.SchemaExecutable;
import edu.uci.ics.jung.graph.Graph;

/**
 * Main executor for the JUNG integration.
 * 
 * @author Sualeh Fatehi
 */
public final class JungExecutable
  extends SchemaExecutable
{

  private static final int DEFAULT_IMAGE_WIDTH = 600;

  protected void doExecute(final DataSource dataSource)
    throws Exception
  {
    Catalog catalog = getCatalog(dataSource);
    final File outputFile = toolOptions.getOutputOptions().getOutputFile();
    final Dimension size = getSize(toolOptions.getOutputOptions()
      .getOutputFormatValue());
    final Graph graph = JungUtil.makeSchemaGraph(catalog);
    JungUtil.saveGraphJpeg(graph, outputFile, size);
  }

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   * @throws Exception
   *         On an exception
   */
  public void main(final String[] args)
    throws Exception
  {
    executeOnSchema(args, "/schemacrawler-jung-readme.txt");
  }

  private Dimension getSize(final String dimensions)
  {
    final String[] sizes = dimensions.split("x");
    try
    {
      final int width = Integer.parseInt(sizes[0]);
      final int height = Integer.parseInt(sizes[1]);
      return new Dimension(width, height);
    }
    catch (final NumberFormatException e)
    {
      return new Dimension(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_WIDTH);
    }
  }

}

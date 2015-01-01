/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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
package schemacrawler.tools.options;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleOutputResource
  implements OutputResource
{

  private static final Logger LOGGER = Logger
    .getLogger(ConsoleOutputResource.class.getName());

  @Override
  public String getDescription()
  {
    return "<console>";
  }

  @Override
  public Writer openOutputWriter(final Charset charset,
                                 final boolean appendOutput)
    throws IOException
  {
    final Writer writer = new BufferedWriter(new OutputStreamWriter(System.out));
    LOGGER.log(Level.INFO, "Opened output writer to console");
    return writer;
  }

  @Override
  public boolean shouldCloseWriter()
  {
    return false;
  }

  @Override
  public String toString()
  {
    return getDescription();
  }

}

/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
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
package schemacrawler.tools.iosource;


import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReaderInputResource
  implements InputResource
{

  private static final Logger LOGGER = Logger
    .getLogger(ReaderInputResource.class.getName());

  private final Reader reader;

  public ReaderInputResource(final Reader reader)
  {
    this.reader = requireNonNull(reader, "No reader provided");
  }

  @Override
  public String getDescription()
  {
    return "<reader>";
  }

  @Override
  public Reader openInputReader(final Charset charset)
    throws IOException
  {
    LOGGER.log(Level.INFO, "Input to provided reader");
    return new BufferedReader(reader);
  }

  @Override
  public boolean shouldCloseReader()
  {
    return false;
  }

  @Override
  public String toString()
  {
    return getDescription();
  }

}

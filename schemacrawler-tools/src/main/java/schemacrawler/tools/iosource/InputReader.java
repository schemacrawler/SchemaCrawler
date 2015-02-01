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
package schemacrawler.tools.iosource;


import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.SchemaCrawlerException;

public class InputReader
  extends Reader
{

  private static final Logger LOGGER = Logger.getLogger(InputReader.class
    .getName());

  private final Reader reader;
  private boolean isClosed;
  private final InputResource inputResource;

  public InputReader(final InputResource inputResource,
                     final Charset inputCharset)
    throws SchemaCrawlerException
  {
    try
    {
      this.inputResource = requireNonNull(inputResource,
                                          "No input resource provided");
      reader = inputResource.openInputReader(inputCharset);
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e);
    }
  }

  @Override
  public void close()
    throws IOException
  {
    if (inputResource.shouldCloseReader())
    {
      LOGGER.log(Level.INFO, "Closing input reader");
      reader.close();
    }
    else
    {
      LOGGER
        .log(Level.INFO,
             "Not closing input reader, since output is to an externally provided reader");
    }

    isClosed = true;
  }

  @Override
  public void mark(final int readAheadLimit)
    throws IOException
  {
    ensureOpen();
    reader.mark(readAheadLimit);
  }

  @Override
  public boolean markSupported()
  {
    return reader.markSupported();
  }

  @Override
  public int read()
    throws IOException
  {
    ensureOpen();
    return reader.read();
  }

  @Override
  public int read(final char[] cbuf)
    throws IOException
  {
    ensureOpen();
    return reader.read(cbuf);
  }

  @Override
  public int read(final char[] cbuf, final int off, final int len)
    throws IOException
  {
    ensureOpen();
    return reader.read(cbuf, off, len);
  }

  @Override
  public int read(final CharBuffer target)
    throws IOException
  {
    ensureOpen();
    return reader.read(target);
  }

  @Override
  public boolean ready()
    throws IOException
  {
    ensureOpen();
    return reader.ready();
  }

  @Override
  public void reset()
    throws IOException
  {
    ensureOpen();
    reader.reset();
  }

  @Override
  public long skip(final long n)
    throws IOException
  {
    ensureOpen();
    return reader.skip(n);
  }

  @Override
  public String toString()
  {
    return inputResource.toString();
  }

  @Override
  protected void finalize()
    throws Throwable
  {
    if (!isClosed)
    {
      throw new IllegalStateException(String.format("Input reader \"%s\" was not closed",
                                                    inputResource
                                                      .getDescription()));
    }
    super.finalize();
  }

  /**
   * Checks to make sure that the stream has not been closed.
   */
  private void ensureOpen()
    throws IOException
  {
    if (isClosed)
    {
      throw new IOException(String.format("Input reader \"%s\" is not open",
                                          inputResource.getDescription()));
    }
  }

}

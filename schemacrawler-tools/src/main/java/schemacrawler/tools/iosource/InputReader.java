/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.tools.iosource;


import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputReader
  extends Reader
{

  private static final Logger LOGGER = Logger
    .getLogger(InputReader.class.getName());

  private final String description;
  private final Reader reader;
  private final boolean shouldCloseReader;
  private boolean isClosed;

  public InputReader(final String description,
                     final Reader reader,
                     final boolean shouldCloseReader)
  {
    this.description = requireNonNull(description, "No description provided");
    this.reader = requireNonNull(reader, "No reader provided");
    this.shouldCloseReader = shouldCloseReader;
  }

  @Override
  public void close()
    throws IOException
  {
    if (shouldCloseReader)
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
    return description;
  }

  @Override
  protected void finalize()
    throws Throwable
  {
    if (!isClosed)
    {
      throw new IllegalStateException(String
        .format("Input reader was not closed <%s>", description));
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
      throw new IOException(String.format("Input reader <%s> is not open",
                                          description));
    }
  }

}

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


import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.SchemaCrawlerException;

public final class OutputWriter
  extends Writer
{

  private final Writer writer;
  private boolean isClosed;
  private final OutputResource outputResource;

  private static final Logger LOGGER = Logger
    .getLogger(ConsoleOutputResource.class.getName());

  public OutputWriter(final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    this(outputOptions, false);
  }

  public OutputWriter(final OutputOptions outputOptions,
                      final boolean appendOutput)
    throws SchemaCrawlerException
  {
    requireNonNull(outputOptions, "No output options provided");
    try
    {
      outputResource = requireNonNull(outputOptions.getOutputResource(),
                                      "No output resource provided");
      writer = outputResource
        .openOutputWriter(outputOptions.getOutputCharset(), appendOutput);
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e);
    }
  }

  @Override
  public Writer append(final char c)
    throws IOException
  {
    ensureOpen();
    return writer.append(c);
  }

  @Override
  public Writer append(final CharSequence csq)
    throws IOException
  {
    ensureOpen();
    return writer.append(csq);
  }

  @Override
  public Writer append(final CharSequence csq, final int start, final int end)
    throws IOException
  {
    ensureOpen();
    return writer.append(csq, start, end);
  }

  @Override
  public void close()
    throws IOException
  {
    flush();

    if (outputResource.shouldCloseWriter())
    {
      LOGGER.log(Level.INFO, "Closing output writer");
      writer.close();
    }
    else
    {
      LOGGER
        .log(Level.INFO,
             "Not closing output writer, since output is to an externally provided writer");
    }

    isClosed = true;
  }

  @Override
  public void flush()
    throws IOException
  {
    ensureOpen();
    writer.flush();
  }

  @Override
  public String toString()
  {
    return outputResource.toString();
  }

  @Override
  public void write(final char[] cbuf)
    throws IOException
  {
    ensureOpen();
    writer.write(cbuf);
  }

  @Override
  public void write(final char[] cbuf, final int off, final int len)
    throws IOException
  {
    ensureOpen();
    writer.write(cbuf, off, len);
  }

  @Override
  public void write(final int c)
    throws IOException
  {
    ensureOpen();
    writer.write(c);
  }

  @Override
  public void write(final String str)
    throws IOException
  {
    ensureOpen();
    writer.write(str);
  }

  @Override
  public void write(final String str, final int off, final int len)
    throws IOException
  {
    ensureOpen();
    writer.write(str, off, len);
  }

  @Override
  protected void finalize()
    throws Throwable
  {
    if (!isClosed)
    {
      throw new IllegalStateException(String.format("Output writer \"%s\" was not closed",
                                                    outputResource
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
      throw new IOException(String.format("Output writer \"%s\" is not open",
                                          outputResource.getDescription()));
    }
  }

}

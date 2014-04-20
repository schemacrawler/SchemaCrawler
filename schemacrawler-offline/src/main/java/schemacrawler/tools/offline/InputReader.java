/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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
package schemacrawler.tools.offline;


import static sf.util.Utility.isBlank;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
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
  private String description;

  public InputReader(final OfflineSnapshotOptions options)
    throws SchemaCrawlerException
  {
    reader = openInputReader(options);
  }

  @Override
  public void close()
    throws IOException
  {
    ensureOpen();

    if (reader != null)
    {
      reader.close();
      LOGGER.log(Level.INFO, "Closed input reader");
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
    super.finalize();
    if (!isClosed)
    {
      throw new IllegalStateException("Input reader was not closed");
    }
  }

  /**
   * Checks to make sure that the stream has not been closed.
   */
  private void ensureOpen()
    throws IOException
  {
    if (isClosed)
    {
      throw new IOException("Reader has already been closed");
    }
  }

  private Reader openInputReader(final OfflineSnapshotOptions options)
    throws SchemaCrawlerException
  {
    try
    {
      if (options.hasReader())
      {
        final Reader reader = options.getReader();
        description = "<reader>";
        LOGGER.log(Level.INFO, "Reading from provided reader");
        return reader;
      }

      final String inputSource = options.getInputSource();
      final File inputFile;
      if (!isBlank(inputSource))
      {
        inputFile = new File(inputSource);
      }
      else if (options.hasInputFile())
      {
        inputFile = options.getInputFile();
      }
      else
      {
        throw new SchemaCrawlerException("Offline snapshot file not provided");
      }

      final InputStream inputStream;
      if (inputFile != null && inputFile.exists() && inputFile.canRead())
      {
        inputStream = new FileInputStream(inputFile);
        description = inputFile.getAbsolutePath();
        LOGGER.log(Level.INFO, "Reading from " + description);
      }
      else
      {
        final String resource = "/" + inputSource;
        inputStream = InputReader.class.getResourceAsStream(resource);
        if (inputStream == null)
        {
          throw new SchemaCrawlerException("Cannot load " + inputSource);
        }
        description = InputReader.class.getResource(resource).toExternalForm();
        LOGGER.log(Level.INFO, "Reading from " + description);
      }
      final Reader reader = new InputStreamReader(inputStream,
                                                  options.getInputCharset());
      return reader;
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not obtain input reader", e);
    }
  }

}

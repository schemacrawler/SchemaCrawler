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


import static java.nio.file.Files.exists;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.newInputStream;
import static sf.util.Utility.isBlank;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

    if (reader != null && !isClosed)
    {
      reader.close();
      LOGGER.log(Level.INFO, "Closed input reader, " + description);
    }
    else
    {
      LOGGER.log(Level.INFO, String
        .format("Input reader \"%s\" is already closed", description));
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
      throw new IllegalStateException(String.format("Input reader \"%s\" was not closed",
                                                    description));
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
      throw new IOException(String.format("Input reader \"%s\" has been closed",
                                          description));
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
      final Path inputFile;
      if (!isBlank(inputSource))
      {
        inputFile = Paths.get(inputSource).normalize().toAbsolutePath();
      }
      else if (options.hasInputFile())
      {
        inputFile = options.getInputFile().normalize().toAbsolutePath();
      }
      else
      {
        throw new SchemaCrawlerException("Offline snapshot file not provided");
      }

      final InputStream inputStream;
      if (inputFile != null && exists(inputFile) && isReadable(inputFile))
      {
        inputStream = newInputStream(inputFile, StandardOpenOption.READ);
        description = inputFile.toString();
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

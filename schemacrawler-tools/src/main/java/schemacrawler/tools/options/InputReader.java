/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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
import schemacrawler.tools.integration.scripting.ScriptExecutable;

public class InputReader
  extends Reader
{

  private static final Logger LOGGER = Logger.getLogger(InputReader.class
    .getName());

  private final Reader reader;
  private boolean isClosed;
  private boolean isFileOutput;

  public InputReader(final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    reader = openInputReader(outputOptions);
  }

  @Override
  public void close()
    throws IOException
  {
    ensureOpen();

    if (isFileOutput)
    {
      if (reader != null)
      {
        reader.close();
        LOGGER.log(Level.INFO, "Closed input reader");
      }
    }
    else
    {
      LOGGER.log(Level.INFO,
                 "Not closing input reader, since input is not from a file");
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

  private Reader openInputReader(final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    try
    {
      final String scriptFileName = outputOptions.getOutputFormatValue();
      if (outputOptions.getOutputFormat() == OutputFormat.text)
      {
        throw new SchemaCrawlerException("No script file provided");
      }
      final File scriptFile = new File(scriptFileName);

      final InputStream inputStream;
      if (scriptFile.exists() && scriptFile.canRead())
      {
        inputStream = new FileInputStream(scriptFile);
      }
      else
      {
        inputStream = ScriptExecutable.class
          .getResourceAsStream("/" + scriptFileName);
        if (inputStream == null)
        {
          throw new SchemaCrawlerException("Cannot load script, "
                                           + scriptFileName);
        }
      }
      Reader reader = new InputStreamReader(inputStream,
                                            outputOptions.getInputCharset());
      return reader;
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not obtain input reader", e);
    }
  }

}

/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.SchemaCrawlerException;

public final class OutputWriter
  extends Writer
{

  private static final Logger LOGGER = Logger.getLogger(OutputWriter.class
    .getName());

  private final Writer writer;
  private final File outputFile;

  public OutputWriter(final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    if (outputOptions != null)
    {
      outputFile = outputOptions.getOutputFile();
    }
    else
    {
      outputFile = null;
    }

    writer = openOutputWriter(outputOptions);
  }

  @Override
  public Writer append(final char c)
    throws IOException
  {
    return writer.append(c);
  }

  @Override
  public Writer append(final CharSequence csq)
    throws IOException
  {
    return writer.append(csq);
  }

  @Override
  public Writer append(final CharSequence csq, final int start, final int end)
    throws IOException
  {
    return writer.append(csq, start, end);
  }

  @Override
  public void close()
    throws IOException
  {
    if (writer != null)
    {
      writer.flush();
    }

    if (outputFile != null)
    {
      if (writer != null)
      {
        writer.close();
        LOGGER.log(Level.INFO,
                   "Closed output writer to file, "
                       + outputFile.getAbsolutePath());
      }
    }
    else
    {
      LOGGER.log(Level.INFO,
                 "Not closing output writer, since output is to console");
    }
  }

  @Override
  public boolean equals(final Object obj)
  {
    return writer.equals(obj);
  }

  @Override
  public void flush()
    throws IOException
  {
    writer.flush();
  }

  @Override
  public int hashCode()
  {
    return writer.hashCode();
  }

  @Override
  public String toString()
  {
    return writer.toString();
  }

  @Override
  public void write(final char[] cbuf)
    throws IOException
  {
    writer.write(cbuf);
  }

  @Override
  public void write(final char[] cbuf, final int off, final int len)
    throws IOException
  {
    writer.write(cbuf, off, len);
  }

  @Override
  public void write(final int c)
    throws IOException
  {
    writer.write(c);
  }

  @Override
  public void write(final String str)
    throws IOException
  {
    writer.write(str);
  }

  @Override
  public void write(final String str, final int off, final int len)
    throws IOException
  {
    writer.write(str, off, len);
  }

  /**
   * Opens the output writer.
   * 
   * @return Writer
   * @throws SchemaCrawlerException
   *         On an exception
   */
  private Writer openOutputWriter(final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    try
    {
      final Writer writer;
      if (outputFile == null)
      {
        writer = new PrintWriter(System.out, /* autoFlush */true);
        LOGGER.log(Level.INFO, "Opened output writer to console");
      }
      else
      {
        final FileWriter fileWriter = new FileWriter(outputFile,
                                                     outputOptions
                                                       .isAppendOutput());
        writer = new PrintWriter(new BufferedWriter(fileWriter), /* autoFlush */
        true);
        LOGGER.log(Level.INFO,
                   "Opened output writer to file, "
                       + outputFile.getAbsolutePath());
      }
      return writer;
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not obtain output writer", e);
    }
  }

}

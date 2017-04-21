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
package schemacrawler.test.utility;


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.Files.size;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Objects.requireNonNull;
import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

import sf.util.IOUtility;

public class TestWriter
  extends Writer
{

  private final Path tempFile;
  private final PrintWriter out;
  private final String outputformat;
  private final boolean isCompressed;

  public TestWriter(final String outputformat)
    throws IOException
  {
    this(outputformat, false);
  }

  public TestWriter(final String outputformat, final boolean isCompressed)
    throws IOException
  {
    this.outputformat = requireNonNull(outputformat);
    tempFile = IOUtility
      .createTempFilePath("schemacrawler",
                          outputformat.replaceAll("[/\\\\]", ""));
    out = openOutputWriter(tempFile, UTF_8, isCompressed);
    this.isCompressed = isCompressed;
  }

  @Override
  public PrintWriter append(final char c)
  {
    return out.append(c);
  }

  @Override
  public PrintWriter append(final CharSequence csq)
  {
    return out.append(csq);
  }

  @Override
  public PrintWriter append(final CharSequence csq,
                            final int start,
                            final int end)
  {
    return out.append(csq, start, end);
  }

  public void assertEmpty()
    throws Exception
  {
    out.close();

    if (size(tempFile) > 0)
    {
      fail("Output is not empty");
    }
  }

  public void assertEquals(final String referenceFile)
    throws Exception
  {
    final List<String> failures = collectFailures(referenceFile);
    if (!failures.isEmpty())
    {
      fail(failures.toString());
    }
  }

  public boolean checkError()
  {
    return out.checkError();
  }

  @Override
  public void close()
    throws IOException
  {
    out.close();

    deleteIfExists(tempFile);
  }

  public List<String> collectFailures(final String referenceFile)
    throws Exception
  {
    out.close();

    final List<String> failures = compareOutput(requireNonNull(referenceFile),
                                                tempFile,
                                                outputformat,
                                                isCompressed);
    return failures;
  }

  @Override
  public boolean equals(final Object obj)
  {
    if (obj == null || !(obj instanceof Writer))
    {
      return false;
    }
    final Writer writer = (Writer) obj;
    return writer.equals(out);
  }

  @Override
  public void flush()
  {
    out.flush();
  }

  public PrintWriter format(final Locale l,
                            final String format,
                            final Object... args)
  {
    return out.format(l, format, args);
  }

  public PrintWriter format(final String format, final Object... args)
  {
    return out.format(format, args);
  }

  @Override
  public int hashCode()
  {
    return out.hashCode();
  }

  public void print(final boolean b)
  {
    out.print(b);
  }

  public void print(final char c)
  {
    out.print(c);
  }

  public void print(final char[] s)
  {
    out.print(s);
  }

  public void print(final double d)
  {
    out.print(d);
  }

  public void print(final float f)
  {
    out.print(f);
  }

  public void print(final int i)
  {
    out.print(i);
  }

  public void print(final long l)
  {
    out.print(l);
  }

  public void print(final Object obj)
  {
    out.print(obj);
  }

  public void print(final String s)
  {
    out.print(s);
  }

  public PrintWriter printf(final Locale l,
                            final String format,
                            final Object... args)
  {
    return out.printf(l, format, args);
  }

  public PrintWriter printf(final String format, final Object... args)
  {
    return out.printf(format, args);
  }

  public void println()
  {
    out.println();
  }

  public void println(final boolean x)
  {
    out.println(x);
  }

  public void println(final char x)
  {
    out.println(x);
  }

  public void println(final char[] x)
  {
    out.println(x);
  }

  public void println(final double x)
  {
    out.println(x);
  }

  public void println(final float x)
  {
    out.println(x);
  }

  public void println(final int x)
  {
    out.println(x);
  }

  public void println(final long x)
  {
    out.println(x);
  }

  public void println(final Object x)
  {
    out.println(x);
  }

  public void println(final String x)
  {
    out.println(x);
  }

  @Override
  public String toString()
  {
    return tempFile.toString();
  }

  @Override
  public void write(final char[] buf)
  {
    out.write(buf);
  }

  @Override
  public void write(final char[] buf, final int off, final int len)
  {
    out.write(buf, off, len);
  }

  @Override
  public void write(final int c)
  {
    out.write(c);
  }

  @Override
  public void write(final String s)
  {
    out.write(s);
  }

  @Override
  public void write(final String s, final int off, final int len)
  {
    out.write(s, off, len);
  }

  private PrintWriter openOutputWriter(final Path tempFile,
                                       final Charset charset,
                                       final boolean isCompressed)
    throws IOException
  {
    final OpenOption[] openOptions = new OpenOption[] {
                                                        WRITE,
                                                        CREATE,
                                                        TRUNCATE_EXISTING };
    final Writer writer;
    if (isCompressed)
    {
      final OutputStream fileStream = newOutputStream(tempFile, openOptions);
      writer = new OutputStreamWriter(new GZIPOutputStream(fileStream, true),
                                      charset);
    }
    else
    {
      writer = newBufferedWriter(tempFile, charset, openOptions);
    }

    return new PrintWriter(writer);
  }

}

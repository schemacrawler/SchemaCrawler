/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Objects;

public final class TestWriter extends Writer implements TestOutputCapture {

  private static final String lineSeparator = System.getProperty("line.separator");

  private final TestOutputStream out;

  public TestWriter() {
    out = new TestOutputStream();
  }

  @Override
  public void close() {
    try {
      out.close();
    } catch (final IOException e) {
      throw new RuntimeException("Could not close test writer", e);
    }
  }

  @Override
  public void flush() throws IOException {
    out.flush();
  }

  @Override
  public String getContents() {
    return out.getContents();
  }

  @Override
  public Path getFilePath() {
    return out.getFilePath();
  }

  public void println() {
    writeout(lineSeparator);
  }

  public void println(final Object x) {
    println(Objects.toString(x));
  }

  public void println(final String x) {
    writeout(x);
    println();
  }

  @Override
  public String toString() {
    return out.toString();
  }

  @Override
  public void write(final char[] cbuf, final int off, final int len) throws IOException {
    writeout(new String(cbuf, off, len));
  }

  private void writeout(final String x) {
    try {
      out.write(Objects.toString(x).getBytes(UTF_8));
    } catch (final IOException e) {
      // Ignore
    }
  }
}

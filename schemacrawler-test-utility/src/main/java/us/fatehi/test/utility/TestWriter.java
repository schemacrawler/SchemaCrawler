/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.utility;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Objects;

public final class TestWriter extends Writer implements TestOutputCapture {

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
    writeout(System.lineSeparator());
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

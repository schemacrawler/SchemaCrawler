/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import static java.nio.file.Files.newOutputStream;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TestOutputStream extends OutputStream implements TestOutputCapture {

  private final Path tempFile;
  private final OutputStream out;

  public TestOutputStream() {
    try {
      tempFile = Files.createTempFile("test", "");
      out = newOutputStream(tempFile, WRITE, CREATE, TRUNCATE_EXISTING);
    } catch (final IOException e) {
      throw new RuntimeException("Could not open output stream to a temporary file", e);
    }
  }

  @Override
  public String getContents() {
    try {
      out.flush();
      out.close();
      return new String(readAllBytes(tempFile), StandardCharsets.UTF_8);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Path getFilePath() {
    return tempFile;
  }

  @Override
  public String toString() {
    return tempFile.toString();
  }

  @Override
  public void write(final int b) throws IOException {
    out.write(b);
  }
}

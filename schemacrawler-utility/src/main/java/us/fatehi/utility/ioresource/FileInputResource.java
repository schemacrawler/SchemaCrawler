/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.ioresource;

import static java.nio.file.Files.newInputStream;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.IOUtility.isFileReadable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class FileInputResource extends BaseInputResource {

  private final Path inputFile;

  public FileInputResource(final Path filePath) throws IOException {
    inputFile = requireNonNull(filePath, "No file path provided").normalize().toAbsolutePath();
    if (!isFileReadable(inputFile)) {
      final IOException e = new IOException("Cannot read file, <%s>".formatted(inputFile));
      throw e;
    }
  }

  @Override
  public InputStream openNewInputStream() throws IOException {
    final InputStream reader = newInputStream(inputFile);
    return reader;
  }

  @Override
  public String toString() {
    return inputFile.toString();
  }
}

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.string;

import static java.nio.file.Files.readAllBytes;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.function.Supplier;

public final class FileContents implements Supplier<String> {

  private final Charset charset;
  private final Path file;

  public FileContents(final Path file) {
    this(file, Charset.defaultCharset());
  }

  public FileContents(final Path file, final Charset charset) {
    this.file = requireNonNull(file, "No file path provided");
    this.charset = requireNonNull(charset, "No charset provided");
  }

  @Override
  public String get() {
    final String output;
    try {
      output = new String(readAllBytes(file), charset);
    } catch (final IOException e) {
      return "";
    }
    return output;
  }

  @Override
  public String toString() {
    return get();
  }
}

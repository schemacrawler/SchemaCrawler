/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.ioresource;

import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import us.fatehi.utility.string.StringFormat;

public final class FileOutputResource implements OutputResource {

  private static final Logger LOGGER = Logger.getLogger(FileOutputResource.class.getName());

  private final Path outputFile;

  public FileOutputResource(final Path filePath) {
    outputFile = requireNonNull(filePath, "No file path provided").normalize().toAbsolutePath();
  }

  public Path getOutputFile() {
    return outputFile;
  }

  @Override
  public Writer openNewOutputWriter(final Charset charset, final boolean appendOutput)
      throws IOException {
    requireNonNull(charset, "No output charset provided");
    final OpenOption[] openOptions;
    if (appendOutput) {
      openOptions = new OpenOption[] {WRITE, CREATE, APPEND};
    } else {
      openOptions = new OpenOption[] {WRITE, CREATE, TRUNCATE_EXISTING};
    }
    final Writer writer = newBufferedWriter(outputFile, charset, openOptions);
    LOGGER.log(Level.FINE, new StringFormat("Opened output writer to file <%s>", outputFile));
    return writer;
  }

  @Override
  public String toString() {
    return outputFile.toString();
  }
}

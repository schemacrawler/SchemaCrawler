/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.output;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.string.StringFormat;

/**
 * Expanded output context that writes each entry as a separate file under a root directory, instead
 * of packaging the report as a ZIP file.
 */
public final class FileScribeOutputContext implements ScribeOutputContext {

  private static final Logger LOGGER = Logger.getLogger(FileScribeOutputContext.class.getName());

  private final Path rootDirectory;

  /**
   * Creates the output context, rooted at the given directory, creating it if it does not exist.
   *
   * @param rootDirectory Root directory for output files
   * @throws IOException On an I/O error
   */
  public FileScribeOutputContext(final Path rootDirectory) throws IOException {
    this.rootDirectory = requireNonNull(rootDirectory, "No root directory provided");
    Files.createDirectories(rootDirectory);
    LOGGER.log(
        Level.INFO, new StringFormat("Writing expanded Scribe output to <%s>", rootDirectory));
  }

  /** {@inheritDoc} */
  @Override
  public void close() {
    // Each writer or stream is closed individually by its caller
  }

  /** {@inheritDoc} */
  @Override
  public OutputStream openNewOutputStream(final String relativePath) throws IOException {
    final Path file = resolve(relativePath);
    return Files.newOutputStream(
        file, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
  }

  /** {@inheritDoc} */
  @Override
  public Writer openNewOutputWriter(final String relativePath) throws IOException {
    final Path file = resolve(relativePath);
    return Files.newBufferedWriter(
        file,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING);
  }

  private Path resolve(final String relativePath) throws IOException {
    final Path file = rootDirectory.resolve(relativePath);
    Files.createDirectories(file.getParent());
    return file;
  }
}

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.okf;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.ioresource.FileOutputResource;
import us.fatehi.utility.ioresource.OutputResource;
import us.fatehi.utility.string.StringFormat;

/**
 * An output resource rooted at a directory. Each call to {@link #resolve(String)} returns a new
 * {@link OutputResource} for the given relative path; the writer creates all necessary parent
 * directories before opening the file.
 */
public final class BundleDirectoryOutput {

  private static final Logger LOGGER = Logger.getLogger(BundleDirectoryOutput.class.getName());

  private final Path rootDirectory;

  /**
   * Creates a directory output resource rooted at the given directory.
   *
   * @param rootDirectory Root directory for all relative output paths
   */
  public BundleDirectoryOutput(final Path rootDirectory) {
    this.rootDirectory =
        requireNonNull(rootDirectory, "No root directory provided").normalize().toAbsolutePath();
    LOGGER.log(Level.FINE, new StringFormat("Output rooted at directory <%s>", this.rootDirectory));
  }

  /**
   * Resolves a relative path under the root directory and returns an {@link OutputResource} that
   * creates all necessary parent directories before opening a writer.
   *
   * @param relativePath Path of the entry, relative to the root directory
   * @return Output resource for the resolved file
   * @throws IOException
   */
  public OutputResource resolve(final String relativePath) throws IOException {
    requireNonNull(relativePath, "No relative path provided");
    final Path file = rootDirectory.resolve(relativePath).normalize().toAbsolutePath();
    Files.createDirectories(file.getParent());
    return new FileOutputResource(file);
  }

  @Override
  public String toString() {
    return rootDirectory.toString();
  }
}

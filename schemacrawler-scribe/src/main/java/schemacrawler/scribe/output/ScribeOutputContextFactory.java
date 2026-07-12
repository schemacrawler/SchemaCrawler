/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.output;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.string.StringFormat;

/**
 * Creates the appropriate {@link ScribeOutputContext} implementation, based on whether expanded
 * file/folder output was requested (see the {@code --expanded-output} Scribe command option).
 */
public final class ScribeOutputContextFactory {

  private static final Logger LOGGER = Logger.getLogger(ScribeOutputContextFactory.class.getName());

  private static final String ZIP_EXTENSION = ".zip";

  /**
   * Creates a new output context for the given output path, choosing between expanded file/folder
   * output and ZIP output.
   *
   * @param outputPath Requested output path
   * @param expandedOutput Whether to write expanded files and folders instead of a ZIP archive
   * @return New output context
   * @throws IOException On an I/O error
   */
  public static ScribeOutputContext create(final Path outputPath, final boolean expandedOutput)
      throws IOException {
    if (expandedOutput) {
      LOGGER.log(Level.INFO, new StringFormat("Using expanded file output at <%s>", outputPath));
      return new FileScribeOutputContext(outputPath);
    }
    return new ZipScribeOutputContext(ensureZipExtension(outputPath));
  }

  private static Path ensureZipExtension(final Path outputPath) {
    if (outputPath.getFileName().toString().toLowerCase().endsWith(ZIP_EXTENSION)) {
      return outputPath;
    }
    return outputPath.resolveSibling(outputPath.getFileName().toString() + ZIP_EXTENSION);
  }

  private ScribeOutputContextFactory() {
    // Prevent instantiation
  }
}

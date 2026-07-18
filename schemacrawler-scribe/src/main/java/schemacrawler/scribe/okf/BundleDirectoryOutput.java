/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.okf;

import static java.util.Objects.requireNonNull;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.ioresource.FileOutputResource;
import us.fatehi.utility.ioresource.OutputResource;
import us.fatehi.utility.string.StringFormat;

/**
 * An output resource rooted at a directory. Each call to {@link #resolve(String)} returns a new
 * {@link OutputResource} for the given relative path; the writer creates all necessary parent
 * directories before opening the file.
 */
public final class BundleDirectoryOutput implements Closeable {

  private static final Logger LOGGER = Logger.getLogger(BundleDirectoryOutput.class.getName());

  private static void deleteDirectory(final Path dir) {
    try (final var paths = Files.walk(dir)) {
      paths
          .sorted(Comparator.reverseOrder())
          .forEach(
              path -> {
                try {
                  Files.deleteIfExists(path);
                } catch (final IOException e) {
                  // Best-effort cleanup; log but don't rethrow
                  LOGGER.log(Level.WARNING, new StringFormat("Could not delete <%s>", path));
                }
              });
    } catch (final IOException e) {
      LOGGER.log(Level.WARNING, new StringFormat("Could not clean up temp directory <%s>", dir));
    }
  }

  private static Path ensureZipExtension(final Path outputPath) {
    if ("zip".equals(IOUtility.getFileExtension(outputPath))) {
      return outputPath;
    }
    return outputPath.resolveSibling(outputPath.getFileName() + ".zip");
  }

  private static void zipDirectory(final Path sourceDir, final Path zipFile) throws IOException {
    LOGGER.log(Level.INFO, new StringFormat("Writing Scribe report to ZIP file <%s>", zipFile));
    final Path parent = zipFile.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    try (final ZipOutputStream zout =
        new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(zipFile)))) {
      try (final var paths = Files.walk(sourceDir)) {
        paths
            .filter(Files::isRegularFile)
            .forEach(
                path -> {
                  final String entryName = sourceDir.relativize(path).toString().replace('\\', '/');
                  try {
                    zout.putNextEntry(new ZipEntry(entryName));
                    Files.copy(path, zout);
                    zout.closeEntry();
                  } catch (final IOException e) {
                    throw new UncheckedIOException(e);
                  }
                });
      }
    }
  }

  private final Path rootDirectory;

  private final Path outputPath;

  private final boolean finalizeToZip;

  /**
   * Creates a directory output resource rooted at the given directory.
   *
   * @param rootDirectory Root directory for all relative output paths
   * @throws IOException
   */
  public BundleDirectoryOutput(final Path outputPath, final boolean finalizeToZip)
      throws IOException {
    requireNonNull(outputPath, "No output path provided");

    this.finalizeToZip = finalizeToZip;
    if (finalizeToZip) {
      final Path zipFilePath = ensureZipExtension(outputPath).toAbsolutePath();
      this.outputPath = zipFilePath;
      rootDirectory = Files.createTempDirectory("scribe-");
    } else {
      this.outputPath = outputPath.toAbsolutePath();
      rootDirectory = outputPath;
    }
    LOGGER.log(Level.FINE, new StringFormat("Output rooted at directory <%s>", rootDirectory));
  }

  @Override
  public void close() throws IOException {
    if (finalizeToZip) {
      try {
        zipDirectory(rootDirectory, outputPath);
      } finally {
        if (rootDirectory != null && Files.exists(rootDirectory)) {
          deleteDirectory(rootDirectory);
        }
      }
    }
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

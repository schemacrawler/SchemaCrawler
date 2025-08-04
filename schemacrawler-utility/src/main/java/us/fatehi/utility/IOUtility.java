/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createTempDirectory;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.isWritable;
import static java.nio.file.Files.size;
import static java.util.UUID.randomUUID;
import static us.fatehi.utility.Utility.isBlank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.ioresource.ClasspathInputResource;
import us.fatehi.utility.ioresource.InputResource;

@UtilityMarker
public final class IOUtility {

  private static final Logger LOGGER = Logger.getLogger(IOUtility.class.getName());

  /**
   * Reads the stream fully, and writes to the writer.
   *
   * @param reader Reader to read.
   * @param writer Writer to copy to
   */
  public static void copy(final Reader reader, final Writer writer) {
    if (reader == null) {
      LOGGER.log(Level.FINE, "Cannot read null reader");
      return;
    }
    if (writer == null) {
      LOGGER.log(Level.FINE, "Cannot write null writer");
      return;
    }

    final char[] buffer = new char[0x10000];
    try {
      // Do not close resources - that is the responsibility of the
      // caller
      final Reader bufferedReader = new BufferedReader(reader, buffer.length);
      final BufferedWriter bufferedWriter = new BufferedWriter(writer, buffer.length);

      int read;
      do {
        read = bufferedReader.read(buffer, 0, buffer.length);
        if (read > 0) {
          bufferedWriter.write(buffer, 0, read);
        }
      } while (read >= 0);

      bufferedWriter.flush();
    } catch (final IOException e) {
      LOGGER.log(Level.INFO, e.getMessage());
      LOGGER.log(Level.FINE, e.getMessage(), e);
    }
  }

  public static Path createTempFilePath(final String stem, final String extension)
      throws IOException {
    final String filename =
        String.format("%s%s.%s", Utility.trimToEmpty(stem), randomUUID(), extension);
    final Path tempFilePath =
        createTempDirectory(null).resolve(filename).normalize().toAbsolutePath();
    tempFilePath.toFile().deleteOnExit();
    return tempFilePath;
  }

  public static String getFileExtension(final Path file) {
    if (file == null) {
      return "";
    }
    final String fileName = file.toString();
    return getFileExtension(fileName == null ? "" : fileName);
  }

  public static String getFileExtension(final String fileName) {
    final String ext;
    if (fileName != null) {
      ext =
          fileName.lastIndexOf('.') == -1 ? "" : fileName.substring(fileName.lastIndexOf('.') + 1);
    } else {
      ext = "";
    }
    return ext;
  }

  /**
   * Checks if an input file can be read. The file must contain some data.
   *
   * @param file Input file to read
   * @return True if the file can be read, false otherwise.
   */
  public static boolean isFileReadable(final Path file) {
    if (file == null || !isReadable(file) || !isRegularFile(file)) {
      return false;
    }
    try {
      if (size(file) == 0) {
        return false;
      }
    } catch (final IOException e) {
      // Not a critical check, so ignore exception
    }
    return true;
  }

  /**
   * Checks if an output file can be written. The file does not need to exist.
   *
   * @param file Output file to write
   * @return True if the file can be written, false otherwise.
   */
  public static boolean isFileWritable(final Path file) {
    if (file == null || isDirectory(file)) {
      return false;
    }
    final Path parentPath = file.getParent();
    return parentPath != null
        && exists(parentPath)
        && isDirectory(parentPath)
        && isWritable(parentPath);
  }

  /**
   * Locates the resource bases on the current thread's classloader. Always assumes that resources
   * are absolute.
   *
   * @return URL for the located resource, or null if not found
   */
  public static URL locateResource(final String classpathResource) {
    if (isBlank(classpathResource)) {
      return null;
    }
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    final String resolvedClasspathResource;
    if (classpathResource.startsWith("/")) {
      resolvedClasspathResource = classpathResource.substring(1);
    } else {
      resolvedClasspathResource = classpathResource;
    }
    return classLoader.getResource(resolvedClasspathResource);
  }

  /**
   * Reads the stream fully, and returns a byte array of data.
   *
   * @param reader Reader to read.
   * @return Byte array
   */
  public static String readFully(final Reader reader) {
    if (reader == null) {
      LOGGER.log(Level.FINE, "Cannot read null reader");
      return "";
    }

    try {
      final StringWriter writer = new StringWriter();
      copy(reader, writer);
      writer.close();
      return writer.toString();
    } catch (final IOException e) {
      // This is the error thrown while closing the writer itself, not during copy
      LOGGER.log(Level.INFO, e.getMessage());
      LOGGER.log(Level.FINE, e.getMessage(), e);
      return "";
    }
  }

  public static String readResourceFully(final String resource) {
    try {
      final InputResource inputResource = new ClasspathInputResource(resource);
      return readFully(inputResource.openNewInputReader(UTF_8));
    } catch (final IOException e) {
      LOGGER.log(Level.INFO, e.getMessage());
      LOGGER.log(Level.FINE, e.getMessage(), e);
      return "";
    }
  }

  private IOUtility() {
    // Prevent instantiation
  }
}

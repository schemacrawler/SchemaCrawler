/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.utility;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Objects.requireNonNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.opentest4j.TestAbortedException;

public final class TestUtility {

  public static void clean(final String dirname) throws Exception {
    final Path expectedResultsDirectory = buildDirectory().resolve("unit_tests_results_output");
    FileUtils.deleteDirectory(expectedResultsDirectory.resolve(dirname).toFile());
    if (Files.exists(expectedResultsDirectory)
        && FileUtils.sizeOfDirectory(expectedResultsDirectory.toFile()) == 0) {
      FileUtils.deleteDirectory(expectedResultsDirectory.toFile());
    }
  }

  public static Path copyResourceToTempFile(final String resource) {
    // Normalize resource name: remove leading slash if present
    final String normalized = resource.startsWith("/") ? resource.substring(1) : resource;

    // Try to load resource from classpath
    try (InputStream in =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(normalized)) {
      if (in == null) {
        return null; // Resource not found
      }

      // Create temp file with same extension if possible
      final String suffix =
          normalized.contains(".") ? normalized.substring(normalized.lastIndexOf('.')) : ".tmp";
      final Path tempFile = Files.createTempFile("resource-", suffix);
      Files.copy(in, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
      return tempFile;
    } catch (final IOException e) {
      return null; // Any I/O error results in null
    }
  }

  public static void deleteIfPossible(final Path testOutputTargetFilePath) {
    try {
      deleteIfExists(testOutputTargetFilePath);
    } catch (final IOException e) {
      // Ignore exception
    }
  }

  public static <V> V failTestSetup(final String message, final Exception e) {
    throw new TestAbortedException(message, e);
  }

  public static String fileHeaderOf(final Path tempFile) throws IOException {
    try (final FileInputStream fileInputStream = new FileInputStream(tempFile.toFile());
        final FileChannel fc = fileInputStream.getChannel()) {
      final ByteBuffer bb = ByteBuffer.allocate(2);
      fc.read(bb);
      final String hexValue = new BigInteger(1, bb.array()).toString(16);
      return hexValue.toUpperCase();
    }
  }

  public static String[] flattenCommandlineArgs(final Map<String, String> argsMap) {
    final List<String> argsList = new ArrayList<>();
    for (final Map.Entry<String, String> arg : argsMap.entrySet()) {
      final String key = arg.getKey();
      final String value = arg.getValue();
      if (value != null) {
        argsList.add("%s=%s".formatted(key, value));
      } else {
        argsList.add("%s".formatted(key));
      }
    }
    final String[] args = argsList.toArray(new String[0]);
    return args;
  }

  /**
   * Loads a properties file.
   *
   * @param inputResource Properties resource.
   * @return Properties
   * @throws IOException
   */
  public static Properties loadPropertiesFromClasspath(final String resource) {
    final Properties props = new Properties();

    // Normalize resource name: remove leading slash if present
    final String normalized = resource.startsWith("/") ? resource.substring(1) : resource;

    try (InputStream in =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(normalized)) {
      if (in != null) {
        props.load(in);
      }
    } catch (final IOException e) {
      // Ignore and return empty properties
    }

    return props;
  }

  public static Path savePropertiesToTempFile(final Properties properties) throws IOException {
    requireNonNull(properties, "No properties provided");
    final Path propertiesFile = Files.createTempFile("schemacrawler", ".properties");
    final Writer writer =
        newBufferedWriter(propertiesFile, UTF_8, WRITE, CREATE, TRUNCATE_EXISTING);
    properties.store(writer, "Temporary file to hold properties");
    return propertiesFile;
  }

  public static Path buildDirectory() throws Exception {
    final StackTraceElement ste = currentMethodStackTraceElement();
    final Class<?> callingClass = Class.forName(ste.getClassName());
    final Path codePath =
        Path.of(callingClass.getProtectionDomain().getCodeSource().getLocation().toURI())
            .normalize()
            .toAbsolutePath();
    final boolean isInTarget = codePath.toString().contains("target");
    if (!isInTarget) {
      throw new RuntimeException("Not in build directory, " + codePath);
    }
    final Path directory = codePath.resolve("..");
    return directory.normalize().toAbsolutePath();
  }

  private static StackTraceElement currentMethodStackTraceElement() {
    final Pattern baseTestClassName = Pattern.compile(".*\\.Base.*Test");
    final Pattern testClassName = Pattern.compile(".*\\.[A-Z].*Test");

    final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    for (final StackTraceElement stackTraceElement : stackTrace) {
      final String className = stackTraceElement.getClassName();
      if (testClassName.matcher(className).matches()
          && !baseTestClassName.matcher(className).matches()) {
        return stackTraceElement;
      }
    }

    return null;
  }

  private TestUtility() {
    // Prevent instantiation
  }
}

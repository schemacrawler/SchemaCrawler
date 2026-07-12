/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.okf;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/** Test-only support for reading entries back out of a generated ZIP. */
final class ZipTestUtility {

  static Set<String> entryNames(final Path zipFile) {
    final Set<String> names = new TreeSet<>();
    try (ZipFile zip = new ZipFile(zipFile.toFile())) {
      zip.stream().map(ZipEntry::getName).forEach(names::add);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
    return names;
  }

  static boolean hasEntry(final Path zipFile, final String entryName) {
    return entryNames(zipFile).contains(entryName);
  }

  static String readEntry(final Path zipFile, final String entryName) {
    try (ZipFile zip = new ZipFile(zipFile.toFile())) {
      final ZipEntry entry = zip.getEntry(entryName);
      if (entry == null) {
        throw new IllegalArgumentException("No such entry <%s>".formatted(entryName));
      }
      return new String(zip.getInputStream(entry).readAllBytes(), StandardCharsets.UTF_8);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private ZipTestUtility() {
    // Prevent instantiation
  }
}

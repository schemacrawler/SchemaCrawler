/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.output;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ZipScribeOutputContextTest {

  @Test
  public void writesTwoEntries(@TempDir final Path tempDir) throws IOException {
    final Path zipFile = tempDir.resolve("report.zip");

    try (ZipScribeOutputContext context = new ZipScribeOutputContext(zipFile)) {
      try (Writer writer = context.openWriter("index.txt")) {
        writer.write("hello index");
      }
      try (Writer writer = context.openWriter("tables/books.txt")) {
        writer.write("hello table");
      }
    }

    try (ZipFile zip = new ZipFile(zipFile.toFile())) {
      final ZipEntry indexEntry = zip.getEntry("index.txt");
      assertThat(indexEntry, is(not((ZipEntry) null)));
      final ZipEntry tableEntry = zip.getEntry("tables/books.txt");
      assertThat(tableEntry, is(not((ZipEntry) null)));

      final String indexContent =
          new String(zip.getInputStream(indexEntry).readAllBytes(), StandardCharsets.UTF_8);
      assertThat(indexContent, is("hello index"));

      final String tableContent =
          new String(zip.getInputStream(tableEntry).readAllBytes(), StandardCharsets.UTF_8);
      assertThat(tableContent, is("hello table"));
    }

    assertThat(Files.exists(zipFile), is(true));
  }
}

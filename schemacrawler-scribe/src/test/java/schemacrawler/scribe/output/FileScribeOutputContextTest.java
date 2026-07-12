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

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileScribeOutputContextTest {

  @Test
  public void writesTwoEntries(@TempDir final Path tempDir) throws IOException {
    final Path rootDirectory = tempDir.resolve("report");

    try (FileScribeOutputContext context = new FileScribeOutputContext(rootDirectory)) {
      try (Writer writer = context.openWriter("index.txt")) {
        writer.write("hello index");
      }
      try (Writer writer = context.openWriter("tables/books.txt")) {
        writer.write("hello table");
      }
    }

    final Path indexFile = rootDirectory.resolve("index.txt");
    final Path tableFile = rootDirectory.resolve("tables/books.txt");

    assertThat(Files.exists(indexFile), is(true));
    assertThat(Files.exists(tableFile), is(true));
    assertThat(Files.isDirectory(rootDirectory.resolve("tables")), is(true));

    assertThat(Files.readString(indexFile), is("hello index"));
    assertThat(Files.readString(tableFile), is("hello table"));
  }
}

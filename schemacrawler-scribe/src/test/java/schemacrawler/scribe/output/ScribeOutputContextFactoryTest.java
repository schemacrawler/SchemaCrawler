/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.output;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ScribeOutputContextFactoryTest {

  @Test
  public void appendsZipExtensionWhenMissing(@TempDir final Path tempDir) throws IOException {
    final Path outputPath = tempDir.resolve("report");
    try (ScribeOutputContext context = ScribeOutputContextFactory.create(outputPath, false)) {
      assertThat(context, instanceOf(ZipScribeOutputContext.class));
    }
    assertThat(Files.exists(tempDir.resolve("report.zip")), is(true));
  }

  @Test
  public void createsZipContextByDefault(@TempDir final Path tempDir) throws IOException {
    final Path outputPath = tempDir.resolve("report.zip");
    try (ScribeOutputContext context = ScribeOutputContextFactory.create(outputPath, false)) {
      assertThat(context, instanceOf(ZipScribeOutputContext.class));
    }
  }

  @Test
  public void createsFileContextWhenExpandedOutputRequested(@TempDir final Path tempDir)
      throws IOException {
    final Path outputPath = tempDir.resolve("report-dir");
    try (ScribeOutputContext context = ScribeOutputContextFactory.create(outputPath, true)) {
      assertThat(context, instanceOf(FileScribeOutputContext.class));
    }
    assertThat(Files.isDirectory(outputPath), is(true));
    assertThat(Files.exists(tempDir.resolve("report-dir.zip")), is(false));
  }
}

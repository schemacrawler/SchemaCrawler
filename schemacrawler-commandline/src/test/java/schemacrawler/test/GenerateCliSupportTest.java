/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import schemacrawler.tools.commandline.utility.GenerateCliSupport;

public class GenerateCliSupportTest {

  @Test
  public void generateCliSupportHappyPath(@TempDir final Path tempDir) throws Exception {
    final String completionScriptName = "schemacrawler_completion.sh";

    GenerateCliSupport.main(
        new String[] {
          "--output-dir", tempDir.toString(), "--completion-script", completionScriptName
        });

    // Output directory should exist and contain files
    assertThat(Files.exists(tempDir), is(true));

    // Completion script should be generated
    final Path completionScript = tempDir.resolve(completionScriptName);
    assertThat("Completion script not generated", Files.exists(completionScript), is(true));
    assertThat(
        "Completion script should not be empty", Files.size(completionScript), is(greaterThan(0L)));

    // At least one AsciiDoc man-page file should be generated
    final long adocCount;
    try (final Stream<Path> files = Files.list(tempDir)) {
      adocCount = files.filter(p -> p.getFileName().toString().endsWith(".adoc")).count();
    }
    assertThat("Expected AsciiDoc man-page files to be generated", adocCount, is(greaterThan(0L)));
  }

  @Test
  public void generateCliSupportMissingCompletionScript(@TempDir final Path tempDir) {
    // Missing --completion-script should cause picocli to report missing params
    assertThrows(
        RuntimeException.class,
        () -> GenerateCliSupport.main(new String[] {"--output-dir", tempDir.toString()}));
  }

  @Test
  public void generateCliSupportMissingRequiredArgs() {
    // Missing --output-dir and --completion-script should cause picocli to report
    // missing params
    // GenerateCliSupport.main throws RuntimeException when exit code is non-zero
    assertThrows(RuntimeException.class, () -> GenerateCliSupport.main(new String[] {}));
  }
}

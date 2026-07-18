/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.okf;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class BundleDirectoryOutputTest {

  @Test
  public void resolveRejectsPathThatEscapesRoot(@TempDir final Path tempDir) throws Exception {
    final Path root = tempDir.resolve("bundle-root");
    final BundleDirectoryOutput output = new BundleDirectoryOutput(root, true);

    assertThrows(IOException.class, () -> output.resolve("../outside.md"));
  }
}

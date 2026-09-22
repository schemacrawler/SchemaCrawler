/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbconnectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

/** Asserts fix to . */
public class BadYamlPluginRegistrationTest {

  @Test
  void badPluginIsNotRegistered() throws IOException {
    final Path tempFile = Files.createTempFile(Paths.get("."), "bad-dbconnector", ".yaml");
    try {
      Files.writeString(tempFile, "This is not YAML!");

      assertThat(Files.exists(tempFile), is(true));
      assertThat(Files.readAllLines(tempFile).size(), is(1));

      assertDoesNotThrow(() -> DatabaseConnectorRegistry.getRegistry());

      final DatabaseConnectorRegistry registry = DatabaseConnectorRegistry.getRegistry();
      assertThat(registry.hasDatabaseSystemIdentifier("access"), is(true));
    } finally {
      if (Files.exists(tempFile)) {
        Files.delete(tempFile);
      }
    }
  }
}

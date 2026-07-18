/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.scribe.command.options.ScribeOptionsBuilder;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.options.OutputOptionsBuilder;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class ScribeCommandTest {

  @Test
  public void executeThrowsWhenFormatIsNotSupported(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final Path zipFile = tempDir.resolve("out.zip");

    final ScribeCommand command = new ScribeCommand();
    command.setCatalog(catalog);
    command.setOutputOptions(
        OutputOptionsBuilder.builder()
            .withOutputFile(zipFile)
            .withOutputFormatValue("stub")
            .toOptions());
    command.configure(ScribeOptionsBuilder.builder().toOptions());

    final ExecutionRuntimeException exception =
        assertThrows(ExecutionRuntimeException.class, command::execute);
    assertThat(exception.getMessage(), containsString("Generate a database schema report bundle"));
    assertThat(Files.exists(zipFile), is(false));
  }

  @Test
  public void executeWithExpandedOutputWritesDirectoryTree(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final Path outputDir = tempDir.resolve("out-dir");

    final ScribeCommand command = new ScribeCommand();
    command.setCatalog(catalog);
    command.setOutputOptions(
        OutputOptionsBuilder.builder()
            .withOutputFile(outputDir)
            .withOutputFormatValue("stub")
            .toOptions());
    command.configure(ScribeOptionsBuilder.builder().withExpandedOutput(true).toOptions());

    final ExecutionRuntimeException exception =
        assertThrows(ExecutionRuntimeException.class, command::execute);
    assertThat(exception.getMessage(), containsString("Generate a database schema report bundle"));

    assertThat(Files.exists(outputDir), is(false));
    assertThat(Files.exists(tempDir.resolve("out-dir.zip")), is(false));
  }

  @Test
  public void executeThrowsWhenNoRendererMatches(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir) {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final ScribeCommand command = new ScribeCommand();
    command.setCatalog(catalog);
    command.setOutputOptions(
        OutputOptionsBuilder.builder()
            .withOutputFile(tempDir.resolve("out.zip"))
            .withOutputFormatValue("no-such-format")
            .toOptions());
    command.configure(ScribeOptionsBuilder.builder().toOptions());

    assertThrows(ExecutionRuntimeException.class, command::execute);
  }

  @Test
  public void executeWithLintEnabledRequiresConnection(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir) {
    final ScribeCommand command = new ScribeCommand();
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    command.setCatalog(catalog);
    command.setOutputOptions(
        OutputOptionsBuilder.builder()
            .withOutputFile(tempDir.resolve("out.zip"))
            .withOutputFormatValue("stub")
            .toOptions());
    command.configure(ScribeOptionsBuilder.builder().withIncludeLint(true).toOptions());

    final ExecutionRuntimeException exception =
        assertThrows(ExecutionRuntimeException.class, command::execute);
    assertThat(exception.getMessage(), containsString("No database connection source provided"));
  }
}

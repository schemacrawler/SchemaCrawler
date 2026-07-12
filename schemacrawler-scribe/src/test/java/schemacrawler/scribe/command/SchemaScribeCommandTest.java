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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.scribe.command.options.SchemaScribeOptions;
import schemacrawler.scribe.command.options.SchemaScribeOptionsBuilder;
import schemacrawler.scribe.output.ScribeOutputContext;
import schemacrawler.scribe.renderer.ScribeRenderer;
import schemacrawler.scribe.renderer.ScribeSupport;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.options.OutputOptionsBuilder;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class SchemaScribeCommandTest {

  @Test
  public void executeRendersWithRegisteredRenderer(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final Path zipFile = tempDir.resolve("out.zip");

    final SchemaScribeCommand command = new SchemaScribeCommand();
    command.setCatalog(catalog);
    command.setOutputOptions(
        OutputOptionsBuilder.builder()
            .withOutputFile(zipFile)
            .withOutputFormatValue("stub")
            .toOptions());
    final SchemaScribeOptions options = SchemaScribeOptionsBuilder.builder().toOptions();
    command.configure(options);

    final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
    final URLClassLoader stubClassLoader = newStubRendererClassLoader(tempDir, originalClassLoader);
    Thread.currentThread().setContextClassLoader(stubClassLoader);
    try {
      final ExecutionRuntimeException exception =
          assertThrows(ExecutionRuntimeException.class, command::execute);
      assertThat(exception.getMessage(), containsString("No Scribe renderer"));
    } finally {
      Thread.currentThread().setContextClassLoader(originalClassLoader);
    }
    assertThat(Files.exists(zipFile), is(false));
  }

  @Test
  public void executeWithExpandedOutputWritesDirectoryTree(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final Path outputDir = tempDir.resolve("out-dir");

    final SchemaScribeCommand command = new SchemaScribeCommand();
    command.setCatalog(catalog);
    command.setOutputOptions(
        OutputOptionsBuilder.builder()
            .withOutputFile(outputDir)
            .withOutputFormatValue("stub")
            .toOptions());
    command.configure(SchemaScribeOptionsBuilder.builder().withExpandedOutput(true).toOptions());

    final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
    final URLClassLoader stubClassLoader = newStubRendererClassLoader(tempDir, originalClassLoader);
    Thread.currentThread().setContextClassLoader(stubClassLoader);
    try {
      final ExecutionRuntimeException exception =
          assertThrows(ExecutionRuntimeException.class, command::execute);
      assertThat(exception.getMessage(), containsString("No Scribe renderer"));
    } finally {
      Thread.currentThread().setContextClassLoader(originalClassLoader);
    }

    assertThat(Files.exists(outputDir), is(false));
    assertThat(Files.exists(tempDir.resolve("out-dir.zip")), is(false));
  }

  @Test
  public void executeThrowsWhenNoRendererMatches(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir) {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final SchemaScribeCommand command = new SchemaScribeCommand();
    command.setCatalog(catalog);
    command.setOutputOptions(
        OutputOptionsBuilder.builder()
            .withOutputFile(tempDir.resolve("out.zip"))
            .withOutputFormatValue("no-such-format")
            .toOptions());
    command.configure(SchemaScribeOptionsBuilder.builder().toOptions());

    assertThrows(ExecutionRuntimeException.class, command::execute);
  }

  @Test
  public void executeWithLintEnabledRequiresConnection(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir) {
    final SchemaScribeCommand command = new SchemaScribeCommand();
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    command.setCatalog(catalog);
    command.setOutputOptions(
        OutputOptionsBuilder.builder()
            .withOutputFile(tempDir.resolve("out.zip"))
            .withOutputFormatValue("stub")
            .toOptions());
    command.configure(SchemaScribeOptionsBuilder.builder().withIncludeLint(true).toOptions());

    final ExecutionRuntimeException exception =
        assertThrows(ExecutionRuntimeException.class, command::execute);
    assertThat(exception.getMessage(), containsString("No database connection source provided"));
  }

  private URLClassLoader newStubRendererClassLoader(final Path tempDir, final ClassLoader parent)
      throws IOException {
    final Path servicesDir = tempDir.resolve("META-INF/services");
    Files.createDirectories(servicesDir);
    Files.writeString(
        servicesDir.resolve("schemacrawler.scribe.renderer.ScribeRenderer"),
        StubScribeRenderer.class.getName(),
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING);
    final URL[] urls = {tempDir.toUri().toURL()};
    return new URLClassLoader(urls, parent);
  }

  /** Test-only renderer, registered dynamically via an isolated classloader per test. */
  public static final class StubScribeRenderer implements ScribeRenderer {

    static volatile ScribeSupport lastHelper;
    static volatile SchemaScribeOptions lastOptions;

    @Override
    public String getSupportedOutputFormat() {
      return "stub";
    }

    @Override
    public void render(
        final ScribeSupport support,
        final SchemaScribeOptions options,
        final ScribeOutputContext output) {
      lastHelper = support;
      lastOptions = options;
      try (Writer writer = output.openWriter("index.txt")) {
        writer.write("stub");
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }
}

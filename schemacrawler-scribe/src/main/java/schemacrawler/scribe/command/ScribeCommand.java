/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.command;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.scribe.command.options.ScribeOptions;
import schemacrawler.scribe.command.options.ScribeOptionsBuilder;
import schemacrawler.scribe.command.options.ScribeOutputFormat;
import schemacrawler.scribe.okf.BundleDirectoryOutput;
import schemacrawler.scribe.renderer.ScribeRenderer;
import schemacrawler.scribe.renderer.ScribeSupport;
import schemacrawler.tools.command.AbstractSchemaCrawlerCommand;
import schemacrawler.tools.lint.LinterRegistry;
import schemacrawler.tools.lint.Linters;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.lint.config.LinterConfigs;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.StringFormat;

/**
 * Generates a multi-file Scribe schema report, packaged as a ZIP file by default, or written as
 * expanded files and folders when the {@code --expanded-output} option is set.
 */
public final class ScribeCommand extends AbstractSchemaCrawlerCommand<ScribeOptions> {

  private static final Logger LOGGER = Logger.getLogger(ScribeCommand.class.getName());

  static final PropertyName COMMAND =
      new PropertyName("scribe", "Generate a database schema report bundle");

  /** Creates the Scribe command. */
  public ScribeCommand() {
    super(COMMAND);
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {
    checkCatalog();

    final String outputFormat = getOutputOptions().getOutputFormatValue();
    final String title = getOutputOptions().getTitle();

    final boolean supportedFormat = ScribeOutputFormat.isSupportedFormat(outputFormat);
    if (!supportedFormat) {
      throw new ExecutionRuntimeException(
          "Output format <%s> not supported for command <%s>".formatted(outputFormat, command));
    }

    final ScribeOptions options =
        ScribeOptionsBuilder.builder()
            .fromOptions(getCommandOptions())
            .withTitle(title)
            .toOptions();

    final Path outputPath = getOutputOptions().getOutputFile("");
    final Lints lints = options.isIncludeLint() ? runLint() : new Lints(List.of());
    final ScribeSupport support = new ScribeSupport(this, options, lints);

    final ScribeRenderer renderer = lookupRenderer(outputFormat);

    Path tempDir = null;
    try {
      tempDir = Files.createTempDirectory("scribe-");
      renderer.render(support, new BundleDirectoryOutput(tempDir));
      finalizeOutput(tempDir, outputPath, options.isExpandedOutput());
    } catch (final Exception e) {
      throw new ExecutionRuntimeException("Could not generate Scribe report", e);
    } finally {
      if (tempDir != null && Files.exists(tempDir)) {
        deleteDirectory(tempDir);
      }
    }

    LOGGER.log(Level.INFO, new StringFormat("Generated Scribe report at <%s>", outputPath));
  }

  /** {@inheritDoc} */
  @Override
  public boolean usesConnection() {
    return getCommandOptions() != null && getCommandOptions().isIncludeLint();
  }

  private ScribeRenderer lookupRenderer(final String outputFormat) {
    final ScribeOutputFormat scribeOutputFormat = ScribeOutputFormat.fromFormat(outputFormat);
    return scribeOutputFormat.newRenderer();
  }

  private Lints runLint() {
    final Config config = ConfigUtility.newConfig();
    final LinterConfigs linterConfigs = new LinterConfigs(config);
    final Linters linters = new Linters(linterConfigs, true);
    final LinterRegistry registry = LinterRegistry.getLinterRegistry();
    linters.initialize(registry);
    transferState(linters);
    linters.lint();
    return linters.getLints();
  }

  private void finalizeOutput(
      final Path tempDir, final Path outputPath, final boolean expandedOutput) throws IOException {
    if (expandedOutput) {
      moveDirectory(tempDir, outputPath);
    } else {
      final Path zipFile = ensureZipExtension(outputPath);
      zipDirectory(tempDir, zipFile);
    }
  }

  private static Path ensureZipExtension(final Path outputPath) {
    if ("zip".equals(IOUtility.getFileExtension(outputPath))) {
      return outputPath;
    }
    return outputPath.resolveSibling(outputPath.getFileName() + ".zip");
  }

  private static void zipDirectory(final Path sourceDir, final Path zipFile) throws IOException {
    LOGGER.log(Level.INFO, new StringFormat("Writing Scribe report to ZIP file <%s>", zipFile));
    try (final ZipOutputStream zout =
        new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(zipFile)))) {
      try (final var paths = Files.walk(sourceDir)) {
        paths
            .filter(Files::isRegularFile)
            .forEach(
                path -> {
                  final String entryName = sourceDir.relativize(path).toString().replace('\\', '/');
                  try {
                    zout.putNextEntry(new ZipEntry(entryName));
                    Files.copy(path, zout);
                    zout.closeEntry();
                  } catch (final IOException e) {
                    throw new UncheckedIOException(e);
                  }
                });
      }
    }
  }

  private static void moveDirectory(final Path source, final Path target) throws IOException {
    LOGGER.log(Level.INFO, new StringFormat("Writing expanded Scribe output to <%s>", target));
    try {
      Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    } catch (final IOException e) {
      // Cross-device move (e.g. temp dir on different drive) — fall back to copy then delete
      copyDirectory(source, target);
    }
  }

  private static void copyDirectory(final Path source, final Path target) throws IOException {
    try (final var paths = Files.walk(source)) {
      for (final Path sourcePath : (Iterable<Path>) paths::iterator) {
        final Path targetPath = target.resolve(source.relativize(sourcePath));
        if (Files.isDirectory(sourcePath)) {
          Files.createDirectories(targetPath);
        } else {
          Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
      }
    }
  }

  private static void deleteDirectory(final Path dir) {
    try (final var paths = Files.walk(dir)) {
      paths
          .sorted(Comparator.reverseOrder())
          .forEach(
              path -> {
                try {
                  Files.deleteIfExists(path);
                } catch (final IOException e) {
                  // best-effort cleanup; log but don't rethrow
                  LOGGER.log(Level.WARNING, new StringFormat("Could not delete <%s>", path));
                }
              });
    } catch (final IOException e) {
      LOGGER.log(Level.WARNING, new StringFormat("Could not clean up temp directory <%s>", dir));
    }
  }
}

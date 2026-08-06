/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.command;

import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    final String title = getSchemaCrawlerOptions().title();

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

    try (final BundleDirectoryOutput outputDirectory =
        new BundleDirectoryOutput(outputPath, options.isExpandedOutput())) {
      renderer.render(support, outputDirectory);
    } catch (final Exception e) {
      throw new ExecutionRuntimeException("Could not generate Scribe report", e);
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
}

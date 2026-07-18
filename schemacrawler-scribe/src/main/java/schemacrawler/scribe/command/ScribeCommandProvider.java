/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.command;

import static schemacrawler.scribe.command.ScribeCommand.COMMAND;
import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;

import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.scribe.command.options.ScribeOptions;
import schemacrawler.scribe.command.options.ScribeOptionsBuilder;
import schemacrawler.scribe.command.options.ScribeOutputFormat;
import schemacrawler.tools.command.AbstractSchemaCrawlerCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;

/** Registers the Scribe command with the SchemaCrawler command line. */
public final class ScribeCommandProvider extends AbstractSchemaCrawlerCommandProvider {

  /** Creates the Scribe command provider, supporting only the {@code scribe} command. */
  public ScribeCommandProvider() {
    super(COMMAND);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Note: Supported output formats are listed in this command's help footer (see {@link
   * #outputFormatsFooter()}).
   */
  @Override
  public PluginCommand getCommandLineCommand() {
    final PluginCommand pluginCommand =
        newPluginCommand(
            COMMAND,
            () ->
                new String[] {
                  "For more information, see https://www.schemacrawler.com/scribe.html"
                },
            () -> new String[] {});
    pluginCommand
        .addOption(
            "language",
            String.class,
            "BCP 47 language tag for localized report text, for example, fr or de-DE; "
                + "the default is the system locale")
        .addOption("include-lint", Boolean.class, "Whether to include lint results in the report")
        .addOption(
            "expanded-output",
            Boolean.class,
            "Whether to write the report as expanded files and folders instead of a single ZIP "
                + "archive");
    return pluginCommand;
  }

  @Override
  public PluginCommand getHelpCommand() {
    final PluginCommand pluginCommand = getCommandLineCommand();
    pluginCommand.addOption(
        "output-format",
        ScribeOutputFormat.class,
        "Supported bundle formats",
        "<output-format> is one of ${COMPLETION-CANDIDATES}",
        "Optional");
    return pluginCommand;
  }

  /** {@inheritDoc} */
  @Override
  public ScribeCommand newCommand(final String command, final Config config) {
    if (!supportsCommand(command)) {
      throw new ExecutionRuntimeException("Unsupported command <%s>".formatted(command));
    }
    final ScribeOptions options = ScribeOptionsBuilder.builder().fromConfig(config).toOptions();
    final ScribeCommand scribeCommand = new ScribeCommand();
    scribeCommand.configure(options);
    return scribeCommand;
  }

  /** {@inheritDoc} */
  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    return supportsOutputFormat(command, outputOptions, ScribeOutputFormat::isSupportedFormat);
  }
}

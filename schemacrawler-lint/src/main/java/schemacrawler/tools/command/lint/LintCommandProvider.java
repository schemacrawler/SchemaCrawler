/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.lint;

import static schemacrawler.tools.command.lint.LintCommand.COMMAND;
import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;
import java.nio.file.Path;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.command.lint.options.LintOptionsBuilder;
import schemacrawler.tools.command.lint.options.LintReportOutputFormat;
import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.lint.LintDispatch;
import schemacrawler.tools.lint.LinterHelp;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;

public class LintCommandProvider extends BaseCommandProvider {

  public LintCommandProvider() {
    super(COMMAND);
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    final PluginCommand pluginCommand =
        newPluginCommand(
            COMMAND,
            () ->
                new String[] {
                  "For more information, see https://www.schemacrawler.com/lint.html %n"
                },
            new LinterHelp());
    pluginCommand
        .addOption(
            "linter-configs", Path.class, "Path to the SchemaCrawler linter configuration file")
        .addOption(
            "lint-dispatch",
            LintDispatch.class,
            "Specifies how to fail if a linter threshold is exceeded%n"
                + "Optional, defaults to none%n"
                + "Corresponds to the configuration file setting: schemacrawler.lint.lintdispatch")
        .addOption(
            "run-all-linters",
            boolean.class,
            "Whether to run all linters, including running the ones "
                + "that are not explicitly configured with their default settings%n"
                + "Optional, defaults to true%n"
                + "Corresponds to the configuration file setting: schemacrawler.lint.runalllinters");
    return pluginCommand;
  }

  @Override
  public PluginCommand getHelpCommand() {
    final PluginCommand pluginCommand = getCommandLineCommand();
    pluginCommand.addOption(
        "output-format",
        LintReportOutputFormat.class,
        "Supported lint report output formats",
        "<output-format> is one of ${COMPLETION-CANDIDATES}",
        "Optional, inferred from the extension of the output file");
    return pluginCommand;
  }

  @Override
  public LintCommand newSchemaCrawlerCommand(final String command, final Config config) {
    if (!supportsCommand(command)) {
      throw new IllegalArgumentException("Cannot support command, " + command);
    }

    final LintOptions lintOptions = LintOptionsBuilder.builder().fromConfig(config).toOptions();
    final LintCommand scCommand = new LintCommand();
    scCommand.configure(lintOptions);
    return scCommand;
  }

  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    return supportsOutputFormat(command, outputOptions, LintReportOutputFormat::isSupportedFormat);
  }
}

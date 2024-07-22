/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.lint;

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
import us.fatehi.utility.property.CommandDescription;

public class LintCommandProvider extends BaseCommandProvider {

  public static final String DESCRIPTION_HEADER =
      "Find lints (non-adherence to coding standards and conventions) " + "in the database schema";

  public LintCommandProvider() {
    super(new CommandDescription(LintCommand.COMMAND, DESCRIPTION_HEADER));
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    final PluginCommand pluginCommand =
        newPluginCommand(
            "lint",
            "** " + DESCRIPTION_HEADER,
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
    final LintOptions lintOptions = LintOptionsBuilder.builder().fromConfig(config).toOptions();
    final LintCommand scCommand = new LintCommand();
    scCommand.setCommandOptions(lintOptions);
    return scCommand;
  }

  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    return supportsOutputFormat(command, outputOptions, LintReportOutputFormat::isSupportedFormat);
  }
}

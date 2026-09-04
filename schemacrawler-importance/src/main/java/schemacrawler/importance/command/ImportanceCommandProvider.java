/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.command;

import static schemacrawler.importance.command.ImportanceCommand.COMMAND;
import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;

import schemacrawler.importance.options.ImportanceOptions;
import schemacrawler.importance.options.ImportanceOptionsBuilder;
import schemacrawler.importance.options.ImportanceReportOutputFormat;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.command.AbstractSchemaCrawlerCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;

/** Registers the importance command with the SchemaCrawler command line. */
public final class ImportanceCommandProvider extends AbstractSchemaCrawlerCommandProvider {

  public ImportanceCommandProvider() {
    super(COMMAND);
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    return newPluginCommand(COMMAND, () -> new String[] {}, () -> new String[] {})
        .addOption(
            "table-filter",
            String.class,
            "Regular expression for table and view full names to include in the report")
        .addOption(
            "max-tables",
            Integer.class,
            "Maximum number of tables to include in the report (default 5, <=0 for all)");
  }

  @Override
  public PluginCommand getHelpCommand() {
    return getCommandLineCommand()
        .addOption(
            "output-format",
            ImportanceReportOutputFormat.class,
            "Supported importance report output formats",
            "<output-format> is one of ${COMPLETION-CANDIDATES}",
            "Optional, inferred from the extension of the output file");
  }

  @Override
  public ImportanceCommand newCommand(final String command, final Config config) {
    if (!supportsCommand(command)) {
      throw new ExecutionRuntimeException("Unsupported command <%s>".formatted(command));
    }
    final ImportanceOptions options =
        ImportanceOptionsBuilder.builder().fromConfig(config).toOptions();
    final ImportanceCommand importanceCommand = new ImportanceCommand();
    importanceCommand.configure(options);
    return importanceCommand;
  }

  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    return supportsOutputFormat(
        command, outputOptions, ImportanceReportOutputFormat::isSupportedFormat);
  }
}

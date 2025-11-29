/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.text.diagram;

import schemacrawler.tools.command.text.diagram.options.DiagramOptions;
import schemacrawler.tools.command.text.diagram.options.DiagramOptionsBuilder;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.command.text.schema.options.CommandProviderUtility;
import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import us.fatehi.utility.property.PropertyName;

public final class DiagramCommandProvider extends BaseCommandProvider {

  public DiagramCommandProvider() {
    super(CommandProviderUtility.schemaTextCommands());
  }

  @Override
  public DiagramRenderer newSchemaCrawlerCommand(final String command, final Config config) {
    final PropertyName commandName = lookupSupportedCommand(command);
    if (commandName == null) {
      throw new IllegalArgumentException("Cannot support command, " + command);
    }
    final DiagramOptions diagramOptions =
        DiagramOptionsBuilder.builder().fromConfig(config).toOptions();
    final DiagramRenderer scCommand = new DiagramRenderer(commandName, new GraphExecutorFactory());
    scCommand.configure(diagramOptions);
    return scCommand;
  }

  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    return supportsOutputFormat(
        command,
        outputOptions,
        format -> {
          final DiagramOutputFormat diagramOutputFormat = DiagramOutputFormat.fromFormat(format);
          final boolean supportsOutputFormat =
              DiagramOutputFormat.isSupportedFormat(format)
                  && diagramOutputFormat != DiagramOutputFormat.htmlx;
          return supportsOutputFormat;
        });
  }
}

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.operation;

import java.util.ArrayList;
import java.util.Collection;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.command.text.operation.options.OperationOptions;
import schemacrawler.tools.command.text.operation.options.OperationOptionsBuilder;
import schemacrawler.tools.command.text.operation.options.OperationType;
import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import us.fatehi.utility.property.PropertyName;

public final class OperationCommandProvider extends BaseCommandProvider {

  private static Collection<PropertyName> operationCommands() {
    final Collection<PropertyName> supportedCommands = new ArrayList<>();
    for (final OperationType operation : OperationType.values()) {
      supportedCommands.add(new PropertyName(operation.name(), operation.getDescription()));
    }
    return supportedCommands;
  }

  public OperationCommandProvider() {
    super(operationCommands());
  }

  @Override
  public OperationCommand newSchemaCrawlerCommand(final String command, final Config config) {
    final PropertyName commandName;
    if (isNamedQuery(command, config)) {
      commandName = new PropertyName(command);
    } else {
      commandName = lookupSupportedCommand(command);
    }
    if (commandName == null) {
      throw new IllegalArgumentException("Cannot support command, " + command);
    }

    final OperationOptions operationOptions =
        OperationOptionsBuilder.builder().withCommand(command).fromConfig(config).toOptions();

    final OperationCommand scCommand = new OperationCommand(commandName);
    scCommand.configure(operationOptions);
    return scCommand;
  }

  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    return true;
  }

  @Override
  public boolean supportsSchemaCrawlerCommand(
      final String command,
      final SchemaCrawlerOptions schemaCrawlerOptions,
      final Config additionalConfig,
      final OutputOptions outputOptions) {
    // Check if the command is an operation
    final boolean isOperation = supportsCommand(command);
    // Check if the command is a query
    final boolean isNamedQuery = isNamedQuery(command, additionalConfig);

    // Operation and query output is only in text or HTML,
    // but nevertheless some operations such as count can be
    // represented on diagrams (since the catalog is annotated with attributes).
    // Also, if a query is part of a comma-separated list of commands,
    // the run should not fail due to a bad output format.
    // So no check is done for output format.
    final boolean supportsSchemaCrawlerCommand = isOperation || isNamedQuery;
    return supportsSchemaCrawlerCommand;
  }

  private boolean isNamedQuery(final String command, final Config additionalConfig) {
    /// Check if the command is a named query
    final boolean isNamedQuery;
    if (additionalConfig != null) {
      isNamedQuery = additionalConfig.containsKey(command);
    } else {
      isNamedQuery = false;
    }
    return isNamedQuery;
  }
}

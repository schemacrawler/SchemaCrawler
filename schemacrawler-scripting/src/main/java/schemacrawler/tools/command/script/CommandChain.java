/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.script;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import schemacrawler.tools.executable.CommandRegistry;
import schemacrawler.tools.executable.SchemaCrawlerCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.scripting.options.LanguageOptions;
import us.fatehi.utility.property.PropertyName;

/**
 * Allows chaining multiple executables together, that produce different artifacts, such as an image
 * and a HTML file.
 */
public final class CommandChain extends BaseSchemaCrawlerCommand<LanguageOptions> {

  private static final PropertyName COMMAND =
      new PropertyName(
          "chain", "Runs SchemaCrawler commands sequentially, each relying on the previous one");

  private static final Logger LOGGER = Logger.getLogger(CommandChain.class.getName());

  private final CommandRegistry commandRegistry;
  private final List<SchemaCrawlerCommand<?>> scCommands;
  private final Config additionalConfig;

  /**
   * Copy configuration settings from another command.
   *
   * @param scCommand Other command
   */
  public CommandChain(final ScriptCommand scCommand) {
    super(COMMAND);

    requireNonNull(scCommand, "No command provided, for settings");

    commandRegistry = CommandRegistry.getCommandRegistry();
    scCommands = new ArrayList<>();

    // Copy all configuration
    additionalConfig = ConfigUtility.fromConfig(scCommand.getCommandOptions().getConfig());
    setSchemaCrawlerOptions(scCommand.getSchemaCrawlerOptions());
    setOutputOptions(scCommand.getOutputOptions());

    setCatalog(scCommand.getCatalog());
    if (usesConnection()) {
      setConnection(scCommand.getConnection());
    }
    setIdentifiers(scCommand.getIdentifiers());
    setInformationSchemaViews(scCommand.getInformationSchemaViews());
  }

  public SchemaCrawlerCommand<?> addNext(
      final String command, final String outputFormat, final String outputFileName) {
    requireNonNull(command, "No command provided");
    requireNonNull(outputFormat, "No output format provided");
    requireNonNull(outputFileName, "No output file name provided");

    final Path outputFile = Path.of(outputFileName);
    final OutputOptions outputOptions =
        OutputOptionsBuilder.builder(getOutputOptions())
            .withOutputFormatValue(outputFormat)
            .withOutputFile(outputFile)
            .toOptions();

    return addNextAndConfigureForExecution(command, outputOptions);
  }

  @Override
  public void checkAvailability() {
    // Check the availability of the chain, even though there may be no
    // command in the chain until the actual point of execution
    checkAvailabilityChain();
  }

  @Override
  public void execute() {
    checkCatalog();

    initializeChain();
    checkAvailabilityChain();
    executeChain();
  }

  @Override
  public boolean usesConnection() {
    return false;
  }

  private SchemaCrawlerCommand<?> addNextAndConfigureForExecution(
      final String command, final OutputOptions outputOptions) {
    try {
      final SchemaCrawlerCommand<?> scCommand =
          commandRegistry.configureNewCommand(
              command, schemaCrawlerOptions, additionalConfig, outputOptions);
      if (scCommand == null) {
        return null;
      }

      scCommand.setCatalog(catalog);
      if (scCommand.usesConnection()) {
        scCommand.setConnection(connection);
      }
      scCommand.setIdentifiers(identifiers);
      scCommand.setInformationSchemaViews(informationSchemaViews);

      scCommands.add(scCommand);

      return scCommand;
    } catch (final Exception e) {
      throw new ExecutionRuntimeException(
          "Cannot chain command, unknown command <%s>".formatted(command), e);
    }
  }

  private void checkAvailabilityChain() {
    if (scCommands.isEmpty()) {
      LOGGER.log(Level.INFO, "No command to execute");
      return;
    }

    for (final SchemaCrawlerCommand<?> scCommand : scCommands) {
      try {
        scCommand.checkAvailability();
      } catch (final Exception e) {
        throw new InternalRuntimeException("Command <%s> is not available".formatted(scCommand));
      }
    }
  }

  private void executeChain() {
    if (scCommands.isEmpty()) {
      LOGGER.log(Level.INFO, "No command to execute");
      return;
    }

    for (final SchemaCrawlerCommand<?> scCommand : scCommands) {
      try {
        scCommand.call();
      } catch (final SchemaCrawlerException e) {
        throw e;
      } catch (final Exception e) {
        throw new ExecutionRuntimeException(e);
      }
    }
  }

  private void initializeChain() {
    if (scCommands.isEmpty()) {
      LOGGER.log(Level.INFO, "No command to initialize");
      return;
    }

    for (final SchemaCrawlerCommand<?> scCommand : scCommands) {
      scCommand.initialize();
    }
  }
}

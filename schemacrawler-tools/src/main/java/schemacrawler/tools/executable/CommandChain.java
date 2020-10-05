/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.executable;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import schemacrawler.SchemaCrawlerLogger;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;

/**
 * Allows chaining multiple executables together, that produce different artifacts, such as an image
 * and a HTML file.
 */
public final class CommandChain extends BaseSchemaCrawlerCommand {

  private static final String COMMAND = "chain";

  private static final SchemaCrawlerLogger LOGGER =
      SchemaCrawlerLogger.getLogger(CommandChain.class.getName());

  private final CommandRegistry commandRegistry;

  private final List<SchemaCrawlerCommand> scCommands;

  /**
   * Copy configuration settings from another command.
   *
   * @param scCommand Other command
   * @throws SchemaCrawlerException On an exception
   */
  public CommandChain(final SchemaCrawlerCommand scCommand) throws SchemaCrawlerException {
    super(COMMAND);

    requireNonNull(scCommand, "No command provided, for settings");

    commandRegistry = CommandRegistry.getCommandRegistry();
    scCommands = new ArrayList<>();

    // Copy all configuration
    setSchemaCrawlerOptions(scCommand.getSchemaCrawlerOptions());
    // Set command options also
    setOutputOptions(scCommand.getOutputOptions());

    setIdentifiers(scCommand.getIdentifiers());
    setCatalog(scCommand.getCatalog());
    setConnection(scCommand.getConnection());
  }

  public SchemaCrawlerCommand addNext(
      final String command, final OutputFormat outputFormat, final Path outputFile)
      throws SchemaCrawlerException {
    requireNonNull(command, "No command provided");
    requireNonNull(outputFormat, "No output format provided");
    requireNonNull(outputFile, "No output file provided");

    return addNext(
        command, outputFormat.getFormat(), outputFile.normalize().toAbsolutePath().toString());
  }

  public SchemaCrawlerCommand addNext(
      final String command, final String outputFormat, final String outputFileName)
      throws SchemaCrawlerException {
    requireNonNull(command, "No command provided");
    requireNonNull(outputFormat, "No output format provided");
    requireNonNull(outputFileName, "No output file name provided");

    final Path outputFile = Paths.get(outputFileName);
    final OutputOptions outputOptions =
        OutputOptionsBuilder.builder(getOutputOptions())
            .withOutputFormatValue(outputFormat)
            .withOutputFile(outputFile)
            .toOptions();

    return addNextAndConfigureForExecution(command, outputOptions);
  }

  @Override
  public void checkAvailability() throws Exception {
    // Check the availability of the chain, even though there may be no
    // command in the chain until the actual point of execution
    checkAvailabilityChain();
  }

  @Override
  public void execute() throws Exception {

    if (scCommands.isEmpty()) {
      LOGGER.log(Level.INFO, "No command to initialize");
      return;
    }

    checkCatalog();

    initializeChain();
    checkAvailabilityChain();
    executeChain();
  }

  @Override
  public CommandOptions getCommandOptions() {
    // No-op
    return null;
  }

  @Override
  public void setCommandOptions(CommandOptions commandOptions) {
    // No-op
  }

  @Override
  public boolean usesConnection() {
    return false;
  }

  private SchemaCrawlerCommand<?> addNextAndConfigureForExecution(
      final String command, final OutputOptions outputOptions) throws SchemaCrawlerException {
    try {
      final SchemaCrawlerCommand<?> scCommand =
          commandRegistry.configureNewCommand(
              command, schemaCrawlerOptions, new Config(), outputOptions);
      if (scCommand == null) {
        return null;
      }

      scCommand.setIdentifiers(identifiers);
      scCommand.setCatalog(catalog);
      scCommand.setConnection(connection);

      scCommands.add(scCommand);

      return scCommand;
    } catch (final SchemaCrawlerException e) {
      throw e;
    } catch (final Exception e) {
      throw new SchemaCrawlerException(
          String.format("Cannot chain command, unknown command <%s>", command));
    }
  }

  private void checkAvailabilityChain() throws Exception {
    for (final SchemaCrawlerCommand<?> scCommand : scCommands) {
      scCommand.checkAvailability();
    }
  }

  private void executeChain() throws Exception {
    for (final SchemaCrawlerCommand<?> scCommand : scCommands) {
      scCommand.execute();
    }
  }

  private void initializeChain() throws Exception {
    for (final SchemaCrawlerCommand<?> scCommand : scCommands) {
      scCommand.initialize();
    }
  }
}

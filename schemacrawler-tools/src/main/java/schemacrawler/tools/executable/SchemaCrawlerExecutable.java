/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.tools.utility.SchemaCrawlerUtility.matchSchemaRetrievalOptions;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.updateConnectionDataSource;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.string.ObjectToStringFormat;
import us.fatehi.utility.string.StringFormat;

/**
 * Wrapper executable for any SchemaCrawler command. Looks up the command registry, and instantiates
 * the registered executable for the command. If the command is not a known command,
 * SchemaCrawlerExecutable will check if it is a query configured in the properties. If not, it will
 * assume that a query is specified on the command-line, and execute that.
 */
public final class SchemaCrawlerExecutable {

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerExecutable.class.getName());

  private final String command;
  private Config additionalConfig;
  private Catalog catalog;
  private DatabaseConnectionSource dataSource;
  private OutputOptions outputOptions;
  private SchemaCrawlerOptions schemaCrawlerOptions;
  private SchemaRetrievalOptions schemaRetrievalOptions;

  public SchemaCrawlerExecutable(final String command) {
    this.command = requireNotBlank(command, "No command specified");

    schemaCrawlerOptions = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    outputOptions = OutputOptionsBuilder.newOutputOptions();
    additionalConfig = new Config();
  }

  public void execute() {

    // Match schema retrieval options, and update data source before any connections are used
    if (schemaRetrievalOptions == null) {
      schemaRetrievalOptions = matchSchemaRetrievalOptions(dataSource);
    }
    updateConnectionDataSource(dataSource, schemaRetrievalOptions);

    try (final Connection connection = dataSource.get(); ) {

      // Load the command to see if it is available
      // Fail early (before loading the catalog) if the command is not
      // available
      final SchemaCrawlerCommand<?> scCommand = loadCommand();

      // Set identifiers strategy
      scCommand.setIdentifiers(schemaRetrievalOptions.getIdentifiers());

      // Initialize, and check if the command is available
      scCommand.initialize();
      scCommand.checkAvailability();

      if (catalog == null) {
        loadCatalog();
      }

      // Prepare to execute
      scCommand.setCatalog(catalog);

      if (scCommand.usesConnection()) {
        scCommand.setConnection(connection);
      }

      // Execute
      LOGGER.log(Level.INFO, new StringFormat("Executing SchemaCrawler command <%s>", command));
      LOGGER.log(Level.CONFIG, new ObjectToStringFormat(scCommand.getIdentifiers()));
      LOGGER.log(Level.CONFIG, new ObjectToStringFormat(scCommand.getCommandOptions()));
      scCommand.execute();
    } catch (final SchemaCrawlerException e) {
      throw e;
    } catch (final Exception e) {
      throw new ExecutionRuntimeException(e);
    }
  }

  public Catalog getCatalog() {
    return catalog;
  }

  public OutputOptions getOutputOptions() {
    return outputOptions;
  }

  public SchemaCrawlerOptions getSchemaCrawlerOptions() {
    return schemaCrawlerOptions;
  }

  public SchemaRetrievalOptions getSchemaRetrievalOptions() {
    return schemaRetrievalOptions;
  }

  public void setAdditionalConfiguration(final Config additionalConfig) {
    // Make a defensive copy
    this.additionalConfig = new Config(additionalConfig);
  }

  public void setCatalog(final Catalog catalog) {
    this.catalog = catalog;
  }

  public void setDataSource(final DatabaseConnectionSource dataSource) {
    this.dataSource = requireNonNull(dataSource, "No data source provided");
  }

  public void setOutputOptions(final OutputOptions outputOptions) {
    if (outputOptions == null) {
      this.outputOptions = OutputOptionsBuilder.newOutputOptions();
    } else {
      this.outputOptions = outputOptions;
    }
  }

  public void setSchemaCrawlerOptions(final SchemaCrawlerOptions schemaCrawlerOptions) {
    if (schemaCrawlerOptions == null) {
      this.schemaCrawlerOptions = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    } else {
      this.schemaCrawlerOptions = schemaCrawlerOptions;
    }
  }

  public void setSchemaRetrievalOptions(final SchemaRetrievalOptions schemaRetrievalOptions) {
    this.schemaRetrievalOptions = schemaRetrievalOptions;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return command;
  }

  private void loadCatalog() {
    catalog =
        SchemaCrawlerUtility.getCatalog(
            dataSource, schemaRetrievalOptions, schemaCrawlerOptions, additionalConfig);
    requireNonNull(catalog, "Catalog could not be retrieved");
  }

  private SchemaCrawlerCommand<?> loadCommand() {
    final CommandRegistry commandRegistry = CommandRegistry.getCommandRegistry();
    final SchemaCrawlerCommand<?> scCommand =
        commandRegistry.configureNewCommand(
            command, schemaCrawlerOptions, additionalConfig, outputOptions);

    return requireNonNull(scCommand, "No SchemaCrawler command instantiated");
  }
}

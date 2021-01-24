/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static us.fatehi.utility.Utility.requireNotBlank;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import schemacrawler.SchemaCrawlerLogger;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.catalogloader.CatalogLoaderRegistry;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.string.ObjectToStringFormat;
import us.fatehi.utility.string.StringFormat;

/**
 * Wrapper executable for any SchemaCrawler command. Looks up the command registry, and instantiates
 * the registered executable for the command. If the command is not a known command,
 * SchemaCrawlerExecutable will check if it is a query configured in the properties. If not, it will
 * assume that a query is specified on the command-line, and execute that.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerExecutable {

  private static final SchemaCrawlerLogger LOGGER =
      SchemaCrawlerLogger.getLogger(SchemaCrawlerExecutable.class.getName());

  private final String command;
  private Config additionalConfiguration;
  private Catalog catalog;
  private Connection connection;
  private OutputOptions outputOptions;
  private SchemaCrawlerOptions schemaCrawlerOptions;
  private SchemaRetrievalOptions schemaRetrievalOptions;

  public SchemaCrawlerExecutable(final String command) {
    this.command = requireNotBlank(command, "No command specified");

    schemaCrawlerOptions = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    outputOptions = OutputOptionsBuilder.newOutputOptions();
    additionalConfiguration = new Config();
  }

  public void execute() throws Exception {

    if (schemaRetrievalOptions == null) {
      schemaRetrievalOptions = SchemaCrawlerUtility.matchSchemaRetrievalOptions(connection);
    }

    // Load the command to see if it is available
    // Fail early (before loading the catalog) if the command is not
    // available
    final SchemaCrawlerCommand<?> scCommand = loadCommand();

    // Set options
    scCommand.setIdentifiers(schemaRetrievalOptions.getIdentifiers());

    // Initialize, and check if the command is available
    scCommand.initialize();
    scCommand.checkAvailability();

    if (catalog == null) {
      loadCatalog();
    }

    // Prepare to execute
    scCommand.setCatalog(catalog);
    scCommand.setConnection(connection);

    // Execute
    LOGGER.log(Level.INFO, new StringFormat("Executing SchemaCrawler command <%s>", command));
    LOGGER.log(Level.CONFIG, new ObjectToStringFormat(scCommand.getIdentifiers()));
    LOGGER.log(Level.CONFIG, new ObjectToStringFormat(scCommand.getCommandOptions()));
    scCommand.execute();
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

  public boolean hasConnection() {
    if (connection == null) {
      return false;
    }
    try {
      final boolean closed = connection.isClosed();
      return !closed;
    } catch (final SQLException e) {
      LOGGER.log(Level.FINE, e.getMessage(), e);
      return true;
    }
  }

  public void setAdditionalConfiguration(final Config additionalConfiguration) {
    // Make a defensive copy
    this.additionalConfiguration = new Config(additionalConfiguration);
  }

  public void setCatalog(final Catalog catalog) {
    this.catalog = catalog;
  }

  public void setConnection(final Connection connection) {
    this.connection = requireNonNull(connection, "No connection provided");
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

  private void loadCatalog() throws Exception {
    final CatalogLoaderRegistry catalogLoaderRegistry = new CatalogLoaderRegistry();
    final CatalogLoader catalogLoader =
        catalogLoaderRegistry.lookupCatalogLoader(
            schemaRetrievalOptions.getDatabaseServerType().getDatabaseSystemIdentifier());
    LOGGER.log(Level.CONFIG, new StringFormat("Catalog loader: %s", getClass().getName()));

    catalogLoader.setConnection(connection);
    catalogLoader.setSchemaRetrievalOptions(schemaRetrievalOptions);
    catalogLoader.setSchemaCrawlerOptions(schemaCrawlerOptions);

    catalog = catalogLoader.loadCatalog();
    requireNonNull(catalog, "Catalog could not be retrieved");
  }

  private SchemaCrawlerCommand<?> loadCommand() throws SchemaCrawlerException {
    final CommandRegistry commandRegistry = CommandRegistry.getCommandRegistry();
    final SchemaCrawlerCommand<?> scCommand =
        commandRegistry.configureNewCommand(
            command, schemaCrawlerOptions, additionalConfiguration, outputOptions);
    if (scCommand == null) {
      throw new SchemaCrawlerException("Could not configure command, " + command);
    }

    return scCommand;
  }
}

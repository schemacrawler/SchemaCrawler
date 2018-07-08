/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.filter.ReducerFactory.getRoutineReducer;
import static schemacrawler.filter.ReducerFactory.getSchemaReducer;
import static schemacrawler.filter.ReducerFactory.getSequenceReducer;
import static schemacrawler.filter.ReducerFactory.getSynonymReducer;
import static schemacrawler.filter.ReducerFactory.getTableReducer;
import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.util.logging.Level;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Reducible;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.catalogloader.CatalogLoaderRegistry;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.utility.SchemaCrawlerUtility;
import sf.util.ObjectToString;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

/**
 * Wrapper executable for any SchemaCrawler command. Looks up the
 * command registry, and instantiates the registered executable for the
 * command. If the command is not a known command,
 * SchemaCrawlerExecutable will check if it is a query configured in the
 * properties. If not, it will assume that a query is specified on the
 * command-line, and execute that.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerExecutable
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SchemaCrawlerExecutable.class.getName());

  private final String command;
  private SchemaCrawlerOptions schemaCrawlerOptions;
  private OutputOptions outputOptions;
  private Config additionalConfiguration;
  private SchemaRetrievalOptions schemaRetrievalOptions;
  private Connection connection;

  public SchemaCrawlerExecutable(final String command)
  {
    if (isBlank(command))
    {
      throw new IllegalArgumentException("No command specified");
    }
    this.command = command;

    schemaCrawlerOptions = SchemaCrawlerOptionsBuilder
      .newSchemaCrawlerOptions();
    outputOptions = OutputOptionsBuilder.newOutputOptions();
    additionalConfiguration = new Config();
  }

  public final void execute()
    throws Exception
  {
    requireNonNull(connection, "No connection provided");

    if (schemaRetrievalOptions == null)
    {
      schemaRetrievalOptions = SchemaCrawlerUtility
        .matchSchemaRetrievalOptions(connection);
    }

    logExecution();

    // Load the command to see if it is available
    // Fail early (before loading the catalog) if the command is not
    // available
    final SchemaCrawlerCommand scCommand = loadCommand();

    final Catalog catalog = loadCatalog();

    executeCommand(scCommand, catalog);
  }

  public final void setAdditionalConfiguration(final Config additionalConfiguration)
  {
    // Make a defensive copy
    this.additionalConfiguration = new Config(additionalConfiguration);
  }

  public void setConnection(final Connection connection)
  {
    this.connection = requireNonNull(connection, "No connection provided");
  }

  public final void setOutputOptions(final OutputOptions outputOptions)
  {
    if (outputOptions == null)
    {
      this.outputOptions = OutputOptionsBuilder.newOutputOptions();
    }
    else
    {
      this.outputOptions = outputOptions;
    }
  }

  public final void setSchemaCrawlerOptions(final SchemaCrawlerOptions schemaCrawlerOptions)
  {
    if (schemaCrawlerOptions == null)
    {
      this.schemaCrawlerOptions = SchemaCrawlerOptionsBuilder
        .newSchemaCrawlerOptions();
    }
    else
    {
      this.schemaCrawlerOptions = schemaCrawlerOptions;
    }
  }

  public void setSchemaRetrievalOptions(final SchemaRetrievalOptions schemaRetrievalOptions)
  {
    this.schemaRetrievalOptions = schemaRetrievalOptions;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String toString()
  {
    return ObjectToString.toString(this);
  }

  private void executeCommand(final SchemaCrawlerCommand scCommand,
                              final Catalog catalog)
    throws Exception
  {
    // Reduce all once again, since the catalog may have been loaded
    // from an offline or other source
    ((Reducible) catalog).reduce(Schema.class,
                                 getSchemaReducer(schemaCrawlerOptions));
    ((Reducible) catalog).reduce(Table.class,
                                 getTableReducer(schemaCrawlerOptions));
    ((Reducible) catalog).reduce(Routine.class,
                                 getRoutineReducer(schemaCrawlerOptions));
    ((Reducible) catalog).reduce(Synonym.class,
                                 getSynonymReducer(schemaCrawlerOptions));
    ((Reducible) catalog).reduce(Sequence.class,
                                 getSequenceReducer(schemaCrawlerOptions));

    scCommand.setCatalog(catalog);
    scCommand.setConnection(connection);

    LOGGER.log(Level.INFO,
               new StringFormat("Executing command <%s> using <%s>",
                                command,
                                scCommand.getClass().getName()));
    scCommand.beforeExecute();
    scCommand.execute();
  }

  private Catalog loadCatalog()
    throws SchemaCrawlerException, Exception
  {
    final CatalogLoaderRegistry catalogLoaderRegistry = new CatalogLoaderRegistry();
    final CatalogLoader catalogLoader = catalogLoaderRegistry
      .lookupCatalogLoader(schemaRetrievalOptions.getDatabaseServerType()
        .getDatabaseSystemIdentifier());
    LOGGER
      .log(Level.CONFIG,
           new StringFormat("Catalog loader: %s", this.getClass().getName()));

    catalogLoader.setAdditionalConfiguration(additionalConfiguration);
    catalogLoader.setConnection(connection);
    catalogLoader.setSchemaRetrievalOptions(schemaRetrievalOptions);
    catalogLoader.setSchemaCrawlerOptions(schemaCrawlerOptions);

    final Catalog catalog = catalogLoader.loadCatalog();
    requireNonNull(catalog, "Catalog could not be retrieved");

    return catalog;
  }

  private SchemaCrawlerCommand loadCommand()
    throws SchemaCrawlerException
  {
    final Commands commands = new Commands(command);
    if (commands.isEmpty())
    {
      throw new SchemaCrawlerException("No command specified");
    }

    final CommandRegistry commandRegistry = new CommandRegistry();
    final SchemaCrawlerCommand scCommand;
    if (commands.hasMultipleCommands())
    {
      // NOTE: The daisy chain command may change the provided output
      // options for each chained command
      scCommand = new CommandDaisyChain(command);
      scCommand.setSchemaCrawlerOptions(schemaCrawlerOptions);
      scCommand.setOutputOptions(outputOptions);
    }
    else
    {
      scCommand = commandRegistry
        .configureNewCommand(command, schemaCrawlerOptions, outputOptions);
    }

    scCommand.setAdditionalConfiguration(additionalConfiguration);
    scCommand.setIdentifiers(schemaRetrievalOptions.getIdentifiers());

    return scCommand;
  }

  private void logExecution()
  {
    if (LOGGER.isLoggable(Level.INFO))
    {
      LOGGER
        .log(Level.INFO,
             String.format("Executing SchemaCrawler command <%s>", command));

      if (LOGGER.isLoggable(Level.CONFIG))
      {
        LOGGER.log(Level.CONFIG,
                   String.format("Executable: %s", this.getClass().getName()));
        LOGGER.log(Level.CONFIG, ObjectToString.toString(schemaCrawlerOptions));
        LOGGER.log(Level.CONFIG, ObjectToString.toString(outputOptions));
        LOGGER.log(Level.CONFIG, schemaRetrievalOptions.toString());

        if (LOGGER.isLoggable(Level.FINE))
        {
          LOGGER.log(Level.FINE,
                     ObjectToString.toString(additionalConfiguration));
        }
      }
    }
  }

}

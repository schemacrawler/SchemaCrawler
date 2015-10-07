/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.tools.offline;


import static java.util.Objects.requireNonNull;
import static schemacrawler.filter.FilterFactory.routineFilter;
import static schemacrawler.filter.FilterFactory.tableFilter;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.crawl.RoutinesReducer;
import schemacrawler.crawl.SchemasReducer;
import schemacrawler.crawl.SequencesReducer;
import schemacrawler.crawl.SynonymsReducer;
import schemacrawler.crawl.TablesReducer;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Reducible;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.executable.StagedExecutable;
import schemacrawler.tools.integration.serialization.XmlSerializedCatalog;
import schemacrawler.tools.options.OutputOptions;

/**
 * A SchemaCrawler tools executable unit.
 *
 * @author Sualeh Fatehi
 */
public class OfflineSnapshotExecutable
  extends BaseExecutable
  implements StagedExecutable
{

  private static final Logger LOGGER = Logger
    .getLogger(OfflineSnapshotExecutable.class.getName());

  private OutputOptions inputOptions;

  protected OfflineSnapshotExecutable(final String command)
  {
    super(command);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.executable.Executable#execute(java.sql.Connection)
   */
  @Override
  public final void execute(final Connection connection)
    throws Exception
  {
    checkConnection(connection);

    inputOptions = new OutputOptions();
    inputOptions.setCompressedInputFile(((OfflineConnection) connection)
      .getOfflineDatabasePath());

    final Catalog catalog = loadCatalog();

    executeOn(catalog, connection);
  }

  @Override
  public void
    execute(final Connection connection,
            final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
              throws Exception
  {
    execute(connection);
  }

  @Override
  public void executeOn(final Catalog catalog, final Connection connection)
    throws Exception
  {
    loadOfflineSnapshotOptions();
    checkConnection(connection);

    requireNonNull(catalog, "No catalog provided");

    // Reduce all
    ((Reducible) catalog).reduce(Schema.class,
                                 new SchemasReducer(schemaCrawlerOptions));
    final Predicate<Table> tableFilter = tableFilter(schemaCrawlerOptions);
    ((Reducible) catalog)
      .reduce(Table.class,
              new TablesReducer(schemaCrawlerOptions, tableFilter));
    final Predicate<Routine> routineFilter = routineFilter(schemaCrawlerOptions);
    ((Reducible) catalog).reduce(Routine.class,
                                 new RoutinesReducer(routineFilter));
    ((Reducible) catalog).reduce(Synonym.class,
                                 new SynonymsReducer(schemaCrawlerOptions));
    ((Reducible) catalog).reduce(Sequence.class,
                                 new SequencesReducer(schemaCrawlerOptions));

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(additionalConfiguration);
    executable.setOutputOptions(outputOptions);
    executable.executeOn(catalog, connection);
  }

  public void setInputOptions(final OutputOptions inputOptions)
  {
    this.inputOptions = inputOptions;
  }

  private void checkConnection(final Connection connection)
  {
    if (connection == null || !(connection instanceof OfflineConnection))
    {
      LOGGER
        .log(Level.SEVERE,
             "Offline database connection not provided for the offline snapshot");
    }
  }

  private Catalog loadCatalog()
    throws SchemaCrawlerException
  {
    final Reader snapshotReader;
    try
    {
      snapshotReader = inputOptions.openNewInputReader();
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException("Cannot open input reader", e);
    }

    final XmlSerializedCatalog xmlDatabase = new XmlSerializedCatalog(snapshotReader);
    return xmlDatabase;
  }

  private void loadOfflineSnapshotOptions()
  {
    if (inputOptions == null)
    {
      inputOptions = new OutputOptions(additionalConfiguration);
    }
  }

}

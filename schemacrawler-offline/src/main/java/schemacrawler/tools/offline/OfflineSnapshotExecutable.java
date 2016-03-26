/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.offline;


import static schemacrawler.filter.FilterFactory.routineFilter;
import static schemacrawler.filter.FilterFactory.tableFilter;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

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
import schemacrawler.tools.offline.jdbc.OfflineConnection;
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

  @Override
  public void execute(final Connection connection,
                      final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
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

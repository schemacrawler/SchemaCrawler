/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
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

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Reducible;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.executable.StagedExecutable;
import schemacrawler.tools.integration.serialization.XmlSerializedCatalog;
import schemacrawler.tools.iosource.InputReader;
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
    ((Reducible) catalog).reduce(schemaCrawlerOptions);

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
    if (connection != null)
    {
      LOGGER
        .log(Level.CONFIG,
             "No database connection should be provided for the offline snapshot");
    }
  }

  private Catalog loadCatalog()
    throws SchemaCrawlerException
  {
    final InputReader snapshotReader = new InputReader(inputOptions.obtainInputResource(),
                                                       inputOptions
                                                         .getInputCharset());
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

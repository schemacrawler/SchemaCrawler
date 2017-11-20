/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static sf.util.DatabaseUtility.checkConnection;

import java.sql.Connection;
import java.util.logging.Level;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.utility.SchemaCrawlerUtility;
import sf.util.ObjectToString;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

/**
 * A SchemaCrawler tools executable unit.
 *
 * @author Sualeh Fatehi
 */
public abstract class BaseStagedExecutable
  extends BaseExecutable
  implements StagedExecutable
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(BaseStagedExecutable.class.getName());

  protected BaseStagedExecutable(final String command)
  {
    super(command);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void execute(final Connection connection,
                            final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
    throws Exception
  {
    requireNonNull(connection, "No connection provided");
    requireNonNull(databaseSpecificOverrideOptions,
                   "No database specific overrides provided");

    LOGGER.log(Level.INFO,
               new StringFormat("Executing SchemaCrawler command <%s>",
                                getCommand()));
    if (LOGGER.isLoggable(Level.CONFIG))
    {
      LOGGER.log(Level.CONFIG,
                 String.format("Executable: %s", this.getClass().getName()));
      LOGGER.log(Level.CONFIG, ObjectToString.toString(schemaCrawlerOptions));
      LOGGER.log(Level.CONFIG, ObjectToString.toString(outputOptions));
    }
    if (LOGGER.isLoggable(Level.FINE))
    {
      LOGGER.log(Level.FINE, ObjectToString.toString(additionalConfiguration));
    }

    final SchemaCrawler schemaCrawler = new SchemaCrawler(connection,
                                                          databaseSpecificOverrideOptions);
    final Catalog catalog = schemaCrawler.crawl(schemaCrawlerOptions);

    executeOn(catalog, connection, databaseSpecificOverrideOptions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void executeOn(final Catalog catalog,
                              final Connection connection)
    throws Exception
  {
    requireNonNull(catalog, "No catalog provided");
    checkConnection(connection);

    final DatabaseSpecificOverrideOptions dbSpecificOverrideOptions = SchemaCrawlerUtility
      .matchDatabaseSpecificOverrideOptions(connection);
    executeOn(catalog, connection, dbSpecificOverrideOptions);
  }

}

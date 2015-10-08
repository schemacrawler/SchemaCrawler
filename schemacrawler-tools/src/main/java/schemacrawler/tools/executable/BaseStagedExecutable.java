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

package schemacrawler.tools.executable;


import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import sf.util.ObjectToString;

/**
 * A SchemaCrawler tools executable unit.
 *
 * @author Sualeh Fatehi
 */
public abstract class BaseStagedExecutable
  extends BaseExecutable
  implements StagedExecutable
{

  private static final Logger LOGGER = Logger
    .getLogger(BaseStagedExecutable.class.getName());

  protected BaseStagedExecutable(final String command)
  {
    super(command);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.executable.Executable#execute(Connection,
   *      DatabaseSpecificOverrideOptions))
   */
  @Override
  public final void execute(final Connection connection,
                            final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
                              throws Exception
  {
    requireNonNull(connection, "No connection provided");
    requireNonNull(databaseSpecificOverrideOptions,
                   "No database specific overrides provided");

    LOGGER.log(Level.INFO, "Executing SchemaCrawler command, " + getCommand());
    LOGGER.log(Level.CONFIG, ObjectToString.toString(schemaCrawlerOptions));
    LOGGER.log(Level.CONFIG, ObjectToString.toString(outputOptions));
    LOGGER.log(Level.FINE, ObjectToString.toString(additionalConfiguration));

    final SchemaCrawler crawler = new SchemaCrawler(connection,
                                                    databaseSpecificOverrideOptions);
    final Catalog catalog = crawler.crawl(schemaCrawlerOptions);

    executeOn(catalog, connection);
  }

}

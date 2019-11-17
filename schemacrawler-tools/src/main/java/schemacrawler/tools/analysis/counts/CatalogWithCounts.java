/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.analysis.counts;


import static schemacrawler.filter.ReducerFactory.getTableReducer;
import static schemacrawler.tools.analysis.counts.CountsUtility.addRowCountToTable;
import static schemacrawler.utility.QueryUtility.executeForLong;
import static sf.util.DatabaseUtility.checkConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.BaseCatalogDecorator;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.utility.Identifiers;
import schemacrawler.utility.Query;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

public final class CatalogWithCounts
  extends BaseCatalogDecorator
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(CatalogWithCounts.class.getName());

  private static final long serialVersionUID = -3953296149824921463L;

  private final Map<Table, Long> counts;

  public CatalogWithCounts(final Catalog catalog,
                           final Connection connection,
                           final SchemaCrawlerOptions options)
    throws SchemaCrawlerException
  {
    super(catalog);

    counts = new HashMap<>();

    Identifiers identifiers;
    try
    {
      checkConnection(connection);
      identifiers = Identifiers.identifiers().withConnection(connection)
        .build();
    }
    catch (final SQLException e)
    {
      // The offline snapshot executable may not have a live connection,
      // so we cannot fail with an exception. Log and continue.
      LOGGER.log(Level.WARNING, "No connection provided", e);

      identifiers = Identifiers.identifiers().withIdentifierQuoteString("\"")
        .build();

      return;
    }

    final Query query = Operation.count.getQuery();
    final List<Table> allTables = new ArrayList<>(catalog.getTables());
    for (final Table table: allTables)
    {
      try
      {
        final long count = executeForLong(query,
                                          connection,
                                          table,
                                          identifiers);
        counts.put(table, count);
        addRowCountToTable(table, count);
      }
      catch (final SchemaCrawlerException e)
      {
        LOGGER.log(Level.WARNING,
                   new StringFormat("Could not get count for table <%s>",
                                    table),
                   e);
      }
    }

    reduce(Table.class, getTableReducer(new TableCountFilter(options)));
  }

  public Map<Table, Long> getCounts()
  {
    return counts;
  }

}

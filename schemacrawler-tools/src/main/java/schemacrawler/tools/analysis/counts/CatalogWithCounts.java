/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package schemacrawler.tools.analysis.counts;


import static schemacrawler.tools.analysis.counts.CountsUtility.addCountToTable;
import static sf.util.DatabaseUtility.checkConnection;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.crawl.TablesReducer;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.BaseCatalogDecorator;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.utility.Query;

public final class CatalogWithCounts
  extends BaseCatalogDecorator
{

  private static final Logger LOGGER = Logger
    .getLogger(CatalogWithCounts.class.getName());

  private static final long serialVersionUID = -3953296149824921463L;

  private final Map<Table, Long> counts;

  public CatalogWithCounts(final Catalog catalog, final Connection connection,
                           final SchemaCrawlerOptions options)
                             throws SchemaCrawlerException
  {
    super(catalog);

    counts = new HashMap<>();

    try
    {
      checkConnection(connection);
    }
    catch (final SchemaCrawlerException e)
    {
      // The offline snapshot executable may not have a live connection,
      // so we cannot fail with an exception. Log and continue.
      LOGGER.log(Level.WARNING, "No connection provided", e);
      return;
    }

    final Query query = Operation.count.getQuery();
    final List<Table> allTables = new ArrayList<>(catalog.getTables());
    for (final Table table: allTables)
    {
      try
      {
        final long count = query.executeForLong(connection, table);
        counts.put(table, count);
        addCountToTable(table, count);
      }
      catch (final SchemaCrawlerException e)
      {
        LOGGER.log(Level.WARNING, "Could not get count for " + table, e);
      }
    }

    reduce(Table.class,
           new TablesReducer(options, new TableCountFilter(options)));
  }

  public Map<Table, Long> getCounts()
  {
    return counts;
  }

}

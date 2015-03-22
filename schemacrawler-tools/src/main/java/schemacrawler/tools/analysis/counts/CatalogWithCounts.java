/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
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


import static sf.util.DatabaseUtility.checkConnection;
import static sf.util.DatabaseUtility.executeSqlForScalar;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.BaseCatalogDecorator;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.operation.Query;

public final class CatalogWithCounts
  extends BaseCatalogDecorator
{

  private static final Logger LOGGER = Logger.getLogger(CatalogWithCounts.class
    .getName());

  private static final long serialVersionUID = -3953296149824921463L;

  private final Map<Table, Long> counts;

  public CatalogWithCounts(final Connection connection, final Catalog catalog)
    throws SchemaCrawlerException
  {
    super(catalog);

    try
    {
      checkConnection(connection);
    }
    catch (SQLException e)
    {
      throw new SchemaCrawlerException("No connection provided", e);
    }

    counts = new HashMap<>();
    final Query query = Operation.count.getQuery();
    final List<Table> allTables = new ArrayList<>(catalog.getTables());
    for (final Table table: allTables)
    {
      try
      {
        final long count = executeSqlForScalar(connection,
                                               query.getQueryForTable(table,
                                                                      false));
        counts.put(table, count);
      }
      catch (final SchemaCrawlerException e)
      {
        LOGGER.log(Level.WARNING, "Could not get count for " + table, e);
      }
    }
  }

  public Map<Table, Long> getCounts()
  {
    return counts;
  }

}

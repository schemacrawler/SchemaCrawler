/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.analysis.counts;

import static java.util.Objects.requireNonNull;
import static schemacrawler.analysis.counts.TableRowCountsUtility.addRowCountToTable;
import static schemacrawler.schemacrawler.QueryUtility.executeForLong;
import static us.fatehi.utility.DatabaseUtility.checkConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import schemacrawler.SchemaCrawlerLogger;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.Retriever;
import us.fatehi.utility.string.StringFormat;

@Retriever
public final class TableRowCountsRetriever {

  private static final SchemaCrawlerLogger LOGGER =
      SchemaCrawlerLogger.getLogger(TableRowCountsRetriever.class.getName());

  private final Connection connection;
  private final Catalog catalog;

  public TableRowCountsRetriever(final Connection connection, final Catalog catalog)
      throws SQLException {
    this.connection = checkConnection(connection);
    this.catalog = requireNonNull(catalog, "No catalog provided");
  }

  public void retrieveTableRowCounts() {

    Identifiers identifiers;
    try {
      identifiers = Identifiers.identifiers().withConnection(connection).build();
    } catch (final SQLException e) {
      // The offline snapshot executable may not have a live connection,
      // so we cannot fail with an exception. Log and continue.
      LOGGER.log(Level.WARNING, "No connection provided", e);

      identifiers = Identifiers.identifiers().withIdentifierQuoteString("\"").build();

      return;
    }

    final Query query =
        new Query("schemacrawler.table.row_counts", "SELECT COUNT(*) FROM ${table}");
    final List<Table> allTables = new ArrayList<>(catalog.getTables());
    for (final Table table : allTables) {
      try {
        final long count = executeForLong(query, connection, table, identifiers);
        addRowCountToTable(table, count);
      } catch (final SQLException e) {
        LOGGER.log(Level.WARNING, new StringFormat("Could not get count for table <%s>", table), e);
      }
    }
  }
}

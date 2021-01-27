/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static schemacrawler.filter.ReducerFactory.getTableReducer;

import java.util.logging.Level;

import schemacrawler.SchemaCrawlerLogger;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.catalogloader.BaseCatalogLoader;
import us.fatehi.utility.StopWatch;

public class TableRowCountsCatalogLoader extends BaseCatalogLoader {

  private static final SchemaCrawlerLogger LOGGER =
      SchemaCrawlerLogger.getLogger(TableRowCountsCatalogLoader.class.getName());

  public TableRowCountsCatalogLoader() {
    super(2);
  }

  @Override
  public void loadCatalog() throws SchemaCrawlerException {
    if (!isLoaded()) {
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving table row counts");
    final StopWatch stopWatch = new StopWatch("loadTableRowCounts");
    try {
      final Catalog catalog = getCatalog();
      final TableRowCountsRetriever rowCountsRetriever =
          new TableRowCountsRetriever(getConnection(), catalog);
      final SchemaCrawlerOptions options = getSchemaCrawlerOptions();
      stopWatch.time(
          "retrieveTableRowCounts",
          () -> {
            final boolean loadRowCounts = options.getLoadOptions().isLoadRowCounts();
            if (loadRowCounts) {
              rowCountsRetriever.retrieveTableRowCounts();
            } else {
              LOGGER.log(
                  Level.INFO, "Not retrieving table row counts, since this was not requested");
            }
            return null;
          });

      stopWatch.time(
          "filterEmptyTables",
          () -> {
            catalog.reduce(
                Table.class, getTableReducer(new TableRowCountsFilter(options.getFilterOptions())));
            return null;
          });

      LOGGER.log(Level.INFO, stopWatch.stringify());
    } catch (final Exception e) {
      throw new SchemaCrawlerException("Exception retrieving table row counts", e);
    }
  }
}

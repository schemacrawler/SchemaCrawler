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
import schemacrawler.tools.catalogloader.BaseCatalogLoader;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.StopWatch;

public class TableRowCountsCatalogLoader extends BaseCatalogLoader {

  private static final SchemaCrawlerLogger LOGGER =
      SchemaCrawlerLogger.getLogger(TableRowCountsCatalogLoader.class.getName());

  private static final String OPTION_NO_EMPTY_TABLES = "no-empty-tables";
  private static final String OPTION_LOAD_ROW_COUNTS = "load-row-counts";

  public TableRowCountsCatalogLoader() {
    super(2);
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    final PluginCommand pluginCommand =
        PluginCommand.newCatalogLoaderCommand(this.getClass().getName(), "Catalog load options");
    pluginCommand
        .addOption(
            OPTION_LOAD_ROW_COUNTS,
            Boolean.class,
            "Loads row counts for each table",
            "This can be a time consuming operation",
            "Optional, defaults to false")
        .addOption(
            OPTION_NO_EMPTY_TABLES,
            Boolean.class,
            "Includes only tables that have rows of data",
            "Requires table row counts to be loaded",
            "Optional, default is false");
    return pluginCommand;
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
      final Config config = getAdditionalConfiguration();
      stopWatch.time(
          "retrieveTableRowCounts",
          () -> {
            final boolean loadRowCounts = config.getBooleanValue(OPTION_LOAD_ROW_COUNTS, false);
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
            final boolean noEmptyTables = config.getBooleanValue(OPTION_NO_EMPTY_TABLES, false);
            catalog.reduce(Table.class, getTableReducer(new TableRowCountsFilter(noEmptyTables)));
            return null;
          });

      LOGGER.log(Level.INFO, stopWatch.stringify());
    } catch (final Exception e) {
      throw new SchemaCrawlerException("Exception retrieving table row counts", e);
    }
  }
}

/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.loader.counts;

import static schemacrawler.filter.ReducerFactory.getTableReducer;

import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.catalogloader.BaseCatalogLoader;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.scheduler.TaskDefinition;
import us.fatehi.utility.scheduler.TaskRunner;
import us.fatehi.utility.scheduler.TaskRunners;

public class TableRowCountsCatalogLoader extends BaseCatalogLoader {

  private static final Logger LOGGER =
      Logger.getLogger(TableRowCountsCatalogLoader.class.getName());

  private static final String OPTION_NO_EMPTY_TABLES = "no-empty-tables";
  private static final String OPTION_LOAD_ROW_COUNTS = "load-row-counts";

  public TableRowCountsCatalogLoader() {
    super(new CommandDescription("countsloader", "Loader for table row counts"), 2);
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    final CommandDescription commandDescription = getCommandDescription();
    final PluginCommand pluginCommand =
        PluginCommand.newCatalogLoaderCommand(
            commandDescription.getName(), commandDescription.getDescription());
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
  public void loadCatalog() {
    if (!isLoaded()) {
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving table row counts");
    final TaskRunner taskRunner = TaskRunners.getTaskRunner("loadTableRowCounts", 1);
    try {
      final Catalog catalog = getCatalog();
      final TableRowCountsRetriever rowCountsRetriever =
          new TableRowCountsRetriever(getDataSource(), catalog);
      final Config config = getAdditionalConfiguration();
      taskRunner.run(
          new TaskDefinition(
              "retrieveTableRowCounts",
              () -> {
                final boolean loadRowCounts = config.getBooleanValue(OPTION_LOAD_ROW_COUNTS, false);
                if (loadRowCounts) {
                  rowCountsRetriever.retrieveTableRowCounts();
                } else {
                  LOGGER.log(
                      Level.INFO, "Not retrieving table row counts, since this was not requested");
                }
              }));

      taskRunner.run(
          new TaskDefinition(
              "filterEmptyTables",
              () -> {
                final boolean noEmptyTables = config.getBooleanValue(OPTION_NO_EMPTY_TABLES, false);
                catalog.reduce(
                    Table.class, getTableReducer(new TableRowCountsFilter(noEmptyTables)));
              }));

      taskRunner.stop();
      LOGGER.log(Level.INFO, taskRunner.report());
    } catch (final Exception e) {
      throw new ExecutionRuntimeException("Exception retrieving table row counts", e);
    }
  }
}

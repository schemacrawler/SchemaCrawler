/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.loader.counts;

import static schemacrawler.filter.ReducerFactory.getTableReducer;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.catalogloader.BaseCatalogLoader;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.scheduler.TaskDefinition;
import us.fatehi.utility.scheduler.TaskRunner;
import us.fatehi.utility.scheduler.TaskRunners;

public class TableRowCountsCatalogLoader extends BaseCatalogLoader {

  private static final Logger LOGGER =
      Logger.getLogger(TableRowCountsCatalogLoader.class.getName());

  private static final String OPTION_NO_EMPTY_TABLES = "no-empty-tables";
  private static final String OPTION_LOAD_ROW_COUNTS = "load-row-counts";

  public TableRowCountsCatalogLoader() {
    super(new PropertyName("countsloader", "Loader for table row counts"), 2);
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    final PropertyName catalogLoaderName = getCatalogLoaderName();
    final PluginCommand pluginCommand = PluginCommand.newCatalogLoaderCommand(catalogLoaderName);
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
    try (final TaskRunner taskRunner = TaskRunners.getTaskRunner("loadTableRowCounts", 1); ) {
      final Catalog catalog = getCatalog();
      final TableRowCountsRetriever rowCountsRetriever =
          new TableRowCountsRetriever(getDataSource(), catalog);
      final Config config = getAdditionalConfiguration();
      taskRunner.add(
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
      taskRunner.submit();

      taskRunner.add(
          new TaskDefinition(
              "filterEmptyTables",
              () -> {
                final boolean noEmptyTables = config.getBooleanValue(OPTION_NO_EMPTY_TABLES, false);
                catalog.reduce(
                    Table.class, getTableReducer(new TableRowCountsFilter(noEmptyTables)));
              }));
      taskRunner.submit();

      LOGGER.log(Level.INFO, taskRunner.report());
    } catch (final Exception e) {
      throw new ExecutionRuntimeException("Exception retrieving table row counts", e);
    }
  }
}

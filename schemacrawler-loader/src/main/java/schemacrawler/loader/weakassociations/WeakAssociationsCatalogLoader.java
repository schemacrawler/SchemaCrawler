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
package schemacrawler.loader.weakassociations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.crawl.WeakAssociationBuilder;
import schemacrawler.crawl.WeakAssociationBuilder.WeakAssociationColumn;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.catalogloader.BaseCatalogLoader;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.scheduler.MainThreadTaskRunner;
import us.fatehi.utility.scheduler.TaskDefinition;
import us.fatehi.utility.scheduler.TaskRunner;
import us.fatehi.utility.string.StringFormat;

public final class WeakAssociationsCatalogLoader extends BaseCatalogLoader {

  private static final Logger LOGGER =
      Logger.getLogger(WeakAssociationsCatalogLoader.class.getName());

  private static final String OPTION_WEAK_ASSOCIATIONS = "weak-associations";

  public WeakAssociationsCatalogLoader() {
    super(new CommandDescription("weakassociationsloader", "Loader for weak associations"), 3);
  }

  public void findWeakAssociations() {
    final Catalog catalog = getCatalog();
    final List<Table> allTables = new ArrayList<>(catalog.getTables());
    final WeakAssociationsAnalyzer weakAssociationsAnalyzer =
        new WeakAssociationsAnalyzer(allTables);
    final Collection<ProposedWeakAssociation> proposedWeakAssociations =
        weakAssociationsAnalyzer.analyzeTables();

    for (final ProposedWeakAssociation proposedWeakAssociation : proposedWeakAssociations) {
      LOGGER.log(
          Level.INFO, new StringFormat("Adding weak association <%s> ", proposedWeakAssociation));

      final Column fkColumn = proposedWeakAssociation.getForeignKeyColumn();
      final Column pkColumn = proposedWeakAssociation.getPrimaryKeyColumn();

      final WeakAssociationBuilder builder = WeakAssociationBuilder.builder(catalog);
      builder.addColumnReference(
          new WeakAssociationColumn(fkColumn), new WeakAssociationColumn(pkColumn));
      builder.build();
    }
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    final CommandDescription commandDescription = getCommandDescription();
    final PluginCommand pluginCommand =
        PluginCommand.newCatalogLoaderCommand(
            commandDescription.getName(), commandDescription.getDescription());
    pluginCommand.addOption(
        OPTION_WEAK_ASSOCIATIONS,
        Boolean.class,
        "Analyzes the schema to find weak associations between tables, based on table and column naming patterns",
        "This can be a time consuming operation",
        "Optional, defaults to false");
    return pluginCommand;
  }

  @Override
  public void loadCatalog() {
    if (!isLoaded()) {
      return;
    }

    final TaskRunner taskRunner = new MainThreadTaskRunner("loadWeakAssociations");

    LOGGER.log(Level.INFO, "Finding weak associations");
    try {
      taskRunner.run(
          new TaskDefinition(
              "retrieveWeakAssociations",
              () -> {
                final Config config = getAdditionalConfiguration();
                final boolean findWeakAssociations =
                    config.getBooleanValue(OPTION_WEAK_ASSOCIATIONS, false);
                if (findWeakAssociations) {
                  findWeakAssociations();
                } else {
                  LOGGER.log(
                      Level.INFO, "Not retrieving weak associations, since this was not requested");
                }
              }));

      taskRunner.stop();
      LOGGER.log(Level.INFO, taskRunner.report());
    } catch (final Exception e) {
      throw new ExecutionRuntimeException("Exception retrieving weak association information", e);
    }
  }
}

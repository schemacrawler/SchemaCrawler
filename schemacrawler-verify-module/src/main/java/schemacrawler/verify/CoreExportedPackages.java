/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.verify;

import java.util.List;
import schemacrawler.ermodel.associations.ImplicitAssociationAnalyzer;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.ermodel.utility.ERModelUtility;
import schemacrawler.filter.ReducerFactory;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.loader.catalog.CatalogLoader;
import schemacrawler.loader.catalog.model.CatalogAttributes;
import schemacrawler.loader.catalog.summary.CatalogStats;
import schemacrawler.loader.ermodel.ERModelLoader;
import schemacrawler.loader.ermodel.summary.ERModelStats;
import schemacrawler.loader.utility.TableRowCountsUtility;
import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.command.CommandProvider;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.registry.PluginRegistry;
import schemacrawler.tools.state.ExecutionState;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import schemacrawler.utility.MetaDataUtility;
import us.fatehi.utility.Builder;
import us.fatehi.utility.database.DatabaseUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.graph.DirectedGraph;
import us.fatehi.utility.html.Tag;
import us.fatehi.utility.ioresource.InputResource;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.readconfig.ReadConfig;
import us.fatehi.utility.scheduler.TaskRunner;
import us.fatehi.utility.string.StringFormat;

public final class CoreExportedPackages {

  private CoreExportedPackages() {}

  public static final List<Class<?>> exportedCoreTypes() {
    return List.of(
        Builder.class,
        DatabaseUtility.class,
        DatabaseConnectionSource.class,
        DirectedGraph.class,
        Tag.class,
        InputResource.class,
        PropertyName.class,
        ReadConfig.class,
        TaskRunner.class,
        StringFormat.class,
        Catalog.class,
        SchemaCrawlerOptions.class,
        SchemaCrawlerException.class,
        ReducerFactory.class,
        InclusionRule.class,
        EnumDataTypeHelper.class,
        MetaDataUtility.class,
        ImplicitAssociationAnalyzer.class,
        ERModel.class,
        ERModelUtility.class,
        DatabaseConnector.class,
        CommandProvider.class,
        SchemaCrawlerExecutable.class,
        schemacrawler.tools.executable.commandline.PluginCommand.class,
        CatalogLoader.class,
        ERModelLoader.class,
        Config.class,
        PluginRegistry.class,
        ExecutionState.class,
        SchemaCrawlerUtility.class,
        CatalogAttributes.class,
        CatalogStats.class,
        ERModelStats.class,
        TableRowCountsUtility.class);
  }
}

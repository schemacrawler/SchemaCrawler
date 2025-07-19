/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.catalogloader;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.property.PropertyName;

public interface CatalogLoader extends Comparable<CatalogLoader> {

  Catalog getCatalog();

  PropertyName getCatalogLoaderName();

  PluginCommand getCommandLineCommand();

  DatabaseConnectionSource getDataSource();

  default PluginCommand getHelpCommand() {
    return getCommandLineCommand();
  }

  int getPriority();

  SchemaCrawlerOptions getSchemaCrawlerOptions();

  SchemaRetrievalOptions getSchemaRetrievalOptions();

  void loadCatalog();

  void setAdditionalConfiguration(Config additionalConfig);

  void setCatalog(Catalog catalog);

  void setDataSource(DatabaseConnectionSource dataSource);

  void setSchemaCrawlerOptions(SchemaCrawlerOptions schemaCrawlerOptions);

  void setSchemaRetrievalOptions(SchemaRetrievalOptions schemaRetrievalOptions);
}

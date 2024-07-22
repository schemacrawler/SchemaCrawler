/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

  PropertyName getCommandDescription();

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

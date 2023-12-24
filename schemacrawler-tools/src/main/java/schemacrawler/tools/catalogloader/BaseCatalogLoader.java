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

import static java.util.Comparator.comparingInt;
import static java.util.Comparator.nullsLast;
import static java.util.Objects.compare;
import static java.util.Objects.requireNonNull;

import java.util.Comparator;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public abstract class BaseCatalogLoader implements CatalogLoader {

  private static Comparator<CatalogLoader> comparator =
      nullsLast(comparingInt(CatalogLoader::getPriority))
          .thenComparing(loader -> loader.getCommandDescription().getName());

  private final int priority;
  private final CommandDescription commandDescription;
  private SchemaRetrievalOptions schemaRetrievalOptions;
  private SchemaCrawlerOptions schemaCrawlerOptions;
  private Config additionalConfig;
  private DatabaseConnectionSource dataSource;
  private Catalog catalog;

  protected BaseCatalogLoader(final CommandDescription commandDescription, final int priority) {
    this.commandDescription = requireNonNull(commandDescription, "No command description provided");
    this.priority = priority;
  }

  @Override
  public int compareTo(final CatalogLoader otherCatalogLoader) {
    return compare(this, otherCatalogLoader, comparator);
  }

  @Override
  public Catalog getCatalog() {
    return catalog;
  }

  @Override
  public CommandDescription getCommandDescription() {
    return commandDescription;
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    return PluginCommand.empty();
  }

  @Override
  public DatabaseConnectionSource getDataSource() {
    return dataSource;
  }

  @Override
  public int getPriority() {
    return priority;
  }

  @Override
  public SchemaCrawlerOptions getSchemaCrawlerOptions() {
    if (schemaCrawlerOptions == null) {
      return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    } else {
      return schemaCrawlerOptions;
    }
  }

  @Override
  public SchemaRetrievalOptions getSchemaRetrievalOptions() {
    if (schemaRetrievalOptions == null) {
      return SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions();
    } else {
      return schemaRetrievalOptions;
    }
  }

  /**
   * @param additionalConfig the additionalConfig to set
   */
  @Override
  public void setAdditionalConfiguration(final Config additionalConfig) {
    this.additionalConfig = additionalConfig;
  }

  @Override
  public void setCatalog(final Catalog catalog) {
    this.catalog = catalog;
  }

  @Override
  public void setDataSource(final DatabaseConnectionSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void setSchemaCrawlerOptions(final SchemaCrawlerOptions schemaCrawlerOptions) {
    this.schemaCrawlerOptions = schemaCrawlerOptions;
  }

  @Override
  public void setSchemaRetrievalOptions(final SchemaRetrievalOptions schemaRetrievalOptions) {
    this.schemaRetrievalOptions = schemaRetrievalOptions;
  }

  protected Config getAdditionalConfiguration() {
    return additionalConfig;
  }

  protected boolean isDatabaseSystemIdentifier(final String databaseSystemIdentifier) {
    final String actualDatabaseSystemIdentifier =
        getSchemaRetrievalOptions().getDatabaseServerType().getDatabaseSystemIdentifier();
    if (actualDatabaseSystemIdentifier == null && databaseSystemIdentifier == null) {
      return true;
    } else if (actualDatabaseSystemIdentifier != null) {
      return actualDatabaseSystemIdentifier.equals(databaseSystemIdentifier);
    } else {
      return false;
    }
  }

  protected boolean isLoaded() {
    return catalog != null;
  }
}

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.catalogloader;

import static java.util.Comparator.comparingInt;
import static java.util.Comparator.nullsLast;
import static java.util.Objects.compare;
import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.StringFormat;

public abstract class BaseCatalogLoader implements CatalogLoader {

  private static final Logger LOGGER = Logger.getLogger(BaseCatalogLoader.class.getName());

  private static Comparator<CatalogLoader> comparator =
      nullsLast(comparingInt(CatalogLoader::getPriority))
          .thenComparing(loader -> loader.getCatalogLoaderName().getName());

  private final int priority;
  private final PropertyName catalogLoaderName;
  private SchemaRetrievalOptions schemaRetrievalOptions;
  private SchemaCrawlerOptions schemaCrawlerOptions;
  private Config additionalConfig;
  private DatabaseConnectionSource dataSource;
  private Catalog catalog;

  protected BaseCatalogLoader(final PropertyName catalogLoaderName, final int priority) {
    this.catalogLoaderName = requireNonNull(catalogLoaderName, "No catalog loader name provided");
    this.priority = priority;
  }

  @Override
  public final int compareTo(final CatalogLoader otherCatalogLoader) {
    return compare(this, otherCatalogLoader, comparator);
  }

  @Override
  public final Catalog getCatalog() {
    return catalog;
  }

  @Override
  public final PropertyName getCatalogLoaderName() {
    return catalogLoaderName;
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    return PluginCommand.empty();
  }

  @Override
  public final DatabaseConnectionSource getDataSource() {
    return dataSource;
  }

  @Override
  public final int getPriority() {
    return priority;
  }

  @Override
  public final SchemaCrawlerOptions getSchemaCrawlerOptions() {
    if (schemaCrawlerOptions == null) {
      return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    }
    return schemaCrawlerOptions;
  }

  @Override
  public final SchemaRetrievalOptions getSchemaRetrievalOptions() {
    if (schemaRetrievalOptions == null) {
      return SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions();
    }
    return schemaRetrievalOptions;
  }

  /**
   * @param additionalConfig the additionalConfig to set
   */
  @Override
  public final void setAdditionalConfiguration(final Config additionalConfig) {
    this.additionalConfig = additionalConfig;
  }

  @Override
  public final void setCatalog(final Catalog catalog) {
    if (catalog != null) {
      LOGGER.log(Level.INFO, new StringFormat("Loaded catalog with loader <%s>", this.getClass()));
    }
    this.catalog = catalog;
  }

  @Override
  public final void setDataSource(final DatabaseConnectionSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public final void setSchemaCrawlerOptions(final SchemaCrawlerOptions schemaCrawlerOptions) {
    this.schemaCrawlerOptions = schemaCrawlerOptions;
  }

  @Override
  public final void setSchemaRetrievalOptions(final SchemaRetrievalOptions schemaRetrievalOptions) {
    this.schemaRetrievalOptions = schemaRetrievalOptions;
  }

  protected final Config getAdditionalConfiguration() {
    return additionalConfig;
  }

  protected final boolean isDatabaseSystemIdentifier(final String databaseSystemIdentifier) {
    final String actualDatabaseSystemIdentifier =
        getSchemaRetrievalOptions().getDatabaseServerType().getDatabaseSystemIdentifier();
    if (actualDatabaseSystemIdentifier == null && databaseSystemIdentifier == null) {
      return true;
    }
    if (actualDatabaseSystemIdentifier != null) {
      return actualDatabaseSystemIdentifier.equals(databaseSystemIdentifier);
    }
    return false;
  }

  protected final boolean isLoaded() {
    return catalog != null;
  }
}

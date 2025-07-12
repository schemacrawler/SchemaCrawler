/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.catalogloader;

import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.StringFormat;

public class ChainedCatalogLoader extends BaseCatalogLoader implements Iterable<CatalogLoader> {

  private static final Logger LOGGER = Logger.getLogger(ChainedCatalogLoader.class.getName());

  private final List<CatalogLoader> chainedCatalogLoaders;

  public ChainedCatalogLoader(final List<CatalogLoader> chainedCatalogLoaders) {
    super(
        new PropertyName("chainloader", "Chain of all catalog loaders, called in turn by priority"),
        Integer.MIN_VALUE);
    requireNonNull(chainedCatalogLoaders);
    this.chainedCatalogLoaders = new ArrayList<>(chainedCatalogLoaders);
  }

  @Override
  public Iterator<CatalogLoader> iterator() {
    return chainedCatalogLoaders.iterator();
  }

  @Override
  public void loadCatalog() {
    Catalog catalog = null;
    final DatabaseConnectionSource dataSource = getDataSource();
    final SchemaCrawlerOptions schemaCrawlerOptions = getSchemaCrawlerOptions();
    final SchemaRetrievalOptions schemaRetrievalOptions = getSchemaRetrievalOptions();
    final Config additionalConfig = getAdditionalConfiguration();
    for (final CatalogLoader nextCatalogLoader : chainedCatalogLoaders) {
      LOGGER.log(
          Level.CONFIG,
          new StringFormat("Loading catalog with <%s>", nextCatalogLoader.getClass()));
      nextCatalogLoader.setCatalog(catalog);
      nextCatalogLoader.setDataSource(dataSource);
      nextCatalogLoader.setSchemaCrawlerOptions(schemaCrawlerOptions);
      nextCatalogLoader.setSchemaRetrievalOptions(schemaRetrievalOptions);
      nextCatalogLoader.setAdditionalConfiguration(additionalConfig);

      nextCatalogLoader.loadCatalog();

      catalog = nextCatalogLoader.getCatalog();
    }
    setCatalog(catalog);
  }

  @Override
  public String toString() {
    return "CatalogLoader [" + chainedCatalogLoaders + "]";
  }
}

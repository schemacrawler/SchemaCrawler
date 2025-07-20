/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.catalogloader;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import us.fatehi.utility.property.PropertyName;

public class SchemaCrawlerCatalogLoader extends BaseCatalogLoader {

  public SchemaCrawlerCatalogLoader() {
    super(new PropertyName("schemacrawlerloader", "Loader for SchemaCrawler metadata catalog"), 0);
  }

  @Override
  public void loadCatalog() {
    if (isLoaded()) {
      return;
    }

    final SchemaCrawler schemaCrawler =
        new SchemaCrawler(getDataSource(), getSchemaRetrievalOptions(), getSchemaCrawlerOptions());
    final Catalog catalog = schemaCrawler.crawl();
    setCatalog(catalog);
  }
}

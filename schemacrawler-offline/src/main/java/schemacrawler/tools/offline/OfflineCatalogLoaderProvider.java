/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.offline;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import schemacrawler.tools.loader.catalog.BaseCatalogLoaderProvider;
import schemacrawler.tools.offline.OfflineCatalogLoader.OfflineCatalogLoaderOptions;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.property.PropertyName;

public class OfflineCatalogLoaderProvider extends BaseCatalogLoaderProvider {

  private static final PropertyName NAME =
      new PropertyName("offlineloader", "Loader for offline databases");

  @Override
  public Collection<PropertyName> getSupportedCommands() {
    return List.of(NAME);
  }

  @Override
  public OfflineCatalogLoader newCommand(final Config config) {
    requireNonNull(config, "No config provided");
    final OfflineCatalogLoader loader = new OfflineCatalogLoader(NAME);
    loader.configure(new OfflineCatalogLoaderOptions());
    return loader;
  }
}

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.offline;

import java.util.Collection;
import java.util.List;
import schemacrawler.tools.catalogloader.BaseCatalogLoaderProvider;
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
  public OfflineCatalogLoader newCommand(final String command, final Config config) {
    if (config == null) {
      throw new IllegalArgumentException("No config provided");
    }
    if (!NAME.getName().equals(command)) {
      throw new IllegalArgumentException("Bad catalog loader command <%s>".formatted(command));
    }

    final OfflineCatalogLoader loader = new OfflineCatalogLoader(NAME);
    loader.configure(new OfflineCatalogLoaderOptions());

    return loader;
  }
}

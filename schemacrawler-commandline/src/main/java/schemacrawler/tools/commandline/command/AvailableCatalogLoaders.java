/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.commandline.command;

import schemacrawler.tools.catalogloader.CatalogLoaderRegistry;

public class AvailableCatalogLoaders extends BaseAvailableRegistryPlugins {

  private final String name;

  public AvailableCatalogLoaders() {
    super(CatalogLoaderRegistry.getCatalogLoaderRegistry().getRegisteredPlugins());
    name = CatalogLoaderRegistry.getCatalogLoaderRegistry().getName();
  }

  @Override
  public String getName() {
    return name;
  }
}

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.command;

import schemacrawler.loader.ermodel.ERModelLoaderRegistry;

public class AvailableERModelLoaders extends BaseAvailableRegistryPlugins {

  private final String name;

  public AvailableERModelLoaders() {
    super(ERModelLoaderRegistry.getERModelLoaderRegistry().getRegisteredPlugins());
    name = ERModelLoaderRegistry.getERModelLoaderRegistry().getName();
  }

  @Override
  public String getName() {
    return name;
  }
}

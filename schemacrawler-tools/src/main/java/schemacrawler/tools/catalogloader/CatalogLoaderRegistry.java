/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.catalogloader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.registry.BasePluginRegistry;
import schemacrawler.tools.registry.PluginCommandRegistry;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.StringFormat;

/** Registry for registered catalog orders, in order of priority. */
public final class CatalogLoaderRegistry extends BasePluginRegistry
    implements PluginCommandRegistry {

  private static final Logger LOGGER = Logger.getLogger(CatalogLoaderRegistry.class.getName());

  private static CatalogLoaderRegistry catalogLoaderRegistrySingleton;

  public static CatalogLoaderRegistry getCatalogLoaderRegistry() {
    if (catalogLoaderRegistrySingleton == null) {
      catalogLoaderRegistrySingleton = new CatalogLoaderRegistry();
      catalogLoaderRegistrySingleton.log();
    }
    return catalogLoaderRegistrySingleton;
  }

  private static List<CatalogLoader> loadCatalogLoaderRegistry() {

    // Use thread-safe list
    final List<CatalogLoader> catalogLoaderRegistry = new CopyOnWriteArrayList<>();

    try {
      final ServiceLoader<CatalogLoader> serviceLoader =
          ServiceLoader.load(CatalogLoader.class, CatalogLoaderRegistry.class.getClassLoader());
      for (final CatalogLoader catalogLoader : serviceLoader) {
        LOGGER.log(
            Level.CONFIG,
            new StringFormat("Loading catalog loader, %s", catalogLoader.getClass().getName()));

        catalogLoaderRegistry.add(catalogLoader);
      }
    } catch (final Throwable e) {
      throw new InternalRuntimeException("Could not load catalog loader registry", e);
    }

    Collections.sort(catalogLoaderRegistry);
    return catalogLoaderRegistry;
  }

  private final List<CatalogLoader> catalogLoaderRegistry;

  private CatalogLoaderRegistry() {
    catalogLoaderRegistry = loadCatalogLoaderRegistry();
  }

  @Override
  public Collection<PluginCommand> getCommandLineCommands() {
    final Collection<PluginCommand> commandLineCommands = new HashSet<>();
    for (final CatalogLoader catalogLoader : catalogLoaderRegistry) {
      commandLineCommands.add(catalogLoader.getCommandLineCommand());
    }
    return commandLineCommands;
  }

  @Override
  public Collection<PluginCommand> getHelpCommands() {
    final Collection<PluginCommand> commandLineCommands = new HashSet<>();
    for (final CatalogLoader catalogLoader : catalogLoaderRegistry) {
      commandLineCommands.add(catalogLoader.getHelpCommand());
    }
    return commandLineCommands;
  }

  @Override
  public Collection<PropertyName> getRegisteredPlugins() {
    final List<PropertyName> commandLineCommands = new ArrayList<>();
    for (final CatalogLoader catalogLoader : catalogLoaderRegistry) {
      final PropertyName catalogLoaderName = catalogLoader.getCatalogLoaderName();
      commandLineCommands.add(catalogLoaderName);
    }
    // Do not sort property names, since the loaders are already sorted in order of priority
    return commandLineCommands;
  }

  public ChainedCatalogLoader newChainedCatalogLoader() {
    // Make a defensive copy of the list of catalog loaders
    final List<CatalogLoader> chainedCatalogLoaders = new ArrayList<>(catalogLoaderRegistry);
    return new ChainedCatalogLoader(chainedCatalogLoaders);
  }

  @Override
  public String getName() {
    return "SchemaCrawler Catalog Loaders";
  }
}

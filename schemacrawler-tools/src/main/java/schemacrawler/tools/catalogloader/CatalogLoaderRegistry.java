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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.registry.BasePluginRegistry;
import us.fatehi.utility.string.StringFormat;

/** Registry for registered catalog orders, in order of priority. */
public final class CatalogLoaderRegistry extends BasePluginRegistry {

  private static final Logger LOGGER = Logger.getLogger(CatalogLoaderRegistry.class.getName());

  private static CatalogLoaderRegistry catalogLoaderRegistrySingleton;

  public static CatalogLoaderRegistry getCatalogLoaderRegistry() {
    if (catalogLoaderRegistrySingleton == null) {
      catalogLoaderRegistrySingleton = new CatalogLoaderRegistry();
    }
    return catalogLoaderRegistrySingleton;
  }

  public static void reload() {
    if (catalogLoaderRegistrySingleton != null) {
      final List<CatalogLoader> registry = catalogLoaderRegistrySingleton.catalogLoaderRegistry;
      registry.clear();
      registry.addAll(loadCatalogLoaderRegistry());
    }
  }

  private static List<CatalogLoader> loadCatalogLoaderRegistry() {

    final List<CatalogLoader> catalogLoaderRegistry = new ArrayList<>();

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
  public Collection<CommandDescription> getCommandDescriptions() {
    final Collection<CommandDescription> commandLineCommands = new HashSet<>();
    for (final CatalogLoader catalogLoader : catalogLoaderRegistry) {
      final CommandDescription commandDescription = catalogLoader.getCommandDescription();
      commandLineCommands.add(
          new CommandDescription(
              commandDescription.getName(), commandDescription.getDescription()));
    }
    return commandLineCommands;
  }

  public ChainedCatalogLoader newChainedCatalogLoader() {
    // Make a defensive copy of the list of catalog loaders
    final List<CatalogLoader> chainedCatalogLoaders = new ArrayList<>(catalogLoaderRegistry);
    return new ChainedCatalogLoader(chainedCatalogLoaders);
  }

  @Override
  public String getName() {
    return "SchemaCrawler catalog loaders";
  }
}

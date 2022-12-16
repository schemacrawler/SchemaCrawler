/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
import us.fatehi.utility.string.StringFormat;

/** Registry for mapping database connectors from DatabaseConnector-line switch. */
public final class CatalogLoaderRegistry {

  private static final Logger LOGGER = Logger.getLogger(CatalogLoaderRegistry.class.getName());

  public Collection<PluginCommand> getCommandLineCommands() {
    final Collection<PluginCommand> commandLineCommands = new HashSet<>();
    for (final CatalogLoader catalogLoader : instantiateCatalogLoaders()) {
      commandLineCommands.add(catalogLoader.getCommandLineCommand());
    }
    return commandLineCommands;
  }

  public Collection<PluginCommand> getHelpCommands() {
    final Collection<PluginCommand> commandLineCommands = new HashSet<>();
    for (final CatalogLoader catalogLoader : instantiateCatalogLoaders()) {
      commandLineCommands.add(catalogLoader.getHelpCommand());
    }
    return commandLineCommands;
  }

  public Collection<CommandDescription> getSupportedCatalogLoaders() {
    final Collection<CommandDescription> commandLineCommands = new HashSet<>();
    for (final CatalogLoader catalogLoader : instantiateCatalogLoaders()) {
      final CommandDescription commandDescription = catalogLoader.getCommandDescription();
      commandLineCommands.add(
          new CommandDescription(
              commandDescription.getName(), commandDescription.getDescription()));
    }
    return commandLineCommands;
  }

  public ChainedCatalogLoader newChainedCatalogLoader() {
    final List<CatalogLoader> chainedCatalogLoaders = instantiateCatalogLoaders();
    return new ChainedCatalogLoader(chainedCatalogLoaders);
  }

  private List<CatalogLoader> instantiateCatalogLoaders() {

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
}

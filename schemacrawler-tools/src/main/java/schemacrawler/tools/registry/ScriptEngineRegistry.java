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

package schemacrawler.tools.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.commandline.PluginCommand;

public class ScriptEngineRegistry implements PluginRegistry {

  private static final Logger LOGGER = Logger.getLogger(ScriptEngineRegistry.class.getName());

  private static ScriptEngineRegistry scriptEngineRegistrySingleton;

  public static ScriptEngineRegistry getScriptEngineRegistry() {
    if (scriptEngineRegistrySingleton == null) {
      scriptEngineRegistrySingleton = new ScriptEngineRegistry();
    }
    return scriptEngineRegistrySingleton;
  }

  private static List<CommandDescription> loadScriptEngines() {
    final List<CommandDescription> availableScriptEngines = new ArrayList<>();
    try {
      final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
      final List<ScriptEngineFactory> engineFactories = scriptEngineManager.getEngineFactories();
      for (final ScriptEngineFactory scriptEngineFactory : engineFactories) {
        if (scriptEngineFactory != null) {
          availableScriptEngines.add(
              new CommandDescription(
                  scriptEngineFactory.getEngineName(),
                  String.format(
                      "%-15s file extensions: %s",
                      scriptEngineFactory.getEngineVersion(),
                      scriptEngineFactory.getExtensions())));
        }
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not list script engines", e);
    }
    return availableScriptEngines;
  }

  private final Collection<CommandDescription> commandDescriptions;

  private ScriptEngineRegistry() {
    commandDescriptions = loadScriptEngines();
  }

  @Override
  public Collection<PluginCommand> getCommandLineCommands() {
    return Collections.emptyList();
  }

  @Override
  public Collection<PluginCommand> getHelpCommands() {
    return Collections.emptyList();
  }

  @Override
  public Collection<CommandDescription> getCommandDescriptions() {
    return new ArrayList<>(commandDescriptions);
  }
}

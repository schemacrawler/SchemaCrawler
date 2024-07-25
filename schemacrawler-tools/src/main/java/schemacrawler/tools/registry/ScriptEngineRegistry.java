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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import us.fatehi.utility.property.PropertyName;

public class ScriptEngineRegistry extends BasePluginRegistry {

  private static final Logger LOGGER = Logger.getLogger(ScriptEngineRegistry.class.getName());

  private static ScriptEngineRegistry scriptEngineRegistrySingleton;

  public static ScriptEngineRegistry getScriptEngineRegistry() {
    if (scriptEngineRegistrySingleton == null) {
      scriptEngineRegistrySingleton = new ScriptEngineRegistry();
    }
    return scriptEngineRegistrySingleton;
  }

  private static List<PropertyName> loadScriptEngines() {

    // Use thread-safe list
    final List<PropertyName> availableScriptEngines = new CopyOnWriteArrayList<>();
    try {
      final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
      final List<ScriptEngineFactory> engineFactories = scriptEngineManager.getEngineFactories();
      for (final ScriptEngineFactory scriptEngineFactory : engineFactories) {
        if (scriptEngineFactory != null) {
          availableScriptEngines.add(
              new PropertyName(
                  scriptEngineFactory.getEngineName(),
                  String.format(
                      "%-15s file extensions: %s",
                      scriptEngineFactory.getEngineVersion(),
                      scriptEngineFactory.getExtensions())));
        }
      }
    } catch (final Throwable e) {
      // NOTE: Do not hard fail if script engines cannot be loaded
      LOGGER.log(Level.WARNING, "Could not load script engines", e);
    }
    return availableScriptEngines;
  }

  private final Collection<PropertyName> commandDescriptions;

  private ScriptEngineRegistry() {
    commandDescriptions = loadScriptEngines();
  }

  @Override
  public Collection<PropertyName> getRegisteredPlugins() {
    return new ArrayList<>(commandDescriptions);
  }

  @Override
  public String getName() {
    return "script engines";
  }
}

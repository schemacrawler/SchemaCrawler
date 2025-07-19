/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
      scriptEngineRegistrySingleton.log();
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
                  String.format(
                      "%s %s",
                      scriptEngineFactory.getEngineName(), scriptEngineFactory.getExtensions()),
                  scriptEngineFactory.getEngineVersion()));
        }
      }
    } catch (final Throwable e) {
      // NOTE: Do not hard fail if script engines cannot be loaded
      LOGGER.log(Level.WARNING, "Could not load script engines", e);
    }
    Collections.sort(availableScriptEngines);
    return availableScriptEngines;
  }

  private final Collection<PropertyName> scriptEngines;

  private ScriptEngineRegistry() {
    scriptEngines = loadScriptEngines();
  }

  @Override
  public Collection<PropertyName> getRegisteredPlugins() {
    return new ArrayList<>(scriptEngines);
  }

  @Override
  public String getName() {
    return "Script Engines";
  }
}

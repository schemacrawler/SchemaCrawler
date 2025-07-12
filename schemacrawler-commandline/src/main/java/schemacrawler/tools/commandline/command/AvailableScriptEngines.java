/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.commandline.command;

import schemacrawler.tools.registry.ScriptEngineRegistry;

public class AvailableScriptEngines extends BaseAvailableRegistryPlugins {

  private final String name;

  public AvailableScriptEngines() {
    super(ScriptEngineRegistry.getScriptEngineRegistry().getRegisteredPlugins());
    name = ScriptEngineRegistry.getScriptEngineRegistry().getName();
  }

  @Override
  public String getName() {
    return name;
  }
}

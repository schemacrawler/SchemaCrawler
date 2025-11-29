/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.registry;

import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.property.PropertyNameUtility;

public abstract class BasePluginRegistry implements PluginRegistry {

  private static final Logger LOGGER = Logger.getLogger(BasePluginRegistry.class.getName());

  @Override
  public void log() {
    final boolean log = LOGGER.isLoggable(Level.CONFIG);
    if (!log) {
      return;
    }

    final String title = "Registered %s:".formatted(getName());
    final String registeredPlugins = PropertyNameUtility.tableOf(title, getRegisteredPlugins());
    LOGGER.log(Level.CONFIG, registeredPlugins);
  }
}

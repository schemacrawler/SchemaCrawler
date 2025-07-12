/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.registry;

import java.util.Collection;
import us.fatehi.utility.property.PropertyName;

public interface PluginRegistry {

  Collection<PropertyName> getRegisteredPlugins();

  void log();

  String getName();
}

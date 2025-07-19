/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import static org.junit.jupiter.api.Assertions.fail;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.registry.PluginRegistry;

public final class PluginRegistryTestUtility {

  private PluginRegistryTestUtility() {
    // Prevent instantiation
  }

  public static void reload(final Class<? extends PluginRegistry> registryClass) {
    Constructor<? extends PluginRegistry> constructor = null;
    try {
      constructor = registryClass.getDeclaredConstructor();
      constructor.setAccessible(true);
      constructor.newInstance();
    } catch (final NoSuchMethodException
        | SecurityException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException e) {
      if (e.getCause() instanceof InternalRuntimeException) {
        throw (InternalRuntimeException) e.getCause();
      }
      fail(e);
    }
  }
}

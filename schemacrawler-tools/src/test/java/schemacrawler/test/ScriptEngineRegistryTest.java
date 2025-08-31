/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static schemacrawler.test.utility.TestUtility.isJre8;

import java.util.Collection;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.registry.ScriptEngineRegistry;
import us.fatehi.utility.property.PropertyName;

public class ScriptEngineRegistryTest {

  @Test
  public void registeredPlugins() {
    if (isJre8()) {
      // No script engines ship with Java versions later than 8
      return;
    }
    final ScriptEngineRegistry driverRegistry = ScriptEngineRegistry.getScriptEngineRegistry();
    final Collection<PropertyName> commandLineCommands = driverRegistry.getRegisteredPlugins();
    assertThat(commandLineCommands, hasSize(1));
  }

  @Test
  public void name() {
    final ScriptEngineRegistry driverRegistry = ScriptEngineRegistry.getScriptEngineRegistry();
    assertThat(driverRegistry.getName(), is("Script Engines"));
  }
}

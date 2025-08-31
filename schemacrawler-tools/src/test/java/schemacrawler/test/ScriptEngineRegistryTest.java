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

import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;
import schemacrawler.tools.registry.ScriptEngineRegistry;
import us.fatehi.utility.property.PropertyName;

public class ScriptEngineRegistryTest {

  @Test
  // No script engines ship with Java versions later than 8
  @EnabledOnJre(JRE.JAVA_8)
  public void registeredPlugins() {
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

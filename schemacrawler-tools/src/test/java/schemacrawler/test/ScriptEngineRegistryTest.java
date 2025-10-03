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

import org.junit.jupiter.api.Test;
import schemacrawler.tools.registry.ScriptEngineRegistry;

public class ScriptEngineRegistryTest {

  @Test
  public void name() {
    final ScriptEngineRegistry driverRegistry = ScriptEngineRegistry.getScriptEngineRegistry();
    assertThat(driverRegistry.getName(), is("Script Engines"));
  }
}

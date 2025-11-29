/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.script;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.tools.command.script.GraalScriptExecutor;
import schemacrawler.tools.command.script.ScriptExecutor;
import schemacrawler.tools.command.script.options.ScriptLanguageType;

@DisableLogging
public class ScriptExecutorTest {

  @Test
  public void graal() throws Exception {
    final StringWriter writer = new StringWriter();
    final ScriptExecutor scriptExecutor = new GraalScriptExecutor(ScriptLanguageType.js);

    final Map<String, Object> context = new HashMap<>();
    context.put("javaObj", new Object());

    scriptExecutor.initialize(
        context,
        new StringReader(
            "if (javaObj instanceof Java.type('java.lang.Object')) { print(\"Hello, World!\"); }"),
        writer);

    assertThat(scriptExecutor.canGenerate(), is(true));

    scriptExecutor.run();
    assertThat(writer.toString().replaceAll("\\R", ""), is("Hello, World!"));
  }

  @Test
  public void graalEngineNullLanguage() throws Exception {
    assertThrows(NullPointerException.class, () -> new GraalScriptExecutor(null));
  }

  @Test
  public void graalEngineUnknownLanguage() throws Exception {
    assertThrows(
        IllegalArgumentException.class, () -> new GraalScriptExecutor(ScriptLanguageType.unknown));
  }
}

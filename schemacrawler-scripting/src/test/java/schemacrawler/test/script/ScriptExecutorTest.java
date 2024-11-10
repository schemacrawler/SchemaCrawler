/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.test.script;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.command.script.GraalJSScriptExecutor;
import schemacrawler.tools.command.script.ScriptEngineExecutor;
import schemacrawler.tools.command.script.ScriptExecutor;

public class ScriptExecutorTest {

  @Test
  public void graal() throws Exception {
    final StringWriter writer = new StringWriter();
    final ScriptExecutor scriptExecutor = new GraalJSScriptExecutor("javascript");

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
  public void graalEngineBadLanguage() throws Exception {
    final ScriptExecutor scriptExecutor = new GraalJSScriptExecutor("foulmouth");

    assertThat(scriptExecutor.canGenerate(), is(false));
  }

  @Test
  public void scriptEngine() throws Exception {
    final StringWriter writer = new StringWriter();
    final ScriptExecutor scriptExecutor = new ScriptEngineExecutor("python");
    scriptExecutor.initialize(
        Collections.emptyMap(), new StringReader("print(\"Hello, World!\")"), writer);

    assertThat(scriptExecutor.canGenerate(), is(true));

    scriptExecutor.run();
    assertThat(writer.toString().replaceAll("\\R", ""), is("Hello, World!"));
  }

  @Test
  public void scriptEngineBadLanguage() throws Exception {
    final ScriptExecutor scriptExecutor = new ScriptEngineExecutor("foulmouth");

    assertThat(scriptExecutor.canGenerate(), is(false));
  }
}

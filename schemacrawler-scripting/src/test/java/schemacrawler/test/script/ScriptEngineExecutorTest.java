/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import schemacrawler.tools.command.script.ScriptEngineExecutor;
import us.fatehi.utility.ioresource.StringInputResource;

public class ScriptEngineExecutorTest {

  @Test
  public void testPythonScript() throws Exception {
    final StringWriter writer = new StringWriter();
    final ScriptEngineExecutor scriptEngineExecutor =
        new ScriptEngineExecutor(
            "python", UTF_8, new StringInputResource("print(\"Hello, World!\")"), writer);

    assertThat(scriptEngineExecutor.canGenerate(), is(true));
    assertThat(scriptEngineExecutor.call(), is(true));
    assertThat(writer.toString().replaceAll("\\R", ""), is("Hello, World!"));
  }
}

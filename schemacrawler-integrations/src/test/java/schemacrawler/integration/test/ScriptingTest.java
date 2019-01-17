/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.integration.test;


import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.integration.test.utility.IntegrationTestUtility.commandlineExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.executableOf;
import static schemacrawler.test.utility.ExecutableTestUtility.outputOf;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestAssertNoSystemErrOutput;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;

@ExtendWith(TestAssertNoSystemErrOutput.class)
@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class ScriptingTest
{

  @Test
  public void commandlineGroovy(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    assertThat(outputOf(commandlineExecution(connectionInfo,
                                             "script",
                                             additionalArgsMap(),
                                             "/plaintextschema.groovy")),
               hasSameContentAs(classpathResource("script_output.txt")));
  }

  @Test
  public void commandlineJavaScript(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    assertThat(outputOf(commandlineExecution(connectionInfo,
                                             "script",
                                             additionalArgsMap(),
                                             "/plaintextschema.js")),
               hasSameContentAs(classpathResource("script_output.txt")));
  }

  @Test
  public void commandlinePython(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    assertThat(outputOf(commandlineExecution(connectionInfo,
                                             "script",
                                             additionalArgsMap(),
                                             "/plaintextschema.py")),
               hasSameContentAs(classpathResource("script_output.txt")));
  }

  @Test
  public void commandlineRuby(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    assertThat(outputOf(commandlineExecution(connectionInfo,
                                             "script",
                                             additionalArgsMap(),
                                             "/plaintextschema.rb")),
               hasSameContentAs(classpathResource("script_output_rb.txt")));
  }

  @Test
  public void executableGroovy(final Connection connection)
    throws Exception
  {
    assertThat(outputOf(executableExecution(connection,
                                            executableOf("script"),
                                            "/plaintextschema.groovy")),
               hasSameContentAs(classpathResource("script_output.txt")));
  }

  @Test
  public void executableJavaScript(final Connection connection)
    throws Exception
  {
    assertThat(outputOf(executableExecution(connection,
                                            executableOf("script"),
                                            "/plaintextschema.js")),
               hasSameContentAs(classpathResource("script_output.txt")));
  }

  @Test
  public void executablePython(final Connection connection)
    throws Exception
  {
    System.setProperty("python.console.encoding", "UTF-8");
    assertThat(outputOf(executableExecution(connection,
                                            executableOf("script"),
                                            "/plaintextschema.py")),
               hasSameContentAs(classpathResource("script_output.txt")));
  }

  @Test
  public void executableRuby(final Connection connection)
    throws Exception
  {
    assertThat(outputOf(executableExecution(connection,
                                            executableOf("script"),
                                            "/plaintextschema.rb")),
               hasSameContentAs(classpathResource("script_output_rb.txt")));
  }

  private Map<String, String> additionalArgsMap()
  {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("schemas", "((?!FOR_LINT).)*");
    argsMap.put("infolevel", "standard");
    return argsMap;
  }

}

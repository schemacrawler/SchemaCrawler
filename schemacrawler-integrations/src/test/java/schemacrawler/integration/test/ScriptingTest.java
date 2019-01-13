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


import java.sql.Connection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.integration.test.utility.BaseIntegrationTest;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class ScriptingTest
  extends BaseIntegrationTest
{

  @Test
  public void commandlineGroovy(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    executeCommandline(connectionInfo,
                       "script",
                       "/plaintextschema.groovy",
                       "script_output.txt");
  }

  @Test
  public void commandlineJavaScript(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    executeCommandline(connectionInfo,
                       "script",
                       "/plaintextschema.js",
                       "script_output.txt");
  }

  @Test
  public void commandlinePython(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    executeCommandline(connectionInfo,
                       "script",
                       "/plaintextschema.py",
                       "script_output.txt");
  }

  @Test
  public void commandlineRuby(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    executeCommandline(connectionInfo,
                       "script",
                       "/plaintextschema.rb",
                       "script_output_rb.txt");
  }

  @Test
  public void executableGroovy(final Connection connection)
    throws Exception
  {
    executeExecutable(connection,
                      createExecutable("script"),
                      "/plaintextschema.groovy",
                      "script_output.txt");
  }

  @Test
  public void executableJavaScript(final Connection connection)
    throws Exception
  {
    executeExecutable(connection,
                      createExecutable("script"),
                      "/plaintextschema.js",
                      "script_output.txt");
  }

  @Test
  public void executablePython(final Connection connection)
    throws Exception
  {
    executeExecutable(connection,
                      createExecutable("script"),
                      "/plaintextschema.py",
                      "script_output.txt");
  }

  @Test
  public void executableRuby(final Connection connection)
    throws Exception
  {
    executeExecutable(connection,
                      createExecutable("script"),
                      "/plaintextschema.rb",
                      "script_output_rb.txt");
  }

}

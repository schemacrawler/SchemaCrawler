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


import static schemacrawler.test.utility.ExecutableTestUtility.createExecutable;
import static schemacrawler.test.utility.ExecutableTestUtility.executeExecutable;

import java.sql.Connection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.integration.test.utility.BaseIntegrationTest;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class TemplatingIntegrationTest
  extends BaseIntegrationTest
{

  @Test
  public void commandlineFreeMarker(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    executeCommandline(connectionInfo,
                       "freemarker",
                       "/plaintextschema.ftl",
                       "executableForFreeMarker.txt");
  }

  @Test
  public void commandlineThymeleaf(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    executeCommandline(connectionInfo,
                       "thymeleaf",
                       "/plaintextschema.thymeleaf",
                       "executableForThymeleaf.txt");
  }

  @Test
  public void commandlineVelocity(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    executeCommandline(connectionInfo,
                       "velocity",
                       "/plaintextschema.vm",
                       "executableForVelocity.txt");
  }

  @Test
  public void executableFreeMarker(final Connection connection)
    throws Exception
  {
    executeExecutable(connection,
                      createExecutable("freemarker"),
                      "/plaintextschema.ftl",
                      "executableForFreeMarker.txt");
  }

  @Test
  public void executableThymeleaf(final Connection connection)
    throws Exception
  {
    executeExecutable(connection,
                      createExecutable("thymeleaf"),
                      "/plaintextschema.thymeleaf",
                      "executableForThymeleaf.txt");
  }

  @Test
  public void executableVelocity(final Connection connection)
    throws Exception
  {
    executeExecutable(connection,
                      createExecutable("velocity"),
                      "/plaintextschema.vm",
                      "executableForVelocity.txt");
  }

}

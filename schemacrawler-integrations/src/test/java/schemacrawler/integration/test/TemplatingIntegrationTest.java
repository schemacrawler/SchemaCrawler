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
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.fileResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.Main;
import schemacrawler.test.utility.BaseExecutableTest;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.test.utility.TestWriter;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class TemplatingIntegrationTest
  extends BaseExecutableTest
{

  @Test
  public void commandlineFreeMarker(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    executeCommandlineAndCheckForOutputFile("freemarker",
                                            connectionInfo,
                                            "plaintextschema.ftl",
                                            "executableForFreeMarker");
  }

  @Test
  public void commandlineThymeleaf(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    executeCommandlineAndCheckForOutputFile("thymeleaf",
                                            connectionInfo,
                                            "plaintextschema.thymeleaf",
                                            "executableForThymeleaf");
  }

  @Test
  public void commandlineVelocity(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    executeCommandlineAndCheckForOutputFile("velocity",
                                            connectionInfo,
                                            "plaintextschema.vm",
                                            "executableForVelocity");
  }

  @Test
  public void executableFreeMarker(final Connection connection)
    throws Exception
  {
    executeExecutable("freemarker",
                      connection,
                      "plaintextschema.ftl",
                      "executableForFreeMarker.txt");
  }

  @Test
  public void executableThymeleaf(final Connection connection)
    throws Exception
  {
    executeExecutable("thymeleaf",
                      connection,
                      "plaintextschema.thymeleaf",
                      "executableForThymeleaf.txt");
  }

  @Test
  public void executableVelocity(final Connection connection)
    throws Exception
  {
    executeExecutable("velocity",
                      connection,
                      "plaintextschema.vm",
                      "executableForVelocity.txt");
  }

  private void executeCommandlineAndCheckForOutputFile(final String command,
                                                       final DatabaseConnectionInfo connectionInfo,
                                                       final String outputFormatValue,
                                                       final String referenceFileName)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("url", connectionInfo.getConnectionUrl());
      argsMap.put("user", "sa");
      argsMap.put("password", "");
      argsMap.put("infolevel", "standard");
      argsMap.put("command", command);
      argsMap.put("sortcolumns", "true");
      argsMap.put("outputformat", outputFormatValue);
      argsMap.put("outputfile", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(referenceFileName + ".txt")));
  }

}

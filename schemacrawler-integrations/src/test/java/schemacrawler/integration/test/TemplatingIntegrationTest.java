/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static org.junit.Assert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.fileResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import schemacrawler.Main;
import schemacrawler.test.utility.BaseExecutableTest;
import schemacrawler.test.utility.TestWriter;

public class TemplatingIntegrationTest
  extends BaseExecutableTest
{

  @Test
  public void commandlineFreeMarker()
    throws Exception
  {
    executeCommandlineAndCheckForOutputFile("freemarker",
                                            "plaintextschema.ftl",
                                            "executableForFreeMarker");
  }

  @Test
  public void commandlineThymeleaf()
    throws Exception
  {
    executeCommandlineAndCheckForOutputFile("thymeleaf",
                                            "plaintextschema.thymeleaf",
                                            "executableForThymeleaf");
  }

  @Test
  public void commandlineVelocity()
    throws Exception
  {
    executeCommandlineAndCheckForOutputFile("velocity",
                                            "plaintextschema.vm",
                                            "executableForVelocity");
  }

  @Test
  public void executableFreeMarker()
    throws Exception
  {
    executeExecutable("freemarker",
                      "plaintextschema.ftl",
                      "executableForFreeMarker.txt");
  }

  @Test
  public void executableThymeleaf()
    throws Exception
  {
    executeExecutable("thymeleaf",
                      "plaintextschema.thymeleaf",
                      "executableForThymeleaf.txt");
  }

  @Test
  public void executableVelocity()
    throws Exception
  {
    executeExecutable("velocity",
                      "plaintextschema.vm",
                      "executableForVelocity.txt");
  }

  private void executeCommandlineAndCheckForOutputFile(final String command,
                                                       final String outputFormatValue,
                                                       final String referenceFileName)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("url", "jdbc:hsqldb:hsql://localhost/schemacrawler");
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

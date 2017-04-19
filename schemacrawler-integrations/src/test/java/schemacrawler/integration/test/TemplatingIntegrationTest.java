/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.nio.charset.StandardCharsets.UTF_8;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import schemacrawler.Main;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.integration.freemarker.FreeMarkerRenderer;
import schemacrawler.tools.integration.thymeleaf.ThymeleafRenderer;
import schemacrawler.tools.integration.velocity.VelocityRenderer;
import schemacrawler.tools.options.OutputOptions;

public class TemplatingIntegrationTest
  extends BaseDatabaseTest
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
    executeExecutableAndCheckForOutputFile(new FreeMarkerRenderer(),
                                           "plaintextschema.ftl",
                                           "executableForFreeMarker");
  }

  @Test
  public void executableThymeleaf()
    throws Exception
  {
    executeExecutableAndCheckForOutputFile(new ThymeleafRenderer(),
                                           "plaintextschema.thymeleaf",
                                           "executableForThymeleaf");
  }

  @Test
  public void executableVelocity()
    throws Exception
  {
    executeExecutableAndCheckForOutputFile(new VelocityRenderer(),
                                           "plaintextschema.vm",
                                           "executableForVelocity");
  }

  private void executeCommandlineAndCheckForOutputFile(final String command,
                                                       final String outputFormatValue,
                                                       final String referenceFileName)
    throws Exception
  {
    try (final TestWriter out = new TestWriter("text");)
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

      out.assertEquals(referenceFileName + ".txt");
    }
  }

  private void executeExecutableAndCheckForOutputFile(final Executable executable,
                                                      final String outputFormatValue,
                                                      final String referenceFileName)
    throws Exception
  {
    try (final TestWriter out = new TestWriter(outputFormatValue);)
    {
      final OutputOptions outputOptions = new OutputOptions(outputFormatValue,
                                                            out);
      outputOptions.setInputEncoding(UTF_8);
      outputOptions.setOutputEncoding(UTF_8);

      executable.setOutputOptions(outputOptions);
      executable.execute(getConnection());

      out.assertEquals(referenceFileName + ".txt");
    }
  }
}

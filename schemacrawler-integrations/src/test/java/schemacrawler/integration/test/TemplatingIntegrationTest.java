/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package schemacrawler.integration.test;


import static sf.util.Utility.UTF8;
import static sf.util.commandlineparser.CommandLineArgumentsUtility.flattenCommandlineArgs;

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
      argsMap.put("driver", "org.hsqldb.jdbc.JDBCDriver");
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
      outputOptions.setInputEncoding(UTF8);
      outputOptions.setOutputEncoding(UTF8);

      executable.setOutputOptions(outputOptions);
      executable.execute(getConnection());

      out.assertEquals(referenceFileName + ".txt");
    }
  }
}

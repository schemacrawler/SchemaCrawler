/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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


import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static schemacrawler.test.utility.TestUtility.createTempFile;
import static sf.util.Utility.UTF8;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.commandline.SchemaCrawlerCommandLine;
import schemacrawler.tools.databaseconnector.DatabaseSystemConnector;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.integration.freemarker.FreeMarkerRenderer;
import schemacrawler.tools.integration.velocity.VelocityRenderer;
import schemacrawler.tools.options.OutputOptions;

public class IntegrationTest
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
    final Path testOutputFile = createTempFile(command, outputFormatValue);

    final Map<String, String> args = new HashMap<>();
    args.put("driver", "org.hsqldb.jdbc.JDBCDriver");
    args.put("url", "jdbc:hsqldb:hsql://localhost/schemacrawler");
    args.put("user", "sa");
    args.put("password", "");

    args.put("infolevel", "standard");
    args.put("command", command);
    args.put("sortcolumns", "true");
    args.put("outputformat", outputFormatValue);
    args.put("outputfile", testOutputFile.toString());

    final List<String> argsList = new ArrayList<>();
    for (final Map.Entry<String, String> arg: args.entrySet())
    {
      argsList.add(String.format("-%s=%s", arg.getKey(), arg.getValue()));
    }

    final SchemaCrawlerCommandLine commandLine = new SchemaCrawlerCommandLine(new DatabaseSystemConnector(),
                                                                              argsList
                                                                                .toArray(new String[0]));
    commandLine.execute();

    final List<String> failures = compareOutput(referenceFileName + ".txt",
                                                testOutputFile);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  private void executeExecutableAndCheckForOutputFile(final Executable executable,
                                                      final String outputFormatValue,
                                                      final String referenceFileName)
    throws Exception
  {
    final Path testOutputFile = createTempFile(executable.getCommand(),
                                               outputFormatValue);

    final OutputOptions outputOptions = new OutputOptions(outputFormatValue,
                                                          testOutputFile
                                                            .toFile());
    outputOptions.setInputEncoding(UTF8);
    outputOptions.setOutputEncoding(UTF8);

    executable.setOutputOptions(outputOptions);
    executable.execute(getConnection());

    final Charset templateCharset = outputOptions.getInputCharset();

    final List<String> failures = compareOutput(referenceFileName + ".txt",
                                                testOutputFile,
                                                templateCharset,
                                                null);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }
}

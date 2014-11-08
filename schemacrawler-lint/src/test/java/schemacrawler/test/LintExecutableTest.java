/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.test;


import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import schemacrawler.test.utility.BaseExecutableTest;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.commandline.SchemaCrawlerCommandLine;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry.UknownDatabaseConnector;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;

public class LintExecutableTest
  extends BaseExecutableTest
{

  private static final String CONFIG_LINTER_CONFIGS_FILE = "schemacrawer.linter_configs.file";

  private static void removeLinterConfig()
  {
    System.getProperties().remove(CONFIG_LINTER_CONFIGS_FILE);
  }

  @Before
  public void before()
  {
    removeLinterConfig();
  }

  @Test
  public void commandlineLintReport()
    throws Exception
  {
    executeCommandlineAndCheckForOutputFile(TextOutputFormat.text,
                                            "executableForLint");
  }

  @Test
  public void commandlineLintReportWithConfig()
    throws Exception
  {
    useLinterConfigFile();

    executeCommandlineAndCheckForOutputFile(TextOutputFormat.text,
                                            "executableForLintWithConfig");

    removeLinterConfig();
  }

  @Test
  public void executableLintReport()
    throws Exception
  {
    executeExecutableAndCheckForOutputFile(TextOutputFormat.text,
                                           "executableForLint");
  }

  @Test
  public void executableLintReportWithConfig()
    throws Exception
  {
    useLinterConfigFile();

    executeExecutableAndCheckForOutputFile(TextOutputFormat.text,
                                           "executableForLintWithConfig");

    removeLinterConfig();
  }

  private void executeCommandlineAndCheckForOutputFile(final OutputFormat outputFormat,
                                                       final String referenceFileName)
    throws Exception
  {

    final File testOutputFile = File.createTempFile("schemacrawler.lint.",
                                                    ".test");
    testOutputFile.delete();

    final Map<String, String> args = new HashMap<>();
    args.put("driver", "org.hsqldb.jdbc.JDBCDriver");
    args.put("url", "jdbc:hsqldb:hsql://localhost/schemacrawler");
    args.put("user", "sa");
    args.put("password", "");

    args.put("infolevel", "standard");
    args.put("command", "lint");
    args.put("sortcolumns", "true");
    args.put("outputformat", outputFormat
             .getFormat());
    args.put("outputfile", testOutputFile.getAbsolutePath());

    final List<String> argsList = new ArrayList<>();
    for (final Map.Entry<String, String> arg: args.entrySet())
    {
      argsList.add(String.format("-%s=%s", arg.getKey(), arg.getValue()));
    }

    final SchemaCrawlerCommandLine commandLine = new SchemaCrawlerCommandLine(new UknownDatabaseConnector(),
                                                                              argsList
                                                                                .toArray(new String[0]));
    commandLine.execute();

    final List<String> failures = TestUtility.compareOutput(referenceFileName
                                                                + ".txt",
                                                            testOutputFile);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  private void executeExecutableAndCheckForOutputFile(final OutputFormat outputFormat,
                                                      final String referenceFileName)
    throws Exception
  {
    executeExecutableAndCheckForOutputFile(new SchemaCrawlerExecutable("lint"),
                                           outputFormat.getFormat(),
                                           referenceFileName + ".txt");
  }

  private void useLinterConfigFile()
    throws IOException
  {
    final File file = TestUtility
      .copyResourceToTempFile("/schemacrawler-linter-configs-off.xml");
    System.setProperty(CONFIG_LINTER_CONFIGS_FILE, file.getAbsolutePath());
  }

}

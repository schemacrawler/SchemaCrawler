/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.test.utility.TestDatabase;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.commandline.SchemaCrawlerCommandLine;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.lint.executable.LintExecutable;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;

public class LintExecutableTest
{

  private static TestDatabase testDatabase = new TestDatabase();

  private static final String CONFIG_LINTER_CONFIGS_FILE = "schemacrawer.linter_configs.file";

  @AfterClass
  public static void afterAllTests()
  {
    testDatabase.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws Exception
  {
    TestDatabase.initializeApplicationLogging();
    testDatabase.startMemoryDatabase();
  }

  @Before
  public void before()
  {
    System.getProperties().remove(CONFIG_LINTER_CONFIGS_FILE);
  }

  @Test
  public void commandlineLintReport()
    throws Exception
  {
    executeCommandlineAndCheckForOutputFile("lint",
                                            OutputFormat.text.name(),
                                            "executableForLint");
  }

  @Test
  public void commandlineLintReportWithConfig()
    throws Exception
  {
    useLinterConfigFile();

    executeCommandlineAndCheckForOutputFile("lint",
                                            OutputFormat.text.name(),
                                            "executableForLintWithConfig");
  }

  @Test
  public void executableLintReport()
    throws Exception
  {
    executeExecutableAndCheckForOutputFile(new LintExecutable(),
                                           OutputFormat.text.name(),
                                           "executableForLint");
  }

  @Test
  public void executableLintReportWithConfig()
    throws Exception
  {
    useLinterConfigFile();

    executeExecutableAndCheckForOutputFile(new LintExecutable(),
                                           OutputFormat.text.name(),
                                           "executableForLintWithConfig");
  }

  private void executeCommandlineAndCheckForOutputFile(final String command,
                                                       final String outputFormatValue,
                                                       final String referenceFileName)
    throws Exception
  {

    final File testOutputFile = File.createTempFile("schemacrawler." + command
                                                    + ".", ".test");
    testOutputFile.delete();

    final SchemaCrawlerCommandLine commandLine = new SchemaCrawlerCommandLine(testDatabase
                                                                                .getDatabaseConnectionOptions(),
                                                                              "-command="
                                                                                  + command,
                                                                              "-infolevel=standard",
                                                                              "-sortcolumns=true",
                                                                              "-outputformat="
                                                                                  + outputFormatValue,
                                                                              "-outputfile="
                                                                                  + testOutputFile
                                                                                    .getAbsolutePath());
    commandLine.execute();

    final List<String> failures = TestUtility.compareOutput(referenceFileName
                                                                + ".txt",
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
    final File testOutputFile = File.createTempFile("schemacrawler."
                                                    + executable.getCommand()
                                                    + ".", ".test");
    testOutputFile.delete();
    final OutputOptions outputOptions = new OutputOptions(outputFormatValue,
                                                          testOutputFile);

    executable.getSchemaCrawlerOptions()
      .setAlphabeticalSortForTableColumns(true);
    executable.setOutputOptions(outputOptions);
    executable.execute(testDatabase.getConnection());

    final List<String> failures = TestUtility.compareOutput(referenceFileName
                                                                + ".txt",
                                                            testOutputFile);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  private void useLinterConfigFile()
    throws IOException
  {
    final File file = TestUtility
      .copyResourceToTempFile("/schemacrawler-linter-configs-off.xml");
    System.setProperty(CONFIG_LINTER_CONFIGS_FILE, file.getAbsolutePath());
  }

}

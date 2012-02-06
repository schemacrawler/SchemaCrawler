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
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.test.utility.LocalEntityResolver;
import schemacrawler.test.utility.TestDatabase;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;

public class LintOutputTest
{

  private static final String TEXT_OUTPUT = "lint_text_output/";
  private static final String COMPOSITE_OUTPUT = "lint_composite_output/";
  private static final String JSON_OUTPUT = "lint_json_output/";

  private static TestDatabase testDatabase = new TestDatabase();

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
    XMLUnit.setControlEntityResolver(new LocalEntityResolver());
  }

  @Test
  public void compareCompositeOutput()
    throws Exception
  {
    final String queryCommand1 = "dump_top5";
    final Config queriesConfig = new Config();
    queriesConfig
      .put(queryCommand1,
           "SELECT TOP 5 ${orderbycolumns} FROM ${table} ORDER BY ${orderbycolumns}");

    final String[] commands = new String[] {
        SchemaTextDetailType.list + "," + Operation.count + "," + "lint",
        queryCommand1 + "," + SchemaTextDetailType.list + "," + "lint",
    };

    final List<String> failures = new ArrayList<String>();
    for (final OutputFormat outputFormat: EnumSet.complementOf(EnumSet
      .of(OutputFormat.tsv)))
    {
      for (final String command: commands)
      {
        final String referenceFile = command + "." + outputFormat.name();

        final File testOutputFile = File.createTempFile("schemacrawler."
                                                            + referenceFile
                                                            + ".",
                                                        ".test");
        testOutputFile.delete();

        final OutputOptions outputOptions = new OutputOptions(outputFormat.name(),
                                                              testOutputFile);
        outputOptions.setNoInfo(false);
        outputOptions.setNoHeader(false);
        outputOptions.setNoFooter(false);

        final Config config = Config.loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
        final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(config);
        schemaCrawlerOptions
          .setSchemaInclusionRule(new InclusionRule(".*FOR_LINT",
                                                    InclusionRule.NONE));
        schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

        final DatabaseConnectionOptions connectionOptions = testDatabase
          .getDatabaseConnectionOptions();

        final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
        executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
        executable.setOutputOptions(outputOptions);
        executable.setAdditionalConfiguration(queriesConfig);
        executable.execute(connectionOptions.getConnection());

        failures.addAll(compareOutput(COMPOSITE_OUTPUT
                                                      + referenceFile,
                                                  testOutputFile,
                                                  outputFormat.name()));
      }
    }
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void compareJsonOutput()
    throws Exception
  {
    final List<String> failures = new ArrayList<String>();
    final InfoLevel infoLevel = InfoLevel.standard;

    final String referenceFile = "lints.json";

    final File testOutputFile = File.createTempFile("schemacrawler."
                                                        + referenceFile + ".",
                                                    ".test");
    testOutputFile.delete();

    final OutputOptions outputOptions = new OutputOptions(OutputFormat.json.name(),
                                                          testOutputFile);
    outputOptions.setNoInfo(false);
    outputOptions.setNoHeader(false);
    outputOptions.setNoFooter(false);

    final Config config = Config.loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(config);
    schemaCrawlerOptions
      .setSchemaInclusionRule(new InclusionRule(".*FOR_LINT",
                                                InclusionRule.NONE));
    schemaCrawlerOptions.setSchemaInfoLevel(infoLevel.getSchemaInfoLevel());

    final DatabaseConnectionOptions connectionOptions = testDatabase
      .getDatabaseConnectionOptions();

    final Executable executable = new SchemaCrawlerExecutable("lint");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setOutputOptions(outputOptions);
    executable.execute(connectionOptions.getConnection());

    failures.addAll(compareOutput(JSON_OUTPUT + referenceFile,
                                              testOutputFile,
                                              outputOptions.getOutputFormat().name()));

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void compareTextOutput()
    throws Exception
  {
    final List<String> failures = new ArrayList<String>();
    final InfoLevel infoLevel = InfoLevel.standard;
    final String referenceFile = "lint.txt";

    final File testOutputFile = File.createTempFile("schemacrawler."
                                                        + referenceFile + ".",
                                                    ".test");
    testOutputFile.delete();

    final OutputOptions outputOptions = new OutputOptions(OutputFormat.text.name(),
                                                          testOutputFile);
    outputOptions.setNoInfo(false);
    outputOptions.setNoHeader(false);
    outputOptions.setNoFooter(false);

    final Config config = Config.loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(config);
    schemaCrawlerOptions
      .setSchemaInclusionRule(new InclusionRule(".*FOR_LINT",
                                                InclusionRule.NONE));
    schemaCrawlerOptions.setSchemaInfoLevel(infoLevel.getSchemaInfoLevel());

    final DatabaseConnectionOptions connectionOptions = testDatabase
      .getDatabaseConnectionOptions();

    final Executable executable = new SchemaCrawlerExecutable("lint");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setOutputOptions(outputOptions);
    executable.execute(connectionOptions.getConnection());

    failures.addAll(compareOutput(TEXT_OUTPUT + referenceFile,
                                              testOutputFile,
                                              outputOptions.getOutputFormat().name()));

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }
}

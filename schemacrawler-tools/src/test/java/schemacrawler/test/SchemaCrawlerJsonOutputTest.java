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

package schemacrawler.test;


import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.clean;
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptionsBuilder;
import schemacrawler.schemacrawler.ExcludeAll;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestName;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;
import sf.util.IOUtility;

public class SchemaCrawlerJsonOutputTest
  extends BaseDatabaseTest
{

  private static final String JSON_EXTRA_OUTPUT = "json_extra_output/";

  @BeforeClass
  public static void cleanOutput()
    throws Exception
  {
    clean(JSON_EXTRA_OUTPUT);
  }

  @Rule
  public TestName testName = new TestName();

  @Test
  public void noTableJsonOutput()
    throws Exception
  {
    jsonOutput(fullName -> false, "");
  }

  @Test
  public void singleTableJsonOutput()
    throws Exception
  {
    jsonOutput(fullName -> fullName.contains("Counts"), "%Counts");
  }

  private void jsonOutput(final InclusionRule tableInclusionRule,
                          final String tableName)
    throws Exception
  {

    final List<String> failures = new ArrayList<>();
    final InfoLevel infoLevel = InfoLevel.standard;
    final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType.schema;
    final String referenceFile = testName.currentMethodName() + ".json";

    final Path testOutputFile = IOUtility
      .createTempFilePath(referenceFile, TextOutputFormat.json.getFormat());

    final OutputOptions outputOptions = new OutputOptions(TextOutputFormat.json,
                                                          testOutputFile);

    final Config config = loadHsqldbConfig();

    final DatabaseSpecificOverrideOptionsBuilder databaseSpecificOverrideOptionsBuilder = new DatabaseSpecificOverrideOptionsBuilder()
      .fromConfig(config);

    final Config schemaTextOptions = new SchemaTextOptionsBuilder().noInfo()
      .toConfig();

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(infoLevel.buildSchemaInfoLevel());
    schemaCrawlerOptions
      .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));
    schemaCrawlerOptions.setRoutineInclusionRule(new ExcludeAll());
    schemaCrawlerOptions.setTableInclusionRule(tableInclusionRule);
    schemaCrawlerOptions.setTableNamePattern(tableName);

    final Executable executable = new SchemaCrawlerExecutable(schemaTextDetailType
      .name());
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setOutputOptions(outputOptions);
    executable.setAdditionalConfiguration(schemaTextOptions);
    executable.execute(getConnection(),
                       databaseSpecificOverrideOptionsBuilder.toOptions());

    failures.addAll(compareOutput(JSON_EXTRA_OUTPUT + referenceFile,
                                  testOutputFile,
                                  outputOptions.getOutputFormatValue()));
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

}

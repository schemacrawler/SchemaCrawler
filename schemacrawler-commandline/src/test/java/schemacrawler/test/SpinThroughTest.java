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
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.Main;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptionsBuilder;
import schemacrawler.schemacrawler.IncludeAll;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.integration.graph.GraphOutputFormat;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import sf.util.IOUtility;

public class SpinThroughTest
  extends BaseDatabaseTest
{

  private static final String SPIN_THROUGH_OUTPUT = "spin_through_output/";

  private static final OutputFormat[] outputFormats = new OutputFormat[] {
                                                                           TextOutputFormat.text,
                                                                           TextOutputFormat.html,
                                                                           TextOutputFormat.json,
                                                                           GraphOutputFormat.htmlx,
                                                                           GraphOutputFormat.scdot };

  @BeforeClass
  public static void clean()
    throws Exception
  {
    TestUtility.clean(SPIN_THROUGH_OUTPUT);
  }

  private Path hsqldbProperties;

  @Before
  public void copyResources()
    throws IOException
  {
    hsqldbProperties = copyResourceToTempFile("/hsqldb.INFORMATION_SCHEMA.config.properties");
  }

  @Test
  public void spinThroughExecutable()
    throws Exception
  {
    final List<String> failures = new ArrayList<>();
    for (final InfoLevel infoLevel: InfoLevel.values())
    {
      if (infoLevel == InfoLevel.unknown)
      {
        continue;
      }
      for (final OutputFormat outputFormat: outputFormats)
      {
        for (final SchemaTextDetailType schemaTextDetailType: SchemaTextDetailType
          .values())
        {
          final String referenceFile = referenceFile(schemaTextDetailType,
                                                     infoLevel,
                                                     outputFormat);
          final Path testOutputFile = createTempFile(schemaTextDetailType,
                                                     infoLevel,
                                                     outputFormat);

          final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                                testOutputFile);

          final Config config = Config.loadFile(hsqldbProperties.toString());

          final DatabaseSpecificOverrideOptionsBuilder databaseSpecificOverrideOptionsBuilder = new DatabaseSpecificOverrideOptionsBuilder();
          databaseSpecificOverrideOptionsBuilder.fromConfig(config);

          final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
          schemaCrawlerOptions
            .setSchemaInfoLevel(infoLevel.buildSchemaInfoLevel());
          schemaCrawlerOptions.setSequenceInclusionRule(new IncludeAll());
          schemaCrawlerOptions.setSynonymInclusionRule(new IncludeAll());

          final Executable executable = new SchemaCrawlerExecutable(schemaTextDetailType
            .name());
          executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
          executable.setOutputOptions(outputOptions);
          executable
            .execute(getConnection(),
                     databaseSpecificOverrideOptionsBuilder.toOptions());

          failures
            .addAll(compareOutput(SPIN_THROUGH_OUTPUT + referenceFile,
                                  testOutputFile,
                                  outputFormat.getFormat()));
        }
      }
    }

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void spinThroughMain()
    throws Exception
  {
    final List<String> failures = new ArrayList<>();

    for (final InfoLevel infoLevel: InfoLevel.values())
    {
      if (infoLevel == InfoLevel.unknown)
      {
        continue;
      }

      for (final OutputFormat outputFormat: outputFormats)
      {
        for (final SchemaTextDetailType schemaTextDetailType: SchemaTextDetailType
          .values())
        {
          final String referenceFile = referenceFile(schemaTextDetailType,
                                                     infoLevel,
                                                     outputFormat);
          final Path testOutputFile = createTempFile(schemaTextDetailType,
                                                     infoLevel,
                                                     outputFormat);

          final Map<String, String> argsMap = new HashMap<>();
          argsMap.put("url", "jdbc:hsqldb:hsql://localhost/schemacrawler");
          argsMap.put("user", "sa");
          argsMap.put("password", null);
          argsMap.put("g", hsqldbProperties.toString());
          argsMap.put("sequences", ".*");
          argsMap.put("synonyms", ".*");
          argsMap.put("infolevel", infoLevel.name());
          argsMap.put("command", schemaTextDetailType.name());
          argsMap.put("outputformat", outputFormat.getFormat());
          argsMap.put("outputfile", testOutputFile.toString());

          Main.main(flattenCommandlineArgs(argsMap));

          failures
            .addAll(compareOutput(SPIN_THROUGH_OUTPUT + referenceFile,
                                  testOutputFile,
                                  outputFormat.getFormat()));
        }
      }
    }
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  private Path createTempFile(final SchemaTextDetailType schemaTextDetailType,
                              final InfoLevel infoLevel,
                              final OutputFormat outputFormat)
    throws IOException
  {
    return IOUtility.createTempFilePath(String
      .format("%s.%s", schemaTextDetailType, infoLevel),
                                        outputFormat.getFormat());
  }

  private String referenceFile(final SchemaTextDetailType schemaTextDetailType,
                               final InfoLevel infoLevel,
                               final OutputFormat outputFormat)
  {
    final String referenceFile = String
      .format("%d%d.%s_%s.%s",
              schemaTextDetailType.ordinal(),
              infoLevel.ordinal(),
              schemaTextDetailType,
              infoLevel,
              outputFormat.getFormat());
    return referenceFile;
  }

}

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
import static org.junit.jupiter.api.Assertions.assertAll;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.DatabaseTestUtility.loadHsqldbConfig;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.ExecutableTestUtility.outputOf;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestAssertNoSystemErrOutput;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.test.utility.TestLoggingExtension;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.integration.graph.GraphOutputFormat;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

@ExtendWith(TestAssertNoSystemErrOutput.class)
@ExtendWith(TestLoggingExtension.class)
@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class SpinThroughGraphTest
{

  private static final String SPIN_THROUGH_OUTPUT = "spin_through_graph_output/";

  @BeforeAll
  public static void clean()
    throws Exception
  {
    TestUtility.clean(SPIN_THROUGH_OUTPUT);
  }

  private Path hsqldbProperties;

  @BeforeEach
  public void copyResources()
    throws IOException
  {
    hsqldbProperties = copyResourceToTempFile("/hsqldb.INFORMATION_SCHEMA.config.properties");
  }

  @Test
  public void spinThroughExecutable(final Connection connection)
    throws Exception
  {
    assertAll(infoLevels().flatMap(infoLevel -> outputFormats()
      .flatMap(outputFormat -> schemaTextDetailTypes()
        .map(schemaTextDetailType -> () -> {

          final String referenceFile = referenceFile(schemaTextDetailType,
                                                     infoLevel,
                                                     outputFormat);

          final Config config = loadHsqldbConfig();

          final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder
            .builder();
          schemaRetrievalOptionsBuilder.fromConfig(config);

          final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
            .builder().withSchemaInfoLevel(infoLevel.toSchemaInfoLevel())
            .includeAllSequences().includeAllSynonyms().includeAllRoutines();
          final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
            .toOptions();

          final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder
            .builder();
          schemaTextOptionsBuilder.noInfo(false);

          final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(schemaTextDetailType
            .name());
          executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
          executable
            .setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());
          executable.setSchemaRetrievalOptions(schemaRetrievalOptionsBuilder
            .toOptions());

          assertThat(outputOf(executableExecution(connection,
                                                  executable,
                                                  outputFormat)),
                     hasSameContentAndTypeAs(classpathResource(SPIN_THROUGH_OUTPUT
                                                               + referenceFile),
                                             outputFormat));

        }))));
  }

  @Test
  public void spinThroughMain(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    assertAll(infoLevels().flatMap(infoLevel -> outputFormats()
      .flatMap(outputFormat -> schemaTextDetailTypes()
        .map(schemaTextDetailType -> () -> {

          final String referenceFile = referenceFile(schemaTextDetailType,
                                                     infoLevel,
                                                     outputFormat);

          final String command = schemaTextDetailType.name();

          final Map<String, String> argsMap = new HashMap<>();
          argsMap.put("g", hsqldbProperties.toString());
          argsMap.put("sequences", ".*");
          argsMap.put("synonyms", ".*");
          argsMap.put("routines", ".*");
          argsMap.put("noinfo", Boolean.FALSE.toString());
          argsMap.put("infolevel", infoLevel.name());

          assertThat(outputOf(commandlineExecution(connectionInfo,
                                                   command,
                                                   argsMap,
                                                   outputFormat)),
                     hasSameContentAndTypeAs(classpathResource(SPIN_THROUGH_OUTPUT
                                                               + referenceFile),
                                             outputFormat));

        }))));
  }

  private Stream<InfoLevel> infoLevels()
  {
    return Arrays.stream(InfoLevel.values())
      .filter(infoLevel -> infoLevel != InfoLevel.unknown);
  }

  private Stream<GraphOutputFormat> outputFormats()
  {
    return Arrays.stream(new GraphOutputFormat[] {
                                                   GraphOutputFormat.htmlx,
                                                   GraphOutputFormat.scdot });
  }

  private String referenceFile(final SchemaTextDetailType schemaTextDetailType,
                               final InfoLevel infoLevel,
                               final OutputFormat outputFormat)
  {
    final String referenceFile = String.format("%d%d.%s_%s.%s",
                                               schemaTextDetailType.ordinal(),
                                               infoLevel.ordinal(),
                                               schemaTextDetailType,
                                               infoLevel,
                                               outputFormat.getFormat());
    return referenceFile;
  }

  private Stream<SchemaTextDetailType> schemaTextDetailTypes()
  {
    return Arrays.stream(SchemaTextDetailType.values());
  }

}

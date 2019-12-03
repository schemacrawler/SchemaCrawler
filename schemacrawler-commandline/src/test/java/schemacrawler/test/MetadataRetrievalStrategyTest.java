/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.FileHasContent.*;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.TestUtility.clean;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.crawl.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.*;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextDetailType;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class MetadataRetrievalStrategyTest
{

  private static final String METADATA_RETRIEVAL_STRATEGY_OUTPUT = "metadata_retrieval_strategy_output/";

  private TestOutputStream out;
  private TestOutputStream err;

  @AfterEach
  public void cleanUpStreams()
  {
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
  }

  @Test
  public void overrideMetadataRetrievalStrategy(final TestContext testContext,
                                                final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    clean(METADATA_RETRIEVAL_STRATEGY_OUTPUT);

    final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType.schema;
    final InfoLevel infoLevel = InfoLevel.minimum;
    final OutputFormat outputFormat = TextOutputFormat.text;

    final Config config = new Config();
    config.put("schemacrawler.schema.retrieval.strategy.tables",
               MetadataRetrievalStrategy.data_dictionary_all.name());

    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("-info-level", infoLevel.name());
    argsMap.put("-no-info", "true");

    // Check that System.err has an error
    assertThat(outputOf(commandlineExecution(connectionInfo,
                                             schemaTextDetailType.name(),
                                             argsMap,
                                             config,
                                             outputFormat)), hasNoContent());

    assertThat(outputOf(err),
               hasSameContentAs(classpathResource(
                 METADATA_RETRIEVAL_STRATEGY_OUTPUT + testContext.testMethodName()
                 + ".stderr.txt")));
  }

  @BeforeEach
  public void setUpStreams()
    throws Exception
  {
    out = new TestOutputStream();
    System.setOut(new PrintStream(out));

    err = new TestOutputStream();
    System.setErr(new PrintStream(err));
  }

}

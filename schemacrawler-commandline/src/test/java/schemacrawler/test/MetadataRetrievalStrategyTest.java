/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
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

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;
import com.ginsberg.junit.exit.SystemExitPreventedException;

import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.MetadataRetrievalStrategy;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.test.utility.TestOutputStream;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.options.OutputFormat;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class MetadataRetrievalStrategyTest {

  private static final String METADATA_RETRIEVAL_STRATEGY_OUTPUT =
      "metadata_retrieval_strategy_output/";

  private TestOutputStream out;
  private TestOutputStream err;

  @AfterEach
  public void cleanUpStreams() {
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
  }

  @Test
  @ExpectSystemExitWithStatus(1)
  public void overrideMetadataRetrievalStrategyDataDictionary(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    clean(METADATA_RETRIEVAL_STRATEGY_OUTPUT);

    final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType.schema;
    final InfoLevel infoLevel = InfoLevel.minimum;
    final OutputFormat outputFormat = TextOutputFormat.text;

    final Map<String, Object> config = new HashMap<>();
    config.put(
        "schemacrawler.schema.retrieval.strategy.tables",
        MetadataRetrievalStrategy.data_dictionary_all.name());

    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", infoLevel.name());

    // Check that System.err has an error
    try {
      assertThat(
          outputOf(
              commandlineExecution(
                  connectionInfo, schemaTextDetailType.name(), argsMap, config, outputFormat)),
          hasNoContent());
    } catch (final SystemExitPreventedException e) {
      // Continue execution
    }

    assertThat(
        outputOf(err),
        hasSameContentAs(
            classpathResource(
                METADATA_RETRIEVAL_STRATEGY_OUTPUT
                    + testContext.testMethodName()
                    + ".stderr.txt")));
  }

  @Test
  public void overrideMetadataRetrievalStrategyNone(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    clean(METADATA_RETRIEVAL_STRATEGY_OUTPUT);

    final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType.schema;
    final InfoLevel infoLevel = InfoLevel.minimum;
    final OutputFormat outputFormat = TextOutputFormat.text;

    final Map<String, Object> config = new HashMap<>();
    config.put(
        "schemacrawler.schema.retrieval.strategy.tables", MetadataRetrievalStrategy.none.name());

    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", infoLevel.name());

    // Check that System.err has an error
    assertThat(
        outputOf(
            commandlineExecution(
                connectionInfo, schemaTextDetailType.name(), argsMap, config, outputFormat)),
        hasSameContentAs(
            classpathResource(
                METADATA_RETRIEVAL_STRATEGY_OUTPUT
                    + testContext.testMethodName()
                    + ".stdout.txt")));

    assertThat(outputOf(err), hasNoContent());
  }

  @BeforeEach
  public void setUpStreams() throws Exception {
    out = new TestOutputStream();
    System.setOut(new PrintStream(out));

    err = new TestOutputStream();
    System.setErr(new PrintStream(err));
  }
}

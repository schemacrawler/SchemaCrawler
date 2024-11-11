/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.test.script;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.ScriptTestUtility.commandLineScriptExecution;
import static schemacrawler.test.utility.TestUtility.deleteIfPossible;
import static schemacrawler.test.utility.TestUtility.readFileFully;
import static schemacrawler.test.utility.TestUtility.validateDiagram;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.ExecutableTestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class SchemaCrawlerExecutableChainTest {

  @Test
  public void commandlineChain(final DatabaseConnectionInfo connectionInfo) throws Exception {
    assertThat(
        outputOf(commandLineScriptExecution(connectionInfo, "/chain_script.js")),
        hasSameContentAs(classpathResource("chain_output.txt")));

    validateTextOutput("chain_schema.txt");
    validateDiagramOutput("chain_schema.png");
  }

  @Test
  public void executableChain(final DatabaseConnectionSource dataSource) throws Exception {
    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("script");
    final Path testOutputFile = IOUtility.createTempFilePath("sc", "data");

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder().includeAllRoutines();
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder.builder();
    textOptionsBuilder.noSchemaCrawlerInfo(false).showDatabaseInfo().showJdbcDriverInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final Config additionalConfig = SchemaTextOptionsBuilder.builder(textOptions).toConfig();
    additionalConfig.put("script", "/chain.js");

    final OutputOptions outputOptions =
        ExecutableTestUtility.newOutputOptions("text", testOutputFile);

    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setOutputOptions(outputOptions);
    executable.setAdditionalConfiguration(additionalConfig);
    executable.setSchemaRetrievalOptions(schemaRetrievalOptionsDefault);
    executable.setDataSource(dataSource);
    executable.execute();

    assertThat(
        readFileFully(testOutputFile).replaceAll("\\R", ""),
        is("Created files \"schema.txt\" and \"schema.png\""));

    validateTextOutput("schema.txt");
    validateDiagramOutput("schema.png");
  }

  private void validateDiagramOutput(String string) throws IOException {
    final Path diagramFile = Paths.get(string);
    validateDiagram(diagramFile);
    deleteIfPossible(diagramFile);
  }

  private void validateTextOutput(final String expectedResource) {
    final Path schemaFile = Paths.get(expectedResource);
    assertThat(outputOf(schemaFile), hasSameContentAs(classpathResource(expectedResource)));
    deleteIfPossible(schemaFile);
  }
}

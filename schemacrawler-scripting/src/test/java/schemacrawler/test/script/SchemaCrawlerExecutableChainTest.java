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

package schemacrawler.test.script;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static schemacrawler.test.utility.TestUtility.validateDiagram;
import static sf.util.IOUtility.readFully;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;
import sf.util.IOUtility;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class SchemaCrawlerExecutableChainTest
{

  @Test
  public void chainJavaScript(final Connection connection)
    throws Exception
  {
    final SchemaCrawlerExecutable executable =
      new SchemaCrawlerExecutable("script");
    final Path testOutputFile = IOUtility.createTempFilePath("sc", "data");

    final LimitOptionsBuilder limitOptionsBuilder =
      LimitOptionsBuilder
        .builder()
        .includeAllRoutines();
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder =
      SchemaCrawlerOptionsBuilder
        .builder()
        .withLimitOptionsBuilder(limitOptionsBuilder);
    final SchemaCrawlerOptions schemaCrawlerOptions =
      schemaCrawlerOptionsBuilder.toOptions();

    final SchemaTextOptionsBuilder textOptionsBuilder =
      SchemaTextOptionsBuilder.builder();
    textOptionsBuilder
      .noSchemaCrawlerInfo(false)
      .showDatabaseInfo()
      .showJdbcDriverInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final Config additionalConfiguration = SchemaTextOptionsBuilder
      .builder(textOptions)
      .toConfig();
    additionalConfiguration.put("script", "/chain.js");

    final OutputOptions outputOptions =
      OutputOptionsBuilder.newOutputOptions("text", testOutputFile);

    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setOutputOptions(outputOptions);
    executable.setAdditionalConfiguration(additionalConfiguration);
    executable.setConnection(connection);
    executable.execute();

    assertThat("Created files \"schema.txt\" and \"schema.png\""
               + System.lineSeparator(),
               equalTo(readFully(new FileReader(testOutputFile.toFile()))));

    final List<String> failures = compareOutput("schema.txt",
                                                Paths.get("schema.txt"),
                                                TextOutputFormat.text.name());
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }

    final Path diagramFile = Paths.get("schema.png");
    validateDiagram(diagramFile);
    Files.deleteIfExists(diagramFile);
  }

}

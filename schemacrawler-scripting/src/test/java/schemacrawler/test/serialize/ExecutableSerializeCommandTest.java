/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.test.serialize;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.oneOf;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.TestUtility.fileHeaderOf;

import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.AssertNoSystemErrOutput;
import schemacrawler.test.utility.AssertNoSystemOutOutput;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.serialize.options.SerializationFormat;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@AssertNoSystemErrOutput
@AssertNoSystemOutOutput
@WithTestDatabase
public class ExecutableSerializeCommandTest {

  private static Path executeSerialize(
      final DatabaseConnectionSource dataSource, final SerializationFormat serializationFormat)
      throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions());

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("serialize");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setSchemaRetrievalOptions(schemaRetrievalOptionsDefault);

    return executableExecution(dataSource, executable, serializationFormat);
  }

  @Test
  @Disabled("Cannot compare files during testing, since a new file is generated")
  public void executableSerializeJava(final DatabaseConnectionSource dataSource) throws Exception {
    assertThat(fileHeaderOf(executeSerialize(dataSource, SerializationFormat.ser)), is("ACED"));
  }

  @Test
  public void executableSerializeJson(final DatabaseConnectionSource dataSource) throws Exception {
    assertThat(
        fileHeaderOf(executeSerialize(dataSource, SerializationFormat.json)),
        is(oneOf("7B0D", "7B0A")));
  }

  @Test
  public void executableSerializeYaml(final DatabaseConnectionSource dataSource) throws Exception {
    assertThat(fileHeaderOf(executeSerialize(dataSource, SerializationFormat.yaml)), is("2D2D"));
  }
}

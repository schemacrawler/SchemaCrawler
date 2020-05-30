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
package schemacrawler.integration.test;


import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.javaVersion;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.testcontainers.containers.CockroachContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

@Testcontainers(disabledWithoutDocker = true)
@EnabledIfSystemProperty(named = "heavydb", matches = "^((?!(false|no)).)*$")
public class CockroachDBTest
  extends BaseAdditionalDatabaseTest
{

  @Container
  private JdbcDatabaseContainer dbContainer = new CockroachContainer();

  @BeforeEach
  public void createDatabase()
    throws SQLException, SchemaCrawlerException
  {
    createDataSource(dbContainer.getJdbcUrl(),
                     dbContainer.getUsername(),
                     dbContainer.getPassword());

    createDatabase("/cockroachdb.scripts.txt");
  }

  @Test
  public void testCockroachDBWithConnection()
    throws Exception
  {
    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder()
      .includeSchemas(new RegularExpressionInclusionRule("public"))
      .includeAllSequences()
      .includeAllSynonyms()
      .tableTypes("TABLE,VIEW");
    final LoadOptionsBuilder loadOptionsBuilder = LoadOptionsBuilder.builder()
      .withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder.builder()
      .withLimitOptionsBuilder(limitOptionsBuilder)
      .withLoadOptionsBuilder(loadOptionsBuilder);
    final SchemaCrawlerOptions options =
      schemaCrawlerOptionsBuilder.toOptions();

    final SchemaTextOptionsBuilder textOptionsBuilder =
      SchemaTextOptionsBuilder.builder();
    textOptionsBuilder
      .showDatabaseInfo()
      .showJdbcDriverInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable =
      new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder
                                            .builder(textOptions)
                                            .toConfig());

    final String expectedResultsResource = String.format("testCockroachDBWithConnection.%s.txt", javaVersion());
    assertThat(outputOf(executableExecution(getConnection(), executable)),
               hasSameContentAs(classpathResource(expectedResultsResource)));
  }

}

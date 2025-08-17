/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.executableOf;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.DatabaseUser;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.property.Property;

public abstract class BaseOracleWithConnectionTest extends BaseAdditionalDatabaseTest {

  protected void testOracleWithConnection(
      final DatabaseConnectionSource dataSource,
      final String expectedResource,
      final int numDatabaseUsers,
      final boolean noInfo)
      throws Exception {

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("BOOKS"))
            .includeAllSequences()
            .includeAllSynonyms()
            .includeRoutines(new RegularExpressionInclusionRule("[0-9a-zA-Z_\\.]*"))
            .tableTypes("TABLE,VIEW,MATERIALIZED VIEW");
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder.builder();
    if (noInfo) {
      textOptionsBuilder.noInfo();
    } else {
      textOptionsBuilder.showDatabaseInfo().showJdbcDriverInfo();
    }
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    // -- Schema output tests
    assertThat(
        outputOf(executableExecution(dataSource, executable)),
        hasSameContentAs(classpathResource(expectedResource)));

    // -- Additional catalog tests
    final Catalog catalog = executable.getCatalog();

    final Optional<Table> optionalTable =
        catalog.lookupTable(new SchemaReference(null, "BOOKS"), "BOOKS");
    if (optionalTable.isPresent()) {
      final Table table = optionalTable.get();
      final Column column = table.lookupColumn("TITLE").get();
      assertThat(column.getPrivileges(), is(empty()));
    }

    final List<Property> serverInfo = new ArrayList<>(catalog.getDatabaseInfo().getServerInfo());

    assertThat(serverInfo.size(), equalTo(8));

    final List<DatabaseUser> databaseUsers = (List<DatabaseUser>) catalog.getDatabaseUsers();
    assertThat("Number of database users does not match", databaseUsers, hasSize(numDatabaseUsers));
    assertThat(
        databaseUsers.stream().map(DatabaseUser::getName).collect(Collectors.toList()),
        hasItems("SYS", "SYSTEM", "BOOKS"));
    assertThat(
        databaseUsers.stream()
            .map(databaseUser -> databaseUser.getAttributes().size())
            .collect(Collectors.toList()),
        hasItems(3));
    assertThat(
        databaseUsers.stream()
            .map(databaseUser -> databaseUser.getAttributes().keySet())
            .flatMap(Collection::stream)
            .collect(Collectors.toSet()),
        hasItems("USER_ID", "CREATED"));
  }

  protected void testSelectQuery(
      final DatabaseConnectionSource dataSource, final String expectedResource) throws Exception {
    final SchemaCrawlerExecutable executable = executableOf("authors");
    final Config additionalConfig = new Config();
    additionalConfig.put("authors", "SELECT * FROM BOOKS.AUTHORS");
    executable.setAdditionalConfiguration(additionalConfig);
    assertThat(
        outputOf(executableExecution(dataSource, executable, TextOutputFormat.text.name())),
        hasSameContentAs(classpathResource(expectedResource)));
  }
}

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static java.util.Objects.requireNonNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import schemacrawler.Main;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.DatabaseUser;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.server.hsqldb.HyperSQLDatabaseConnector;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.PortableType;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.options.OutputFormat;
import us.fatehi.utility.datasource.ConnectionDatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
public class HsqldbTest {

  @Test
  public void testHsqldbMain(final DatabaseConnectionInfo connectionInfo) throws Exception {

    final OutputFormat outputFormat = TextOutputFormat.text;
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Map<String, String> argsMap = createArgsMaps(connectionInfo);
      argsMap.put("--command", "details");
      argsMap.put("--no-info", Boolean.FALSE.toString());
      argsMap.put("--output-file", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));
    }

    final String expectedResource = "hsqldb.main.%s".formatted(outputFormat.getFormat());
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(expectedResource)));
  }

  @Test
  public void testHsqldbPortableBroad(
      final DatabaseConnectionInfo connectionInfo, final TestContext testContext) throws Exception {

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Map<String, String> argsMap = createArgsMaps(connectionInfo);
      argsMap.put("--command", "schema");
      argsMap.put("--no-info", Boolean.TRUE.toString());
      argsMap.put("--portable", PortableType.broad.name());
      argsMap.put("--output-file", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));
    }

    final String expectedResource = testContext.testMethodName() + ".txt";
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(expectedResource)));
  }

  @Test
  public void testHsqldbPortableNames(
      final DatabaseConnectionInfo connectionInfo, final TestContext testContext) throws Exception {

    final OutputFormat outputFormat = TextOutputFormat.text;
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Map<String, String> argsMap = createArgsMaps(connectionInfo);
      argsMap.put("--command", "schema");
      argsMap.put("--no-info", Boolean.TRUE.toString());
      argsMap.put("--portable", PortableType.names.name());
      argsMap.put("--output-file", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));
    }

    final String expectedResource = testContext.testMethodName() + ".txt";
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(expectedResource)));
  }

  @Test
  public void testHsqldbWithConnection(final Connection connection) throws Exception {

    final DatabaseConnector hsqldbSystemConnector = new HyperSQLDatabaseConnector();

    final SchemaRetrievalOptions schemaRetrievalOptions =
        hsqldbSystemConnector.getSchemaRetrievalOptionsBuilder(connection).toOptions();

    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    requireNonNull(schemaRetrievalOptions, "No database specific override options provided");

    final DatabaseConnectionSource dataSource = new ConnectionDatabaseConnectionSource(connection);
    final SchemaCrawler schemaCrawler =
        new SchemaCrawler(dataSource, schemaRetrievalOptions, schemaCrawlerOptions);
    final Catalog catalog = schemaCrawler.crawl();
    assertThat(catalog, notNullValue());

    assertThat(catalog.getSchemas(), hasSize(6));
    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertThat(schema, notNullValue());

    assertThat(catalog.getTables(schema), hasSize(11));
    final Table table = catalog.lookupTable(schema, "AUTHORS").orElse(null);
    assertThat(table, notNullValue());

    final Column column = table.lookupColumn("FIRSTNAME").get();
    assertThat(column.getPrivileges(), is(not(empty())));

    assertThat(table.getTriggers(), hasSize(1));
    assertThat(table.lookupTrigger("TRG_AUTHORS"), not(isEmpty()));

    final List<DatabaseUser> databaseUsers = (List<DatabaseUser>) catalog.getDatabaseUsers();
    assertThat(databaseUsers, hasSize(2));
    assertThat(
        databaseUsers.stream().map(DatabaseUser::getName).collect(Collectors.toList()),
        hasItems("OTHERUSER", "SA"));
    assertThat(
        databaseUsers.stream()
            .map(databaseUser -> databaseUser.getAttributes().size())
            .collect(Collectors.toList()),
        hasItems(3, 3));
    assertThat(
        databaseUsers.stream()
            .map(databaseUser -> databaseUser.getAttributes().keySet())
            .flatMap(Collection::stream)
            .collect(Collectors.toSet()),
        hasItems("AUTHENTICATION", "PASSWORD_DIGEST", "ADMIN"));
  }

  private Map<String, String> createArgsMaps(final DatabaseConnectionInfo connectionInfo) {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--server", "hsqldb");
    argsMap.put("--port", String.valueOf(connectionInfo.getPort()));
    argsMap.put("--database", connectionInfo.getDatabase());
    argsMap.put("--user", "sa");
    argsMap.put("--password", "");
    argsMap.put("--info-level", "maximum");
    argsMap.put("--table-types", "VIEW, TABLE, GLOBAL TEMPORARY");
    argsMap.put("--synonyms", ".*");
    argsMap.put("--routines", ".*");
    return argsMap;
  }
}

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.QueryUtility;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.test.utility.TestObjectUtility;

@WithTestDatabase
@ResolveTestContext
public class QueryUtilityTest {

  @Test
  public void getQueryFromResource() throws Exception {
    final Query query =
        QueryUtility.getQueryFromResource("Query title", "/EXT_HIDDEN_TABLE_COLUMNS.sql");
    assertThat(query.getName(), is("Query title"));
    assertThat(query.getQuery(), containsString("INFORMATION_SCHEMA.COLUMNS"));
  }

  @Test
  public void executeAgainstColumnDataType() throws Exception {

    final Connection mockConnection = TestObjectUtility.mockConnection();
    final Statement mockStatement = TestObjectUtility.mockStatement();
    lenient().when(mockConnection.createStatement()).thenReturn(mockStatement);
    final ColumnDataType mockColumnDataType = mock(ColumnDataType.class);
    when(mockColumnDataType.getName()).thenReturn("mock-column-data-type-name");

    final Query query =
        new Query(
            "SQL with column data type",
            "SELECT * FROM SOME_TABLE WHERE SOME_COLUMN_HAS = '${column-data-type}')");

    final Statement statement = mockConnection.createStatement();
    final ResultSet results =
        QueryUtility.executeAgainstColumnDataType(query, statement, mockColumnDataType);

    final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(mockStatement).execute(captor.capture());
    final String expandedSQL = captor.getValue();

    assertThat(
        expandedSQL,
        is("SELECT * FROM SOME_TABLE WHERE SOME_COLUMN_HAS = 'mock-column-data-type-name')"));
  }

  @Test
  public void executeAgainstSchema(final TestContext testContext, final Connection cxn)
      throws Exception {
    final Query query =
        new Query(
            "Tables for schema",
            tablesWhere("REGEXP_MATCHES(TABLE_SCHEMA, '${schema-inclusion-rule}')"));
    final InclusionRule schemaInclusionRule = new RegularExpressionInclusionRule("BOOKS");
    final InclusionRule tableInclusionRule = null;

    executeAgainstSchemaTest(
        testContext, cxn, query, makeLimitMap(schemaInclusionRule, tableInclusionRule));
  }

  @Test
  public void executeAgainstSchemaAndTable(final TestContext testContext, final Connection cxn)
      throws Exception {
    final Query query =
        new Query(
            "Tables for schema",
            tablesWhere(
                "REGEXP_MATCHES(TABLE_SCHEMA, '${schema-inclusion-rule}') AND"
                    + " REGEXP_MATCHES(TABLE_SCHEMA || '.' || TABLE_NAME,"
                    + " '${table-inclusion-rule}')"));
    final InclusionRule schemaInclusionRule = new RegularExpressionInclusionRule("BOOKS");
    final InclusionRule tableInclusionRule = new RegularExpressionInclusionRule(".*\\.AUTHORS");

    executeAgainstSchemaTest(
        testContext, cxn, query, makeLimitMap(schemaInclusionRule, tableInclusionRule));
  }

  @Test
  public void executeAgainstSchemaAndTableExclude(
      final TestContext testContext, final Connection cxn) throws Exception {
    final Query query =
        new Query(
            "Tables for schema",
            tablesWhere(
                "REGEXP_MATCHES(TABLE_SCHEMA, '${schema-inclusion-rule}') AND"
                    + " REGEXP_MATCHES(TABLE_SCHEMA || '.' || TABLE_NAME,"
                    + " '${table-inclusion-rule}')"));
    final InclusionRule schemaInclusionRule = new RegularExpressionInclusionRule("(?!.*INF).*");
    final InclusionRule tableInclusionRule =
        new RegularExpressionInclusionRule(".*\\.(?!.*AUTH).*");

    executeAgainstSchemaTest(
        testContext, cxn, query, makeLimitMap(schemaInclusionRule, tableInclusionRule));
  }

  @Test
  public void executeAgainstSchemaNoMatch(final TestContext testContext, final Connection cxn)
      throws Exception {
    final Query query =
        new Query(
            "Tables for schema",
            tablesWhere("REGEXP_MATCHES(TABLE_SCHEMA, '${schema-inclusion-rule}')"));
    final InclusionRule schemaInclusionRule = new RegularExpressionInclusionRule("NONE");
    final InclusionRule tableInclusionRule = null;

    int rows = 0;
    try (final Connection connection = cxn;
        final Statement statement = connection.createStatement();
        final ResultSet resultSet =
            QueryUtility.executeAgainstSchema(
                query, statement, makeLimitMap(schemaInclusionRule, tableInclusionRule))) {
      while (resultSet.next()) {
        rows++;
      }
    }

    assertThat(rows, is(0));
  }

  @Test
  public void executeAgainstSchemaNoTemplate(final TestContext testContext, final Connection cxn)
      throws Exception {
    final Query query = new Query("Tables for schema", tablesWhere("TABLE_SCHEMA = 'BOOKS'"));
    final InclusionRule schemaInclusionRule = new RegularExpressionInclusionRule("NONE");
    final InclusionRule tableInclusionRule = null;

    executeAgainstSchemaTest(
        testContext, cxn, query, makeLimitMap(schemaInclusionRule, tableInclusionRule));
  }

  @Test
  public void executeAgainstTable(final TestContext testContext, final Connection cxn)
      throws Exception {
    final Query query =
        new Query(
            "Tables for schema",
            tablesWhere(
                "REGEXP_MATCHES(TABLE_SCHEMA || '.' || TABLE_NAME, '${table-inclusion-rule}')"));
    final InclusionRule schemaInclusionRule = null;
    final InclusionRule tableInclusionRule = new RegularExpressionInclusionRule(".*\\.AUTHORS");

    executeAgainstSchemaTest(
        testContext, cxn, query, makeLimitMap(schemaInclusionRule, tableInclusionRule));
  }

  @Test
  public void executeForScalar(final TestContext testContext, final Connection cxn)
      throws Exception {
    final Query query =
        new Query(
            "Select scalar",
            "SELECT POSTALCODE FROM PUBLIC.BOOKS.AUTHORS WHERE LASTNAME = 'Shaffer'");

    final Object scalar = QueryUtility.executeForScalar(query, cxn);

    assertThat(scalar, notNullValue());
    assertThat(scalar, is("37032"));
  }

  @Test
  public void executeForScalarNotPresent(final TestContext testContext, final Connection cxn)
      throws Exception {
    final Query query =
        new Query(
            "Select scalar",
            "SELECT POSTALCODE FROM PUBLIC.BOOKS.AUTHORS WHERE LASTNAME = 'Fatehi'");

    final Object scalar = QueryUtility.executeForScalar(query, cxn);

    assertThat(scalar, nullValue());
  }

  private void executeAgainstSchemaTest(
      final TestContext testContext,
      final Connection cxn,
      final Query query,
      final Map<String, InclusionRule> limitMap)
      throws IOException, SQLException {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      try (final Connection connection = cxn;
          final Statement statement = connection.createStatement();
          final ResultSet resultSet =
              QueryUtility.executeAgainstSchema(query, statement, limitMap)) {
        while (resultSet.next()) {
          out.println(
              String.format(
                  "%s.%s", resultSet.getString("TABLE_SCHEMA"), resultSet.getString("TABLE_NAME")));
        }
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  private Map<String, InclusionRule> makeLimitMap(
      final InclusionRule schemaInclusionRule, final InclusionRule tableInclusionRule) {
    final Map<String, InclusionRule> limitMap = new HashMap<>();
    limitMap.put("schema-inclusion-rule", schemaInclusionRule);
    limitMap.put("table-inclusion-rule", tableInclusionRule);
    return limitMap;
  }

  private String tablesWhere(final String where) {
    return String.format(
        "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE %s ORDER BY TABLE_SCHEMA, TABLE_NAME",
        where);
  }
}

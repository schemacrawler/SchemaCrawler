/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
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
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.QueryUtility;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;

@WithTestDatabase
@ResolveTestContext
public class QueryUtilityTest {

  @Test
  public void executeAgainstSchema(final TestContext testContext, final Connection cxn)
      throws Exception {
    final Query query =
        new Query("Tables for schema", tablesWhere("REGEXP_MATCHES(TABLE_SCHEMA, '${schemas}')"));
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
                "REGEXP_MATCHES(TABLE_SCHEMA, '${schemas}') AND REGEXP_MATCHES(TABLE_NAME, '${tables}')"));
    final InclusionRule schemaInclusionRule = new RegularExpressionInclusionRule("BOOKS");
    final InclusionRule tableInclusionRule = new RegularExpressionInclusionRule("AUTHORS");

    executeAgainstSchemaTest(
        testContext, cxn, query, makeLimitMap(schemaInclusionRule, tableInclusionRule));
  }

  @Test
  public void executeAgainstSchemaNoMatch(final TestContext testContext, final Connection cxn)
      throws Exception {
    final Query query =
        new Query("Tables for schema", tablesWhere("REGEXP_MATCHES(TABLE_SCHEMA, '${schemas}')"));
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
        new Query("Tables for schema", tablesWhere("REGEXP_MATCHES(TABLE_NAME, '${tables}')"));
    final InclusionRule schemaInclusionRule = null;
    final InclusionRule tableInclusionRule = new RegularExpressionInclusionRule("AUTHORS");

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

  protected String tablesWhere(final String where) {
    return String.format(
        "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE %s ORDER BY TABLE_SCHEMA, TABLE_NAME",
        where);
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
    limitMap.put("schemas", schemaInclusionRule);
    limitMap.put("tables", tableInclusionRule);
    return limitMap;
  }
}

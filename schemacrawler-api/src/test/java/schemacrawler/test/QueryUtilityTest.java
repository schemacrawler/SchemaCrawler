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
        new Query(
            "Tables for schema",
            "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE REGEXP_MATCHES(TABLE_SCHEMA, '${schemas}') ORDER BY TABLE_NAME");
    final InclusionRule schemaInclusionRule = new RegularExpressionInclusionRule("BOOKS");
    final InclusionRule tableInclusionRule = null;

    executeAgainstSchemaTest(testContext, cxn, query, schemaInclusionRule, tableInclusionRule);
  }

  @Test
  public void executeAgainstSchemaNoMatch(final TestContext testContext, final Connection cxn)
      throws Exception {
    final Query query =
        new Query(
            "Tables for schema",
            "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE REGEXP_MATCHES(TABLE_SCHEMA, '${schemas}') ORDER BY TABLE_NAME");
    final InclusionRule schemaInclusionRule = new RegularExpressionInclusionRule("NONE");
    final InclusionRule tableInclusionRule = null;

    int rows = 0;
    try (final Connection connection = cxn;
        final Statement statement = connection.createStatement();
        final ResultSet resultSet =
            QueryUtility.executeAgainstSchema(
                query, statement, schemaInclusionRule, tableInclusionRule)) {
      while (resultSet.next()) {
        rows++;
      }
    }

    assertThat(rows, is(0));
  }

  @Test
  public void executeAgainstSchemaNoTemplate(final TestContext testContext, final Connection cxn)
      throws Exception {
    final Query query =
        new Query(
            "Tables for schema",
            "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA= 'BOOKS' ORDER BY TABLE_NAME");
    final InclusionRule schemaInclusionRule = new RegularExpressionInclusionRule("NONE");
    final InclusionRule tableInclusionRule = null;

    executeAgainstSchemaTest(testContext, cxn, query, schemaInclusionRule, tableInclusionRule);
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
      final InclusionRule schemaInclusionRule,
      final InclusionRule tableInclusionRule)
      throws IOException, SQLException {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      try (final Connection connection = cxn;
          final Statement statement = connection.createStatement();
          final ResultSet resultSet =
              QueryUtility.executeAgainstSchema(
                  query, statement, schemaInclusionRule, tableInclusionRule)) {
        while (resultSet.next()) {
          out.println(resultSet.getString("TABLE_NAME"));
        }
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}

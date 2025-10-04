/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.apache.commons.beanutils.PropertyUtils.describe;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import schemacrawler.crawl.ResultsCrawler;
import schemacrawler.schema.ResultsColumn;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;

@WithTestDatabase
@ResolveTestContext
public class ResultColumnsTest {

  private static final Logger LOGGER = Logger.getLogger(ResultColumnsTest.class.getName());

  @Test
  public void columns(final TestContext testContext, final Connection cxn) throws Exception {

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final String sql =
          ""
              + "SELECT                                                                    "
              + " PUBLIC.BOOKS.BOOKS.TITLE AS BOOK,                                        "
              + " PUBLIC.BOOKS.AUTHORS.FIRSTNAME + ' ' + PUBLIC.BOOKS.AUTHORS.LASTNAME,    "
              + " PUBLIC.BOOKS.BOOKS.PRICE                                                 "
              + "FROM                                                                      "
              + " PUBLIC.BOOKS.BOOKS                                                       "
              + " INNER JOIN PUBLIC.BOOKS.BOOKAUTHORS                                      "
              + "   ON PUBLIC.BOOKS.BOOKS.ID = PUBLIC.BOOKS.BOOKAUTHORS.BOOKID             "
              + " INNER JOIN PUBLIC.BOOKS.AUTHORS                                          "
              + "   ON PUBLIC.BOOKS.AUTHORS.ID = PUBLIC.BOOKS.BOOKAUTHORS.AUTHORID         ";

      try (final Connection connection = cxn;
          final Statement statement = connection.createStatement();
          final ResultSet resultSet = statement.executeQuery(sql)) {

        final ResultsColumns resultColumns = new ResultsCrawler(resultSet).crawl();

        assertThat("Could not obtain result columns", resultColumns, notNullValue());

        out.println("full-name: " + resultColumns.getFullName());
        out.println("columns: " + resultColumns.getColumnsListAsString());
        out.println();

        final ResultsColumn[] columns = resultColumns.getColumns().toArray(new ResultsColumn[0]);
        for (final ResultsColumn column : columns) {
          LOGGER.log(Level.FINE, column.toString());
          out.println("column: " + column.getFullName());
          final Map<String, Object> properties = new TreeMap<>(describe(column));
          for (final Map.Entry<String, Object> property : properties.entrySet()) {
            out.println("  %s: %s".formatted(property.getKey(), property.getValue()));
          }
          out.println(
              "  database type: " + column.getColumnDataType().getDatabaseSpecificTypeName());
          out.println("  type: " + column.getColumnDataType().getStandardTypeName());
        }

        out.println();
        out.println("# Additional Tests");
        out.println("lookup C2: " + resultColumns.lookupColumn("C2").orElse(null));
        out.println("lookup PRICE: " + resultColumns.lookupColumn("PRICE").orElse(null));
        out.println(
            "lookup NOT_A_COLUMN: " + resultColumns.lookupColumn("NOT_A_COLUMN").orElse(null));
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}

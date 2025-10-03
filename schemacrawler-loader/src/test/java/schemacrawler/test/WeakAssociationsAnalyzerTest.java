/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.loader.weakassociations.ProposedWeakAssociation;
import schemacrawler.loader.weakassociations.WeakAssociationsAnalyzer;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;

@WithTestDatabase
@ResolveTestContext
@TestInstance(PER_CLASS)
public class WeakAssociationsAnalyzerTest {

  private Catalog catalog;

  @BeforeAll
  public void loadCatalog(final Connection connection) throws Exception {
    final SchemaRetrievalOptions schemaRetrievalOptions = TestUtility.newSchemaRetrievalOptions();

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions());

    catalog =
        DatabaseTestUtility.getCatalog(connection, schemaRetrievalOptions, schemaCrawlerOptions);
  }

  @Test
  public void weakAssociations(final TestContext testContext, final Connection connection)
      throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {

      final WeakAssociationsAnalyzer weakAssociationsAnalyzer =
          new WeakAssociationsAnalyzer(catalog.getTables(), w -> true);
      final Collection<ProposedWeakAssociation> proposedWeakAssociations =
          weakAssociationsAnalyzer.analyzeTables();
      assertThat(
          "Proposed weak association count does not match", proposedWeakAssociations, hasSize(6));
      for (final ProposedWeakAssociation proposedWeakAssociation : proposedWeakAssociations) {
        out.println("weak association: %s".formatted(proposedWeakAssociation));
        assertThat(
            proposedWeakAssociation.getPrimaryKeyColumn().getParent().getWeakAssociations(),
            is(empty()));
        assertThat(
            proposedWeakAssociation.getForeignKeyColumn().getParent().getWeakAssociations(),
            is(empty()));
      }
    }

    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void weakAssociationsFewTables() throws Exception {

    assertThat(
        new WeakAssociationsAnalyzer(new ArrayList<>(), w -> true).analyzeTables(), hasSize(0));

    final Table booksTable =
        catalog.lookupTable(new SchemaReference("PUBLIC", "BOOKS"), "BOOKS").get();
    final Table bookAuthorsTable =
        catalog.lookupTable(new SchemaReference("PUBLIC", "BOOKS"), "BOOKAUTHORS").get();

    assertThat(
        new WeakAssociationsAnalyzer(Arrays.asList(booksTable), w -> true).analyzeTables(),
        hasSize(0));

    assertThat(
        new WeakAssociationsAnalyzer(Arrays.asList(booksTable, bookAuthorsTable), w -> true)
            .analyzeTables(),
        hasSize(1));
  }
}

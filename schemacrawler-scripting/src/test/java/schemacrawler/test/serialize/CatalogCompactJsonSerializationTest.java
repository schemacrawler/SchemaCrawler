/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.test.serialize;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.serialize.model.CatalogDocument;
import schemacrawler.tools.formatter.serialize.CatalogSerializer;
import schemacrawler.tools.formatter.serialize.CompactSerializedCatalog;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
public class CatalogCompactJsonSerializationTest {

  @Test
  public void catalogSerializationWithCompactJson(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    final Catalog catalog =
        getCatalog(dataSource, schemaRetrievalOptionsDefault, schemaCrawlerOptions, new Config());
    final CatalogSerializer serializedCatalog = new CompactSerializedCatalog(catalog);

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      serializedCatalog.save(testout);
    }

    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void catalogSerializationWithCompactJsonAllDetails(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    final Catalog catalog =
        getCatalog(dataSource, schemaRetrievalOptionsDefault, schemaCrawlerOptions, new Config());
    final CatalogSerializer serializedCatalog =
        new CompactSerializedCatalog(catalog, CatalogDocument.allTableDetails(), null);

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      serializedCatalog.save(testout);
    }

    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void catalogSerializationWithCompactJsonAllRoutineDetails(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final LimitOptions limitOptions =
        LimitOptionsBuilder.builder()
            .fromOptions(schemaCrawlerOptions.getLimitOptions())
            .includeTables(new ExcludeAll())
            .includeAllRoutines()
            .toOptions();
    schemaCrawlerOptions =
        new SchemaCrawlerOptions(
            limitOptions,
            schemaCrawlerOptions.getFilterOptions(),
            schemaCrawlerOptions.getGrepOptions(),
            schemaCrawlerOptions.getLoadOptions());

    final Catalog catalog =
        getCatalog(dataSource, schemaRetrievalOptionsDefault, schemaCrawlerOptions, new Config());
    final CatalogSerializer serializedCatalog =
        new CompactSerializedCatalog(catalog, null, CatalogDocument.allRoutineDetails());

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      serializedCatalog.save(testout);
    }

    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}

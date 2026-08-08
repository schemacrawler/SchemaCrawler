/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.renderer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static schemacrawler.scribe.renderer.JsonUtility.yamlMapper;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.scribe.command.options.ScribeOptions;
import schemacrawler.scribe.command.options.ScribeOptionsBuilder;
import schemacrawler.scribe.okf.OkfFrontMatterSupport;
import schemacrawler.test.utility.StubExecutionState;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.test.utility.crawl.LightCatalogUtility;
import schemacrawler.test.utility.crawl.LightRoutine;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.state.ExecutionState;
import tools.jackson.databind.JsonNode;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class ScribeSupportTest {

  @Test
  public void frenchLocale(final DatabaseConnectionSource connectionSource) {
    final Catalog catalog = catalog(connectionSource);
    final ScribeOptions options =
        ScribeOptionsBuilder.builder().withLocale(Locale.FRENCH).toOptions();

    final ScribeSupport support = newHelper(catalog, options);

    assertThat(support.messages().sectionColumns(), is("Colonnes"));
    assertThat(support.databaseTitle(), is("Schéma de base de données"));
  }

  @Test
  public void explicitTitleIsPreserved(final DatabaseConnectionSource connectionSource) {
    final Catalog catalog = catalog(connectionSource);
    final ScribeOptions options =
        ScribeOptionsBuilder.builder().withTitle("Custom Title").toOptions();

    final ScribeSupport support = newHelper(catalog, options);

    assertThat(support.databaseTitle(), is("Custom Title"));
  }

  @Test
  public void markdownEscaping(final DatabaseConnectionSource connectionSource) {
    final Catalog catalog = catalog(connectionSource);
    final ScribeOptions options = ScribeOptionsBuilder.builder().toOptions();
    final ScribeSupport support = newHelper(catalog, options);

    final String escaped = support.escapeMarkdown("A|B\nC*D");
    assertThat(escaped, is("A\\|B C\\*D"));
  }

  @Test
  public void resourceInFrontMatterIsUrlEncoded() throws Exception {
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    final LightTable table = new LightTable(schema, "order details");
    final LightRoutine routine = new LightRoutine(schema, "find order");
    final Catalog catalog = LightCatalogUtility.lightCatalog(table);
    final ScribeOptions options = ScribeOptionsBuilder.builder().toOptions();
    final ScribeSupport support =
        new ScribeSupport(new StubExecutionState(catalog), options, new Lints(List.of()));
    final OkfFrontMatterSupport frontMatter = new OkfFrontMatterSupport();
    support.transferState(frontMatter);

    final JsonNode tableFrontMatter = yamlMapper.readTree(frontMatter.frontMatter(table));
    final JsonNode routineFrontMatter = yamlMapper.readTree(frontMatter.frontMatter(routine));

    final String expectedTableResource =
        "catalog://tables/"
            + URLEncoder.encode(table.getFullName(), StandardCharsets.UTF_8).replace("+", "%20");
    final String expectedRoutineResource =
        "catalog://routines/"
            + URLEncoder.encode(routine.getFullName(), StandardCharsets.UTF_8).replace("+", "%20");

    assertThat(tableFrontMatter.get("resource").asString(), is(expectedTableResource));
    assertThat(routineFrontMatter.get("resource").asString(), is(expectedRoutineResource));
  }

  @Test
  public void statsAndCrossReferences(final DatabaseConnectionSource connectionSource) {
    final Catalog catalog = catalog(connectionSource);
    final ScribeOptions options = ScribeOptionsBuilder.builder().toOptions();

    final ScribeSupport support = newHelper(catalog, options);

    assertThat(support.tableCount(), is(greaterThan(0)));

    final List<Table> allTables = support.allTables();
    assertThat(allTables.isEmpty(), is(false));
    for (int i = 1; i < allTables.size(); i++) {
      assertThat(
          allTables.get(i - 1).getFullName().compareTo(allTables.get(i).getFullName()) <= 0,
          is(true));
    }

    final Table knownTable = allTables.get(0);
    final String slug = knownTable.key().slug();
    assertThat(slug, is(not(emptyString())));
    assertThat(slug.matches("[a-z0-9_.]+"), is(true));

    boolean foundPrimaryKeyColumn = false;
    boolean foundForeignKeyColumn = false;
    for (final Table table : allTables) {
      for (final Column column : table.getColumns()) {
        if (support.isPrimaryKeyColumn(column)) {
          foundPrimaryKeyColumn = true;
        }
        if (support.isForeignKeyColumn(column)) {
          foundForeignKeyColumn = true;
        }
      }
    }
    assertThat(foundPrimaryKeyColumn, is(true));
    assertThat(foundForeignKeyColumn, is(true));

    for (final Table table : support.allTables()) {
      for (final Table referencedTable : support.referencedTables(table)) {
        assertThat(support.referencingTables(referencedTable), hasItem(table));
      }
    }

    assertThat(support.erModelStats(), is(not(nullValue())));
    final var erModelStats = support.erModelStats();
    assertThat(erModelStats.entityCounts().count(), is(greaterThanOrEqualTo(0)));
    assertThat(erModelStats.relationshipCounts().count(), is(greaterThanOrEqualTo(0)));
    assertThat(
        erModelStats.entityCounts().strongEntities()
            + erModelStats.entityCounts().weakEntities()
            + erModelStats.entityCounts().subtypes()
            + erModelStats.entityCounts().nonEntities()
            + erModelStats.entityCounts().unknown(),
        is(erModelStats.entityCounts().count()));
    assertThat(
        erModelStats.relationshipCounts().oneOne()
            + erModelStats.relationshipCounts().oneMany()
            + erModelStats.relationshipCounts().zeroOne()
            + erModelStats.relationshipCounts().zeroMany()
            + erModelStats.relationshipCounts().manyMany()
            + erModelStats.relationshipCounts().unknown(),
        is(erModelStats.relationshipCounts().count()));
    assertThat(erModelStats.implicitRelationshipCount(), is(greaterThanOrEqualTo(0)));
    assertThat(erModelStats.unmodeledTableCount(), is(greaterThanOrEqualTo(0)));
    assertThat(support.crawlTimestamp(), is(catalog.getCrawlInfo().getCrawlTimestampInstant()));
  }

  private Catalog catalog(final DatabaseConnectionSource connectionSource) {
    return getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
  }

  private ScribeSupport newHelper(final Catalog catalog, final ScribeOptions options) {
    final ExecutionState executionState = new StubExecutionState(catalog);
    return new ScribeSupport(executionState, options, new Lints(List.of()));
  }
}

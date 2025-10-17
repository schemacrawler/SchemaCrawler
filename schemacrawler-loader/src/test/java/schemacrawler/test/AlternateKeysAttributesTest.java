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
import static org.hamcrest.Matchers.arrayWithSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.loader.attributes.model.AlternateKeyAttributes;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraintColumn;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AlternateKeysAttributesTest {

  private Catalog catalog;

  @Test
  public void alternateKeyAttributesConstructor() {

    Exception exception;

    exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new AlternateKeyAttributes(
                    "schema",
                    "catalog",
                    "  ",
                    "alternate_key",
                    Arrays.asList("remarks", "other remarks"),
                    Collections.emptyMap(),
                    Arrays.asList("column1", "column2")));
    assertThat(exception.getMessage(), is("No table name provided"));

    exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new AlternateKeyAttributes(
                    "schema",
                    "catalog",
                    "table",
                    "alternate_key",
                    Arrays.asList("remarks", "other remarks"),
                    Collections.emptyMap(),
                    Collections.emptyList()));
    assertThat(exception.getMessage(), is("No columns provided"));

    exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new AlternateKeyAttributes(
                    "schema",
                    "catalog",
                    "table",
                    "alternate_key",
                    Arrays.asList("remarks", "other remarks"),
                    Collections.emptyMap(),
                    null));
    assertThat(exception.getMessage(), is("No columns provided"));

    final AlternateKeyAttributes alternateKeyAttributes =
        new AlternateKeyAttributes(
            "schema",
            "catalog",
            "table",
            "alternate_key",
            Arrays.asList("remarks", "other remarks"),
            Collections.emptyMap(),
            Arrays.asList("column1", "column2"));
    assertThat(
        alternateKeyAttributes.toString(),
        is("Alternate key attributes <schema.catalog.table.alternate_key[[column1, column2]]>"));
  }

  @Test
  public void alternateKeys(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      for (final Schema schema : schemas) {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        for (final Table table : tables) {
          out.println("  table: " + table.getFullName());
          final Collection<PrimaryKey> alternateKeys = table.getAlternateKeys();
          printAlternateKeys(alternateKeys, out);
        }
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @BeforeAll
  public void loadCatalog(final DatabaseConnectionSource dataSource) throws Exception {

    final SchemaRetrievalOptions schemaRetrievalOptions =
        DatabaseTestUtility.newSchemaRetrievalOptions();

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"))
            .includeAllSynonyms()
            .includeAllSequences()
            .includeAllRoutines();
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final Config additionalConfig = new Config();
    additionalConfig.put("attributes-file", "/attributes-alternate-keys.yaml");

    catalog =
        getCatalog(dataSource, schemaRetrievalOptions, schemaCrawlerOptions, additionalConfig);
  }

  private void printAlternateKeys(
      final Collection<? extends PrimaryKey> alternateKeys, final TestWriter out) {
    for (final PrimaryKey alternateKey : alternateKeys) {
      out.println("    alternate key: " + alternateKey.getName());
      final List<TableConstraintColumn> constrainedColumns = alternateKey.getConstrainedColumns();
      for (final TableConstraintColumn column : constrainedColumns) {
        out.println("      column: " + column);
        out.println("        key sequence: " + column.getOrdinalPosition());
      }
      out.println("      remarks: " + alternateKey.getRemarks());
      out.println("      attributes: " + alternateKey.getAttributes());
    }
  }
}

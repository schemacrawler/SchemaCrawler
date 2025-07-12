/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import static us.fatehi.utility.Utility.isBlank;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableReference;
import schemacrawler.schema.WeakAssociation;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
@TestInstance(Lifecycle.PER_CLASS)
public class WeakAssociationsAttributesTest {

  private Catalog catalog;

  @BeforeAll
  public void loadCatalog(final TestContext testContext, final DatabaseConnectionSource dataSource)
      throws Exception {

    final SchemaRetrievalOptions schemaRetrievalOptions = TestUtility.newSchemaRetrievalOptions();

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final Config additionalConfig = new Config();
    additionalConfig.put("weak-associations", Boolean.TRUE);
    additionalConfig.put("attributes-file", "/attributes-weakassociations.yaml");

    catalog =
        getCatalog(dataSource, schemaRetrievalOptions, schemaCrawlerOptions, additionalConfig);
  }

  /** Keep in sync with {@link SchemaCrawlerTest#weakAssociations() LabelName} */
  @Test
  public void weakAssociations(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      for (final Schema schema : schemas) {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        for (final Table table : tables) {
          out.println("  table: " + table.getFullName());
          final Collection<ForeignKey> foreignKeys = table.getForeignKeys();
          printTableReferences("foreign-key", foreignKeys, out);
          final Collection<WeakAssociation> weakAssociations = table.getWeakAssociations();
          printTableReferences("weak-association", weakAssociations, out);
        }
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  private void printTableReferences(
      final String tableReferenceType,
      final Collection<? extends TableReference> tableReferences,
      final TestWriter out) {
    for (final TableReference tableReference : tableReferences) {
      out.println("    " + tableReferenceType + ": " + tableReference.getName());
      out.println("      column references: ");
      final List<ColumnReference> columnReferences = tableReference.getColumnReferences();
      for (int i = 0; i < columnReferences.size(); i++) {
        final ColumnReference columnReference = columnReferences.get(i);
        out.println("        key sequence: " + (i + 1));
        out.println("          " + columnReference);
      }
      // Remarks
      final String remarks = tableReference.getRemarks();
      if (!isBlank(remarks)) {
        out.println("      remarks: " + remarks);
      }
      // Attributes
      final Map<String, Object> attributes = tableReference.getAttributes();
      if (!attributes.isEmpty()) {
        out.println("      attributes: ");
        final Set<Entry<String, Object>> entrySet = attributes.entrySet();
        for (final Entry<String, Object> entry : entrySet) {
          out.println(String.format("        %s: %s", entry.getKey(), entry.getValue()));
        }
      }
    }
  }
}

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Identifiers;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.crawl.LightForeignKey;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.tools.command.text.diagram.options.DiagramOptions;
import schemacrawler.tools.command.text.diagram.options.DiagramOptionsBuilder;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.state.AbstractExecutionState;
import schemacrawler.tools.text.formatter.diagram.SchemaDotFormatter;
import schemacrawler.tools.traversal.ModelHelper;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
@Tag("graphviz")
public class DotFormatterCoverageTest {

  private static final String FORMATTER_COVERAGE_OUTPUT = "formatter_coverage/";

  @Test
  public void blankTable(final TestContext testContext) throws Exception {

    final Table table = new LightTable(new SchemaReference(), "TEST_TABLE");

    checkDotOutputForTable(table, testContext.testMethodFullName());
  }

  @Test
  public void generatedColumnTable(final TestContext testContext) throws Exception {

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addGeneratedColumn("GENERATED_COLUMN");

    checkDotOutputForTable(table, testContext.testMethodFullName());
  }

  @Test
  public void hiddenColumnTable(final TestContext testContext) throws Exception {

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addHiddenColumn("HIDDEN_COLUMN");

    checkDotOutputForTable(table, testContext.testMethodFullName());
  }

  @Test
  public void nullTable(final TestContext testContext) throws Exception {

    final Table table = null;
    assertThrows(
        NullPointerException.class,
        () -> checkDotOutputForTable(table, testContext.testMethodFullName()));
  }

  @Test
  public void filteredForeignKeyEndpointsRespectVisibilityAndDisplayOption() throws Exception {
    final RelationshipFixture fixture = relationshipFixture(false, false);

    final String hiddenByDefault =
        dotOutput(fixture.referencingTable(), Set.of(fixture.referencingTable().key()), false);
    assertThat(hiddenByDefault, not(containsString(fixture.foreignKey().getName())));

    final String shownWithFilteredTables =
        dotOutput(fixture.referencingTable(), Set.of(fixture.referencingTable().key()), true);
    assertThat(shownWithFilteredTables, containsString(fixture.foreignKey().getName()));
    assertThat(shownWithFilteredTables, containsString(fixture.foreignKeyColumn().key().slug()));

    final String hiddenFromReferencedTable =
        dotOutput(fixture.referencedTable(), Set.of(fixture.referencedTable().key()), false);
    assertThat(hiddenFromReferencedTable, not(containsString(fixture.foreignKey().getName())));

    final String shownFromReferencedTable =
        dotOutput(fixture.referencedTable(), Set.of(fixture.referencedTable().key()), true);
    assertThat(shownFromReferencedTable, containsString(fixture.foreignKey().getName()));
    assertThat(shownFromReferencedTable, containsString(fixture.foreignKeyColumn().key().slug()));
  }

  @Test
  public void selfReferencingForeignKeyKeepsItsSelectedNodeIdentity() throws Exception {
    final RelationshipFixture fixture = relationshipFixture(true, false);

    final String dotOutput =
        dotOutput(fixture.referencingTable(), Set.of(fixture.referencingTable().key()), false);

    assertThat(dotOutput, containsString(fixture.foreignKey().getName()));
    assertThat(dotOutput, containsString(fixture.referencingTable().key().slug()));
  }

  @Test
  public void partialEndpointRemainsFilteredEvenWhenItsKeyIsSelected() throws Exception {
    final RelationshipFixture fixture = relationshipFixture(false, true);

    final String hiddenPartialEndpoint =
        dotOutput(fixture.referencedTable(), Set.of(fixture.referencedTable().key()), false);
    assertThat(hiddenPartialEndpoint, not(containsString(fixture.foreignKey().getName())));

    final String shownPartialEndpoint =
        dotOutput(fixture.referencedTable(), Set.of(fixture.referencedTable().key()), true);
    assertThat(shownPartialEndpoint, containsString(fixture.foreignKey().getName()));
  }

  private void checkDotOutputForTable(final Table table, final String referenceFileName) {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final DiagramOptions diagramOptions = DiagramOptionsBuilder.builder().toOptions();
      final OutputOptionsBuilder outputOptionsBuilder =
          OutputOptionsBuilder.builder()
              .withOutputFormatValue(DiagramOutputFormat.scdot.name())
              .withOutputWriter(out);

      final ModelHelper modelHelper = ModelHelper.from(new AbstractExecutionState() {});

      final OutputOptions outputOptions = outputOptionsBuilder.toOptions();
      final SchemaDotFormatter formatter =
          new SchemaDotFormatter(
              SchemaTextDetailType.details,
              diagramOptions,
              outputOptions,
              Identifiers.STANDARD,
              modelHelper,
              t -> true);

      formatter.handle(table);
    }
    assertThat(
        outputOf(testout.getFilePath()),
        hasSameContentAs(
            classpathResource(FORMATTER_COVERAGE_OUTPUT + referenceFileName + ".dot")));
  }

  private String dotOutput(
      final Table selectedTable, final Set<?> selectedTableKeys, final boolean showFilteredTables)
      throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final DiagramOptions diagramOptions =
          DiagramOptionsBuilder.builder().showFilteredTables(showFilteredTables).toOptions();
      final OutputOptions outputOptions =
          OutputOptionsBuilder.builder()
              .withOutputFormatValue(DiagramOutputFormat.scdot.name())
              .withOutputWriter(out)
              .toOptions();
      final ModelHelper modelHelper = ModelHelper.from(new AbstractExecutionState() {});
      final Predicate<Table> tableVisibilityPredicate =
          table -> selectedTableKeys.contains(table.key());
      final SchemaDotFormatter formatter =
          new SchemaDotFormatter(
              SchemaTextDetailType.details,
              diagramOptions,
              outputOptions,
              Identifiers.STANDARD,
              modelHelper,
              tableVisibilityPredicate);

      formatter.begin();
      formatter.handle(selectedTable);
      formatter.end();
    }
    return Files.readString(testout.getFilePath());
  }

  private RelationshipFixture relationshipFixture(
      final boolean selfReferencing, final boolean partialReferencedTable) {
    final SchemaReference schema = new SchemaReference();
    final RelationshipTable referencingTable = new RelationshipTable(schema, "REFERENCING_TABLE");
    final RelationshipTable referencedTable;
    if (selfReferencing) {
      referencedTable = referencingTable;
    } else if (partialReferencedTable) {
      referencedTable = new PartialRelationshipTable(schema, "REFERENCED_TABLE");
    } else {
      referencedTable = new RelationshipTable(schema, "REFERENCED_TABLE");
    }

    final Column foreignKeyColumn = referencingTable.addColumn("PARENT_ID");
    final Column primaryKeyColumn = referencedTable.addColumn("ID");
    final ForeignKey foreignKey =
        new LightForeignKey("FK_REFERENCE", foreignKeyColumn, primaryKeyColumn);
    referencingTable.addForeignKey(foreignKey);
    if (referencedTable != referencingTable) {
      referencedTable.addForeignKey(foreignKey);
    }
    return new RelationshipFixture(referencingTable, referencedTable, foreignKey, foreignKeyColumn);
  }

  private record RelationshipFixture(
      RelationshipTable referencingTable,
      RelationshipTable referencedTable,
      ForeignKey foreignKey,
      Column foreignKeyColumn) {}

  private static class RelationshipTable extends LightTable {

    private final List<ForeignKey> foreignKeys = new ArrayList<>();

    private RelationshipTable(final SchemaReference schema, final String name) {
      super(schema, name);
    }

    @Override
    public Collection<ForeignKey> getForeignKeys() {
      return List.copyOf(foreignKeys);
    }

    private void addForeignKey(final ForeignKey foreignKey) {
      foreignKeys.add(foreignKey);
    }
  }

  private static final class PartialRelationshipTable extends RelationshipTable
      implements PartialDatabaseObject {

    private PartialRelationshipTable(final SchemaReference schema, final String name) {
      super(schema, name);
    }
  }
}

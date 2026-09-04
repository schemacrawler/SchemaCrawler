/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.jupiter.api.Test;
import schemacrawler.importance.model.DatabaseObjectNodeId;
import schemacrawler.importance.model.SchemaEdge;
import schemacrawler.importance.model.SchemaGraphModel;
import schemacrawler.importance.model.TableImportance;
import schemacrawler.importance.model.TableImportanceMetrics;
import schemacrawler.importance.report.ImportanceReportEntry;
import schemacrawler.importance.report.ImportanceReportGenerator;
import schemacrawler.inclusionrule.RegularExpressionRule;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Table;
import schemacrawler.tools.utility.TableCounts;
import schemacrawler.tools.utility.TableTraits;
import schemacrawler.utility.MetaDataUtility.SimpleDatabaseObjectType;

class ReportServiceTest {

  @Test
  void returnsFilteredEntriesOrderedByImportanceScoreThenCentralityThenFullName() {
    final Table alpha = table("ALPHA");
    final Table beta = table("BETA");
    final DatabaseObjectNodeId alphaNode = node("ALPHA");
    final DatabaseObjectNodeId betaNode = node("BETA");
    final SchemaGraphModel schemaGraphModel =
        new SchemaGraphModel(
            new DefaultDirectedGraph<>(SchemaEdge.class),
            Set.of(alphaNode, betaNode),
            Map.of(alphaNode, alpha, betaNode, beta));

    final var entries =
        new ImportanceReportGenerator(schemaGraphModel).report(new RegularExpressionRule(".*", ""));

    assertThat(entries, contains(entry(betaNode, "BETA"), entry(alphaNode, "ALPHA")));
    assertThat(entries.get(0).nodeId(), is(betaNode));
    assertThat(entries.get(0).tableFullName(), is("BETA"));
  }

  @Test
  void fallsBackToCentralityThenFullNameWhenImportanceScoresAreEqual() {
    final Table alpha = tableWithScore("ALPHA", 5, 0.0);
    final Table beta = tableWithScore("BETA", 5, 1.0);
    final DatabaseObjectNodeId alphaNode = node("ALPHA");
    final DatabaseObjectNodeId betaNode = node("BETA");
    final SchemaGraphModel schemaGraphModel =
        new SchemaGraphModel(
            new DefaultDirectedGraph<>(SchemaEdge.class),
            Set.of(alphaNode, betaNode),
            Map.of(alphaNode, alpha, betaNode, beta));

    final var entries =
        new ImportanceReportGenerator(schemaGraphModel).report(new RegularExpressionRule(".*", ""));

    assertThat(entries.get(0).tableFullName(), is("BETA"));
    assertThat(entries.get(1).tableFullName(), is("ALPHA"));
  }

  @Test
  void appliesTheSuppliedInclusionRule() {
    final Table alpha = table("ALPHA");
    final DatabaseObjectNodeId alphaNode = node("ALPHA");
    final SchemaGraphModel schemaGraphModel =
        new SchemaGraphModel(
            new DefaultDirectedGraph<>(SchemaEdge.class),
            Set.of(alphaNode),
            Map.of(alphaNode, alpha));

    final var entries =
        new ImportanceReportGenerator(schemaGraphModel)
            .report(new RegularExpressionRule(".*BETA", ""));

    assertThat(entries.isEmpty(), is(true));
  }

  @Test
  void truncatesEntriesWhenMaxTablesIsPositive() {
    final Table alpha = table("ALPHA");
    final Table beta = table("BETA");
    final DatabaseObjectNodeId alphaNode = node("ALPHA");
    final DatabaseObjectNodeId betaNode = node("BETA");
    final SchemaGraphModel schemaGraphModel =
        new SchemaGraphModel(
            new DefaultDirectedGraph<>(SchemaEdge.class),
            Set.of(alphaNode, betaNode),
            Map.of(alphaNode, alpha, betaNode, beta));

    final var entries =
        new ImportanceReportGenerator(schemaGraphModel)
            .report(new RegularExpressionRule(".*", ""), 1);

    assertThat(entries.size(), is(1));
    assertThat(entries.get(0).tableFullName(), is("BETA"));
  }

  @Test
  void returnsAllEntriesWhenMaxTablesIsZeroOrNegative() {
    final Table alpha = table("ALPHA");
    final Table beta = table("BETA");
    final DatabaseObjectNodeId alphaNode = node("ALPHA");
    final DatabaseObjectNodeId betaNode = node("BETA");
    final SchemaGraphModel schemaGraphModel =
        new SchemaGraphModel(
            new DefaultDirectedGraph<>(SchemaEdge.class),
            Set.of(alphaNode, betaNode),
            Map.of(alphaNode, alpha, betaNode, beta));

    final var entriesZero =
        new ImportanceReportGenerator(schemaGraphModel)
            .report(new RegularExpressionRule(".*", ""), 0);
    assertThat(entriesZero.size(), is(2));

    final var entriesNegative =
        new ImportanceReportGenerator(schemaGraphModel)
            .report(new RegularExpressionRule(".*", ""), -1);
    assertThat(entriesNegative.size(), is(2));
  }

  private static ImportanceReportEntry entry(
      final DatabaseObjectNodeId nodeId, final String tableFullName) {
    return new ImportanceReportEntry(
        nodeId,
        tableFullName,
        new TableImportance(
            score(tableFullName), metrics(tableFullName), new TableTraits(), new TableCounts()));
  }

  private static Table table(final String name) {
    return tableWithScore(name, score(name), "BETA".equals(name) ? 1.0 : 0.0);
  }

  private static Table tableWithScore(
      final String name, final int importanceScore, final double betweennessCentrality) {
    final Table table = mock(Table.class);
    when(table.getFullName()).thenReturn(name);
    when(table.getAttribute(TableImportance.class.getName()))
        .thenReturn(
            new TableImportance(
                importanceScore,
                new TableImportanceMetrics(0, 0, betweennessCentrality, 0, 0),
                new TableTraits(),
                new TableCounts()));
    return table;
  }

  private static int score(final String name) {
    return "BETA".equals(name) ? 10 : 5;
  }

  private static TableImportanceMetrics metrics(final String name) {
    return new TableImportanceMetrics(0, 0, "BETA".equals(name) ? 1.0 : 0.0, 0, 0);
  }

  private static DatabaseObjectNodeId node(final String name) {
    return new DatabaseObjectNodeId(new NamedObjectKey(name), SimpleDatabaseObjectType.table);
  }
}

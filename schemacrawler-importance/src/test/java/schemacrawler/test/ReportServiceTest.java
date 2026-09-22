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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.jupiter.api.Test;
import schemacrawler.importance.model.DatabaseObjectVertexId;
import schemacrawler.importance.model.ImportanceModel;
import schemacrawler.importance.model.SchemaEdge;
import schemacrawler.importance.model.TableCluster;
import schemacrawler.importance.model.TableImportance;
import schemacrawler.importance.model.TableImportanceMetrics;
import schemacrawler.importance.options.ImportanceOptions;
import schemacrawler.importance.options.ImportanceOptionsBuilder;
import schemacrawler.importance.report.ImportanceReportEntry;
import schemacrawler.importance.report.ImportanceReportGenerator;
import schemacrawler.inclusionrule.RegularExpressionRule;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Table;
import schemacrawler.test.utility.LightImportanceModel;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.tools.utility.TableCounts;
import schemacrawler.tools.utility.TableTraits;
import schemacrawler.utility.MetaDataUtility.SimpleDatabaseObjectType;

class ReportServiceTest {

  private static ImportanceReportEntry entry(
      final DatabaseObjectVertexId vertexId, final String tableFullName) {
    return new ImportanceReportEntry(
        vertexId,
        tableFullName,
        new TableImportance(
            score(tableFullName), metrics(tableFullName), new TableTraits(), new TableCounts()));
  }

  private static TableImportanceMetrics metrics(final String name) {
    return new TableImportanceMetrics(0, 0, "BETA".equals(name) ? 1 : 0, 0, 0);
  }

  private static DatabaseObjectVertexId node(final String name) {
    return new DatabaseObjectVertexId(new NamedObjectKey(name), SimpleDatabaseObjectType.table);
  }

  private static ImportanceOptions options(final String pattern, final int maxImportantTables) {
    return ImportanceOptionsBuilder.builder()
        .withTableInclusionRule(new RegularExpressionRule(pattern, ""))
        .withMaxImportantTables(maxImportantTables)
        .toOptions();
  }

  private static ImportanceModel importanceModel(
      final DefaultDirectedGraph<DatabaseObjectVertexId, SchemaEdge> catalogGraph,
      final Set<DatabaseObjectVertexId> tableVertexIds,
      final Map<DatabaseObjectVertexId, Table> objectsByVertexId,
      final List<TableCluster> tableClusters) {
    return new LightImportanceModel(catalogGraph, tableVertexIds, objectsByVertexId, tableClusters);
  }

  private static int score(final String name) {
    return "BETA".equals(name) ? 10 : 5;
  }

  private static Table table(final String name) {
    return tableWithScore(name, score(name), "BETA".equals(name) ? 1 : 0);
  }

  private static Table tableWithScore(
      final String name, final int importanceScore, final int betweennessCentrality) {
    final Table table = new LightTable(name);
    table.setAttribute(
        TableImportance.class.getName(),
        new TableImportance(
            importanceScore,
            new TableImportanceMetrics(0, 0, betweennessCentrality, 0, 0),
            new TableTraits(),
            new TableCounts()));
    return table;
  }

  @Test
  void appliesTheSuppliedInclusionRule() {
    final Table alpha = table("ALPHA");
    final DatabaseObjectVertexId alphaNode = node("ALPHA");
    final ImportanceModel importanceModel =
        importanceModel(
            new DefaultDirectedGraph<>(SchemaEdge.class),
            Set.of(alphaNode),
            Map.of(alphaNode, alpha),
            List.of());

    final var report = new ImportanceReportGenerator(importanceModel).report(options(".*BETA", -1));

    assertThat(report.tables().isEmpty(), is(true));
  }

  @Test
  void fallsBackToCentralityThenFullNameWhenImportanceScoresAreEqual() {
    final Table alpha = tableWithScore("ALPHA", 5, 0);
    final Table beta = tableWithScore("BETA", 5, 1);
    final DatabaseObjectVertexId alphaNode = node("ALPHA");
    final DatabaseObjectVertexId betaNode = node("BETA");
    final ImportanceModel importanceModel =
        importanceModel(
            new DefaultDirectedGraph<>(SchemaEdge.class),
            Set.of(alphaNode, betaNode),
            Map.of(alphaNode, alpha, betaNode, beta),
            List.of());

    final var report = new ImportanceReportGenerator(importanceModel).report(options(".*", -1));

    assertThat(report.tables().get(0).tableFullName(), is("BETA"));
    assertThat(report.tables().get(1).tableFullName(), is("ALPHA"));
  }

  @Test
  void limitsTooManyTableClusters() {
    final Table alpha = table("ALPHA");
    final Table beta = table("BETA");
    final DatabaseObjectVertexId alphaNode = node("ALPHA");
    final DatabaseObjectVertexId betaNode = node("BETA");
    final TableCluster firstCluster =
        new TableCluster(UUID.randomUUID(), alphaNode, List.of(alphaNode));
    final TableCluster secondCluster =
        new TableCluster(UUID.randomUUID(), betaNode, List.of(betaNode));
    final ImportanceModel importanceModel =
        importanceModel(
            new DefaultDirectedGraph<>(SchemaEdge.class),
            Set.of(alphaNode, betaNode),
            Map.of(alphaNode, alpha, betaNode, beta),
            List.of(firstCluster, secondCluster));

    final ImportanceOptions options =
        ImportanceOptionsBuilder.builder()
            .withTableInclusionRule(new RegularExpressionRule(".*", ""))
            .withMaxClusters(1)
            .toOptions();
    final var report = new ImportanceReportGenerator(importanceModel).report(options);

    assertThat(report.clusters(), hasSize(1));
    assertThat(report.clusters().get(0).id(), is(firstCluster.id()));
  }

  @Test
  void returnsAllEntriesWhenMaxTablesIsNegative() {
    final Table alpha = table("ALPHA");
    final Table beta = table("BETA");
    final DatabaseObjectVertexId alphaNode = node("ALPHA");
    final DatabaseObjectVertexId betaNode = node("BETA");
    final ImportanceModel importanceModel =
        importanceModel(
            new DefaultDirectedGraph<>(SchemaEdge.class),
            Set.of(alphaNode, betaNode),
            Map.of(alphaNode, alpha, betaNode, beta),
            List.of());

    final var reportNegative =
        new ImportanceReportGenerator(importanceModel).report(options(".*", -1));
    assertThat(reportNegative.tables().size(), is(2));
  }

  @Test
  void returnsFilteredEntriesOrderedByImportanceScoreThenCentralityThenFullName() {
    final Table alpha = table("ALPHA");
    final Table beta = table("BETA");
    final DatabaseObjectVertexId alphaNode = node("ALPHA");
    final DatabaseObjectVertexId betaNode = node("BETA");
    final ImportanceModel importanceModel =
        importanceModel(
            new DefaultDirectedGraph<>(SchemaEdge.class),
            Set.of(alphaNode, betaNode),
            Map.of(alphaNode, alpha, betaNode, beta),
            List.of());

    final var report = new ImportanceReportGenerator(importanceModel).report(options(".*", -1));

    assertThat(report.tables(), contains(entry(betaNode, "BETA"), entry(alphaNode, "ALPHA")));
    assertThat(report.tables().get(0).tableVertexId(), is(betaNode));
    assertThat(report.tables().get(0).tableFullName(), is("BETA"));
    assertThat(report.clusters(), empty());
  }

  @Test
  void returnsNoEntriesWhenMaxTablesIsZero() {
    final Table alpha = table("ALPHA");
    final Table beta = table("BETA");
    final DatabaseObjectVertexId alphaNode = node("ALPHA");
    final DatabaseObjectVertexId betaNode = node("BETA");
    final ImportanceModel importanceModel =
        importanceModel(
            new DefaultDirectedGraph<>(SchemaEdge.class),
            Set.of(alphaNode, betaNode),
            Map.of(alphaNode, alpha, betaNode, beta),
            List.of());

    final var reportZero = new ImportanceReportGenerator(importanceModel).report(options(".*", 0));
    assertThat(reportZero.tables(), empty());
  }

  @Test
  void truncatesEntriesWhenMaxTablesIsPositive() {
    final Table alpha = table("ALPHA");
    final Table beta = table("BETA");
    final DatabaseObjectVertexId alphaNode = node("ALPHA");
    final DatabaseObjectVertexId betaNode = node("BETA");
    final ImportanceModel importanceModel =
        importanceModel(
            new DefaultDirectedGraph<>(SchemaEdge.class),
            Set.of(alphaNode, betaNode),
            Map.of(alphaNode, alpha, betaNode, beta),
            List.of());

    final var report = new ImportanceReportGenerator(importanceModel).report(options(".*", 1));

    assertThat(report.tables().size(), is(1));
    assertThat(report.tables().get(0).tableFullName(), is("BETA"));
  }

  @Test
  void usesClustersCachedOnTheImportanceModel() {
    final Table alpha = table("ALPHA");
    final DatabaseObjectVertexId alphaNode = node("ALPHA");
    final TableCluster cachedTableCluster =
        new TableCluster(UUID.randomUUID(), alphaNode, List.of(alphaNode));
    final ImportanceModel importanceModel =
        importanceModel(
            new DefaultDirectedGraph<>(SchemaEdge.class),
            Set.of(alphaNode),
            Map.of(alphaNode, alpha),
            List.of(cachedTableCluster));

    final var report = new ImportanceReportGenerator(importanceModel).report(options(".*", -1));

    assertThat(report.clusters(), hasSize(1));
    assertThat(report.clusters().get(0).id(), is(cachedTableCluster.id()));
  }
}

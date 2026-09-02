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
import schemacrawler.importance.cache.DatabaseObjectNodeId;
import schemacrawler.importance.cache.SchemaEdge;
import schemacrawler.importance.cache.SchemaGraphCache;
import schemacrawler.importance.cache.TableImportance;
import schemacrawler.importance.cache.TableImportanceMetrics;
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
  void returnsFilteredEntriesOrderedByCentralityThenFullName() {
    final Table alpha = table("ALPHA");
    final Table beta = table("BETA");
    final DatabaseObjectNodeId alphaNode = node("ALPHA");
    final DatabaseObjectNodeId betaNode = node("BETA");
    final SchemaGraphCache cache =
        new SchemaGraphCache(
            new DefaultDirectedGraph<>(SchemaEdge.class),
            Set.of(alphaNode, betaNode),
            Map.of(alphaNode, alpha, betaNode, beta));

    final var entries =
        new ImportanceReportGenerator(cache).report(new RegularExpressionRule(".*", ""));

    assertThat(entries, contains(entry(betaNode, "BETA"), entry(alphaNode, "ALPHA")));
    assertThat(entries.get(0).nodeId(), is(betaNode));
    assertThat(entries.get(0).tableFullName(), is("BETA"));
  }

  @Test
  void appliesTheSuppliedInclusionRule() {
    final Table alpha = table("ALPHA");
    final DatabaseObjectNodeId alphaNode = node("ALPHA");
    final SchemaGraphCache cache =
        new SchemaGraphCache(
            new DefaultDirectedGraph<>(SchemaEdge.class),
            Set.of(alphaNode),
            Map.of(alphaNode, alpha));

    final var entries =
        new ImportanceReportGenerator(cache).report(new RegularExpressionRule(".*BETA", ""));

    assertThat(entries.isEmpty(), is(true));
  }

  private static ImportanceReportEntry entry(
      final DatabaseObjectNodeId nodeId, final String tableFullName) {
    return new ImportanceReportEntry(
        nodeId, tableFullName, metrics(tableFullName), new TableCounts(), new TableTraits());
  }

  private static Table table(final String name) {
    final Table table = mock(Table.class);
    when(table.getFullName()).thenReturn(name);
    when(table.getAttribute(TableImportance.class.getName()))
        .thenReturn(new TableImportance(new TableTraits(), new TableCounts(), metrics(name)));
    return table;
  }

  private static TableImportanceMetrics metrics(final String name) {
    return new TableImportanceMetrics(0, 0, "BETA".equals(name) ? 1.0 : 0.0, 0, 0);
  }

  private static DatabaseObjectNodeId node(final String name) {
    return new DatabaseObjectNodeId(new NamedObjectKey(name), SimpleDatabaseObjectType.table);
  }
}

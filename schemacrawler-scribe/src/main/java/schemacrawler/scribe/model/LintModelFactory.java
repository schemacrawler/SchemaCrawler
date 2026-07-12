/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.model;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Table;
import schemacrawler.scribe.renderer.ScribeSupport;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.Lints;

/** Builds lint template models from Scribe lint results. */
public final class LintModelFactory {

  public static LintModel createLintModel(final ScribeSupport support) {
    return new LintModelFactory().createModel(support);
  }

  private LintModelFactory() {
    // Prevent instantiation
  }

  /**
   * Builds grouped lint model data.
   *
   * @param support Scribe support
   * @return Lint model with count and grouped entries
   */
  private LintModel createModel(final ScribeSupport support) {
    requireNonNull(support, "No support provided");
    final Lints lints = support.lints();
    final List<Lint<? extends Serializable>> lintEntries = lints.getLints();
    final Map<NamedObjectKey, Table> tablesByKey = new HashMap<>();
    for (final Table table : support.allTablesAndViews()) {
      tablesByKey.put(table.key(), table);
    }

    final Map<String, List<LintEntry>> grouped = new LinkedHashMap<>();
    for (final Lint<? extends Serializable> lint : lintEntries) {
      grouped
          .computeIfAbsent(lint.getLinterId(), key -> new ArrayList<>())
          .add(
              new LintEntry(
                  lint.getObjectName(),
                  tablesByKey.get(lint.getObjectKey()),
                  lint.getSeverity().toString(),
                  lint.getMessage()));
    }

    final List<LintGroup> lintGroups = new ArrayList<>();
    for (final Map.Entry<String, List<LintEntry>> entry : grouped.entrySet()) {
      lintGroups.add(new LintGroup(entry.getKey(), List.copyOf(entry.getValue())));
    }
    return new LintModel(lintEntries.size(), List.copyOf(lintGroups));
  }
}

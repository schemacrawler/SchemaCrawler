/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import schemacrawler.schema.NamedObjectKey;

/**
 * The share of tables with a surviving word in the table name or in the name of one of its columns.
 */
final class TableCoverage {

  static double of(final Names names, final List<VocabularyTerm> terms) {
    final int tableCount = names.tableCount();
    if (tableCount == 0 || terms.isEmpty()) {
      return 0;
    }
    final Set<String> termStems = new HashSet<>();
    for (final VocabularyTerm term : terms) {
      termStems.add(term.stem());
    }
    final Set<NamedObjectKey> coveredTables = new HashSet<>();
    for (final NamedItem item : names.items()) {
      final boolean hasTermStem = item.stems().stream().anyMatch(termStems::contains);
      if (!hasTermStem) {
        continue;
      }
      if (item.kind() == ObjectKind.TABLE) {
        coveredTables.add(item.key());
      } else if (item.kind() == ObjectKind.COLUMN) {
        coveredTables.add(item.parentKey());
      }
    }
    return (double) coveredTables.size() / tableCount;
  }

  private TableCoverage() {
    // Utility class
  }
}

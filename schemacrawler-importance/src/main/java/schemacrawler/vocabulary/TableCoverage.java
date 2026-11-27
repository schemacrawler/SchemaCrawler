/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import schemacrawler.schema.NamedObjectKey;
import us.fatehi.utility.UtilityMarker;

/**
 * The share of tables that use a surviving word. A table uses a word when the word is in its own
 * name, or in the name of anything it contains, such as a column, a constraint, an index or a
 * trigger. The owner of a name is the same as for ranking, so a word that is only in the name of a
 * routine does not cover any table.
 */
@UtilityMarker
final class TableCoverage {

  static double of(final Names names, final List<VocabularyTerm> terms) {
    requireNonNull(names, "No names provided");
    requireNonNull(terms, "No terms provided");

    final int tableCount = names.tableCount();
    if (tableCount == 0 || terms.isEmpty()) {
      return 0;
    }
    final Set<NamedObjectKey> owners = new HashSet<>();
    for (final VocabularyTerm term : terms) {
      owners.addAll(names.ownersOf(term.stem()));
    }
    final Set<NamedObjectKey> tableKeys = new HashSet<>();
    for (final NamedItem table : names.itemsOfKind(ObjectKind.TABLE)) {
      tableKeys.add(table.key());
    }
    owners.retainAll(tableKeys);
    return (double) owners.size() / tableCount;
  }

  private TableCoverage() {
    // Utility class
  }
}

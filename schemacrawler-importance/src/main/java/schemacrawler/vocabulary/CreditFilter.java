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
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * Keeps a word only if it appears inside a name with two or more segments. Codes such as {@code
 * VBAK} appear only as whole names, so they are never credited.
 */
final class CreditFilter implements UnaryOperator<Set<String>> {

  private final Names names;

  CreditFilter(final Names names) {
    this.names = requireNonNull(names, "No names provided");
  }

  @Override
  public Set<String> apply(final Set<String> stems) {
    final Set<String> credited = new HashSet<>();
    for (final NamedItem item : names.items()) {
      if (item.tokenization().segmentCount() >= 2) {
        credited.addAll(item.stems());
      }
    }
    credited.retainAll(stems);
    return Set.copyOf(credited);
  }
}

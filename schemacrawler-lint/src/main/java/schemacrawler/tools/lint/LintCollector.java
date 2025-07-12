/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.lint;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArraySet;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.NamedObject;

/** Thread-safe class that can collect lints from all linters. */
public final class LintCollector {

  private final Collection<Lint<? extends Serializable>> allLints;

  LintCollector() {
    allLints = new CopyOnWriteArraySet<>();
  }

  <N extends NamedObject> void addLint(final N namedObject, final Lint<?> lint) {
    requireNonNull(namedObject, "No named object provided");
    requireNonNull(lint, "No lint provided");
    if (namedObject.key().equals(lint.getObjectKey())) {
      allLints.add(lint);
    }
  }

  /**
   * Get all lints collected by the collector.
   *
   * @return All lints collected by the collector.
   */
  Collection<Lint<? extends Serializable>> getLints() {
    return new HashSet<>(allLints);
  }
}

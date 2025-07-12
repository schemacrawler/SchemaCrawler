/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schema;

import java.util.function.Predicate;

public interface ReducibleCollection<N extends NamedObject> extends Iterable<N> {

  /**
   * Filter out objects.
   *
   * @param predicate Predicate to filter by.
   */
  void filter(Predicate<? super N> predicate);

  /** Reset all previously filtered objects. */
  void resetFilter();
}

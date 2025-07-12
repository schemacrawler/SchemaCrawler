/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.utility;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import static us.fatehi.utility.Utility.convertForComparison;

import java.util.Comparator;
import java.util.Objects;

import schemacrawler.schema.NamedObject;

public enum NamedObjectSort implements Comparator<NamedObject> {

  /** Alphabetical sort, case-insensitive. */
  alphabetical(comparing(namedObject -> convertForComparison(namedObject.getFullName()))),

  /** Natural sort. */
  natural(naturalOrder());

  public static NamedObjectSort getNamedObjectSort(final boolean alphabeticalSort) {
    if (alphabeticalSort) {
      return NamedObjectSort.alphabetical;
    } else {
      return NamedObjectSort.natural;
    }
  }

  private final Comparator<NamedObject> comparator;

  NamedObjectSort(final Comparator<NamedObject> comparator) {
    this.comparator = nullsLast(comparator);
  }

  /** {@inheritDoc} */
  @Override
  public int compare(final NamedObject namedObject1, final NamedObject namedObject2) {
    return Objects.compare(namedObject1, namedObject2, comparator);
  }
}

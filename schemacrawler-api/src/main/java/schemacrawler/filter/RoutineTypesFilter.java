/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.filter;

import java.util.Collection;
import java.util.function.Predicate;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineType;
import schemacrawler.schemacrawler.LimitOptions;

class RoutineTypesFilter implements Predicate<Routine> {

  private final Collection<RoutineType> routineTypes;

  public RoutineTypesFilter(final LimitOptions options) {
    if (options != null) {
      routineTypes = options.getRoutineTypes();
    } else {
      routineTypes = null;
    }
  }

  /**
   * Check for routine limiting rules.
   *
   * @param routine Routine to check
   * @return Whether the routine should be included
   */
  @Override
  public boolean test(final Routine routine) {
    final boolean include;

    if (routineTypes != null) {
      include = routineTypes.contains(routine.getRoutineType());
    } else {
      include = true;
    }

    return include;
  }
}

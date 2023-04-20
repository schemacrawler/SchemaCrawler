/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
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

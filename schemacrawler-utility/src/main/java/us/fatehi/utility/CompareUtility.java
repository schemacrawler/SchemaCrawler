/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package us.fatehi.utility;

import java.util.Iterator;
import java.util.List;

@UtilityMarker
public class CompareUtility {

  public static <T extends Comparable<? super T>> int compareLists(
      final List<? extends T> list1, final List<? extends T> list2) {

    if (list1 == null && list2 == null) {
      return 0;
    }
    if (list1 == null) {
      return -1;
    }
    if (list2 == null) {
      return 1;
    }

    int comparison = Integer.compare(list1.size(), list2.size());

    if (comparison == 0) {
      final Iterator<? extends T> iter1 = list1.iterator();
      final Iterator<? extends T> iter2 = list2.iterator();

      while (comparison == 0 && iter1.hasNext() && iter2.hasNext()) {
        final T object1 = iter1.next();
        final T object2 = iter2.next();

        comparison = object1.compareTo(object2);
      }
    }

    return comparison;
  }

  private CompareUtility() {}
}

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import static us.fatehi.utility.Utility.trimToEmpty;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityMarker
public class CollectionsUtility {

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

  public static <E> boolean listStartsWith(final List<E> main, final List<E> sub) {
    if (main == null || sub == null || main.size() < sub.size()) {
      return false;
    }
    if (main.isEmpty()) {
      return true;
    }

    return main.subList(0, sub.size()).equals(sub);
  }

  public static Collection<String> setOfStrings(final String input) {
    final Set<String> setOfStrings =
        Arrays.stream(trimToEmpty(input).split(","))
            .filter(Objects::nonNull)
            .map(String::strip)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());

    return setOfStrings;
  }

  private CollectionsUtility() {
    // Prevent instantiation
  }
}

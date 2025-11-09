/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityMarker
public class PropertiesUtility {

  public static Map<String, ? extends Object> systemProperties() {
    return filterMap(System.getProperties());
  }

  /**
   * Filters a Properties object or a map to include only string keys and non-null values.
   *
   * @param original Original Properties object or map
   * @return Filtered map
   */
  public static Map<String, ? extends Object> filterMap(final Map<?, ?> original) {
    if (original == null || original.isEmpty()) {
      return Collections.emptyMap();
    }
    final Map<String, ? extends Object> filtered =
        original.entrySet().stream()
            .filter(entry -> entry.getKey() instanceof String)
            .filter(entry -> entry.getValue() != null)
            .collect(Collectors.toMap(entry -> (String) entry.getKey(), Map.Entry::getValue));
    return filtered;
  }

  private PropertiesUtility() {
    // Prevent instantiation
  }
}

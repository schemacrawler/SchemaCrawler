/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.options;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class ConfigUtility {

  /**
   * Create config from map.
   *
   * @param map Provided map for config
   */
  public static Config fromMap(final Map<String, ? extends Object> map) {
    final Map<String, ? extends Object> filteredMap = filterMap(map);
    return new Config(filteredMap);
  }

  /**
   * Create config from properties.
   *
   * @param properties Provided properties for config
   */
  public static Config fromProperties(final Properties properties) {
    final Map<String, ? extends Object> filteredMap = filterMap(properties);
    return new Config(filteredMap);
  }

  /**
   * Creates a new empty config.
   *
   * @return New config
   */
  public static Config newConfig() {
    return fromMap(Collections.emptyMap());
  }

  /**
   * Creates a config from the system environment.
   *
   * @return System environment config
   */
  public static Config systemEnv() {
    return fromMap(System.getenv());
  }

  private static Map<String, ? extends Object> filterMap(final Map<?, ?> original) {
    if (original == null) {
      return new HashMap<>();
    }
    final Map<String, ? extends Object> filtered =
        original.entrySet().stream()
            .filter(entry -> entry.getKey() instanceof String)
            .filter(entry -> entry.getValue() != null)
            .collect(Collectors.toMap(entry -> (String) entry.getKey(), Map.Entry::getValue));
    return new HashMap<>(filtered);
  }

  private ConfigUtility() {
    // Prevent instantiation
  }
}

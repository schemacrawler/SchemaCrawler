/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.options;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import us.fatehi.utility.PropertiesUtility;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class ConfigUtility {

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

  /**
   * Create config from map.
   *
   * @param map Provided map for config
   */
  public static Config fromMap(final Map<String, ? extends Object> map) {
    final Map<String, ? extends Object> filteredMap = PropertiesUtility.filterMap(map);
    return new Config(filteredMap);
  }

  /**
   * Create config from properties.
   *
   * @param properties Provided properties for config
   */
  public static Config fromProperties(final Properties properties) {
    final Map<String, ? extends Object> filteredMap = PropertiesUtility.filterMap(properties);
    return new Config(filteredMap);
  }

  private ConfigUtility() {
    // Prevent instantiation
  }
}

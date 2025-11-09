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
   * Create config copied from another config.
   *
   * @param map Provided config
   */
  public static Config fromConfig(final Config config) {
    final Config newConfig = newConfig();
    if (config != null) {
      newConfig.merge(config);
    }
    return newConfig;
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

  /**
   * Creates a new empty config.
   *
   * @return New config
   */
  public static Config newConfig() {
    return fromMap(Collections.emptyMap());
  }

  private ConfigUtility() {
    // Prevent instantiation
  }
}

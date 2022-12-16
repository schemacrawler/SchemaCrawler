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

package schemacrawler.tools.options;

import static java.util.Objects.requireNonNull;
import static schemacrawler.utility.EnumUtility.enumValue;
import static us.fatehi.utility.Utility.isBlank;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionRule;
import schemacrawler.schemacrawler.Options;
import us.fatehi.utility.ObjectToString;
import us.fatehi.utility.string.StringFormat;

/** Configuration properties. */
public final class Config implements Options {

  public static final Logger LOGGER = Logger.getLogger(Config.class.getName());

  private final Map<String, Object> configMap;

  /** Creates an empty config. */
  public Config() {
    configMap = new HashMap<>();
  }

  public Config(final Config config) {
    this();
    merge(config);
  }

  /**
   * Copies config into a map.
   *
   * @param configMap Config to copy
   */
  public Config(final Map<String, Object> map) {
    this();
    if (map != null) {
      configMap.putAll(map);
    }
  }

  public boolean containsKey(final String key) {
    return configMap.containsKey(key);
  }

  /**
   * Gets the value of a property as a boolean.
   *
   * @param propertyName Property name
   * @return Boolean value
   */
  public boolean getBooleanValue(final String propertyName) {
    return getBooleanValue(propertyName, false);
  }

  public boolean getBooleanValue(final String propertyName, final boolean defaultValue) {
    return Boolean.parseBoolean(getStringValue(propertyName, Boolean.toString(defaultValue)));
  }

  /**
   * Gets the value of a property as an enum.
   *
   * @param propertyName Property name
   * @return Enum value
   */
  public <E extends Enum<E>> E getEnumValue(final String propertyName, final E defaultValue) {
    requireNonNull(defaultValue, "No default value provided");
    final String value = getStringValue(propertyName, defaultValue.name());
    return enumValue(value, defaultValue);
  }

  /**
   * Gets the value of a property as an integer.
   *
   * @param propertyName Property name
   * @return Integer value
   */
  public int getIntegerValue(final String propertyName, final int defaultValue) {
    try {
      return Integer.parseInt(getStringValue(propertyName, String.valueOf(defaultValue)));
    } catch (final NumberFormatException e) {
      LOGGER.log(
          Level.FINEST,
          e,
          new StringFormat("Could not parse integer value for property <%s>", propertyName));
      return defaultValue;
    }
  }

  public <T> T getObject(final String key, final T defaultValue) throws ClassCastException {
    return (T) configMap.getOrDefault(key, defaultValue);
  }

  public Optional<InclusionRule> getOptionalInclusionRule(
      final String includePatternProperty, final String excludePatternProperty) {
    final String includePattern = getStringValue(includePatternProperty, null);
    final String excludePattern = getStringValue(excludePatternProperty, null);
    if (isBlank(includePattern) && isBlank(excludePattern)) {
      return Optional.empty();
    } else {
      return Optional.of(new RegularExpressionRule(includePattern, excludePattern));
    }
  }

  /**
   * Gets the value of a property as a string.
   *
   * @param propertyName Property name
   * @param defaultValue Default value
   * @return String value
   */
  public String getStringValue(final String propertyName, final String defaultValue) {
    final Object value = configMap.get(propertyName);
    if (value == null) {
      return defaultValue;
    } else {
      return value.toString();
    }
  }

  public Map<String, Object> getSubMap(final String propertyName) {
    if (isBlank(propertyName)) {
      return new HashMap<>(configMap);
    }
    final Map<String, Object> subMap = new HashMap<>();
    for (final Entry<String, Object> configEntry : configMap.entrySet()) {
      final String fullKey = configEntry.getKey();
      if (fullKey == null || !fullKey.startsWith(propertyName)) {
        continue;
      }

      final String key = fullKey.substring(propertyName.length() + 1);
      final Object value = configEntry.getValue();
      subMap.put(key, value);
    }
    return subMap;
  }

  public void merge(final Config config) {
    if (config == null) {
      return;
    }
    this.configMap.putAll(config.configMap);
  }

  /**
   * @param key
   * @param value
   * @return
   * @see java.util.Map#put(java.lang.Object, java.lang.Object)
   */
  public Object put(final String key, final Object value) {
    if (value == null) {
      return configMap.remove(key);
    }
    if (!isBlank(key)) {
      return configMap.put(key, value);
    }
    return null;
  }

  public int size() {
    return configMap.size();
  }

  @Override
  public String toString() {
    return ObjectToString.toString(configMap);
  }
}

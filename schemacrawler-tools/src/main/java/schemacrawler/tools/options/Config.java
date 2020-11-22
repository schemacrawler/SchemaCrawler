/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

import schemacrawler.SchemaCrawlerLogger;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionRule;
import schemacrawler.schemacrawler.Options;
import us.fatehi.utility.ObjectToString;
import us.fatehi.utility.string.StringFormat;

/**
 * Configuration properties.
 *
 * @author Sualeh Fatehi
 */
public final class Config implements Options {

  public static final SchemaCrawlerLogger LOGGER =
      SchemaCrawlerLogger.getLogger(Config.class.getName());

  private final Map<String, Object> config;

  /** Creates an empty config. */
  public Config() {
    config = new HashMap<>();
  }

  public Config(final Config config) {
    this();
    if (config != null) {
      this.config.putAll(config.config);
    }
  }

  /**
   * Copies config into a map.
   *
   * @param config Config to copy
   */
  public Config(final Map<String, Object> config) {
    this();
    putAll(config);
  }

  public boolean containsKey(final String key) {
    return config.containsKey(key);
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
          new StringFormat("Could not parse integer value for property <%s>", propertyName),
          e);
      return defaultValue;
    }
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
    final Object value = config.get(propertyName);
    if (value == null) {
      return defaultValue;
    } else {
      return value.toString();
    }
  }

  public Map<String, Object> getSubMap(final String propertyName) {
    if (isBlank(propertyName)) {
      return new HashMap<>(config);
    }
    final Map<String, Object> subMap = new HashMap<>();
    for (final Entry<String, Object> configEntry : config.entrySet()) {
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

  /**
   * @param key
   * @param value
   * @return
   * @see java.util.Map#put(java.lang.Object, java.lang.Object)
   */
  public Object put(final String key, final Object value) {
    return config.put(key, value);
  }

  public void putAll(final Config m) {
    if (m == null) {
      return;
    }
    config.putAll(m.config);
  }

  public void putBooleanValue(final String propertyName, final boolean value) {
    config.put(propertyName, Boolean.toString(value));
  }

  public <E extends Enum<E>> void putEnumValue(final String propertyName, final E value) {
    if (value == null) {
      config.remove(propertyName);
    } else {
      config.put(propertyName, value.name());
    }
  }

  public void putStringValue(final String propertyName, final String value) {
    if (value == null) {
      config.remove(propertyName);
    } else {
      config.put(propertyName, value);
    }
  }

  public int size() {
    return config.size();
  }

  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }

  private void putAll(final Map<? extends String, ? extends Object> m) {
    if (m == null) {
      return;
    }
    config.putAll(m);
  }
}

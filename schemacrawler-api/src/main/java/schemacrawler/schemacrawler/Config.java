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

package schemacrawler.schemacrawler;


import static java.util.Objects.requireNonNull;
import static schemacrawler.utility.EnumUtility.enumValue;
import static sf.util.Utility.isBlank;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionRule;
import sf.util.ObjectToString;
import schemacrawler.SchemaCrawlerLogger;
import sf.util.string.StringFormat;

/**
 * Configuration properties.
 *
 * @author Sualeh Fatehi
 */
public final class Config
  implements Options, Map<String, String>
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(Config.class.getName());

  public static String getSystemConfigurationProperty(final String key,
                                                      final String defaultValue)
  {
    final String systemPropertyValue = System.getProperty(key);
    if (!isBlank(systemPropertyValue))
    {
      LOGGER.log(Level.CONFIG,
                 new StringFormat("Using value from system property <%s=%s>",
                                  key,
                                  systemPropertyValue));
      return systemPropertyValue;
    }

    final String envVariableValue = System.getenv(key);
    if (!isBlank(envVariableValue))
    {
      LOGGER.log(Level.CONFIG,
                 new StringFormat(
                   "Using value from enivronmental variable <%s=%s>",
                   key,
                   envVariableValue));
      return envVariableValue;
    }

    return defaultValue;
  }

  /**
   * Copies properties into a map.
   *
   * @param properties
   *   Properties to copy
   * @return Map of properties and values
   */
  private static Map<String, String> propertiesMap(final Properties properties)
  {
    final Map<String, String> propertiesMap = new HashMap<>();
    if (properties != null)
    {
      final Set<Map.Entry<Object, Object>> entries = properties.entrySet();
      for (final Map.Entry<Object, Object> entry : entries)
      {
        propertiesMap.put((String) entry.getKey(), (String) entry.getValue());
      }
    }
    return propertiesMap;
  }

  private final Map<String, String> config;

  /**
   * Creates an empty config.
   */
  public Config()
  {
    config = new HashMap<>();
  }

  /**
   * Copies config into a map.
   *
   * @param config
   *   Config to copy
   */
  public Config(final Map<String, String> config)
  {
    this();
    if (config != null)
    {
      putAll(config);
    }
  }

  /**
   * Copies properties into a map.
   *
   * @param properties
   *   Properties to copy
   */
  public Config(final Properties properties)
  {
    this(propertiesMap(properties));
  }

  /**
   * Gets the value of a property as a boolean.
   *
   * @param propertyName
   *   Property name
   * @return Boolean value
   */
  public boolean getBooleanValue(final String propertyName)
  {
    return getBooleanValue(propertyName, false);
  }

  public boolean getBooleanValue(final String propertyName,
                                 final boolean defaultValue)
  {
    return Boolean.parseBoolean(getStringValue(propertyName,
                                               Boolean.toString(defaultValue)));
  }

  /**
   * Gets the value of a property as an double.
   *
   * @param propertyName
   *   Property name
   * @return Double value
   */
  public double getDoubleValue(final String propertyName,
                               final double defaultValue)
  {
    try
    {
      return Double.parseDouble(getStringValue(propertyName,
                                               String.valueOf(defaultValue)));
    }
    catch (final NumberFormatException e)
    {
      LOGGER.log(Level.FINEST,
                 new StringFormat(
                   "Could not parse double value for property <%s>",
                   propertyName),
                 e);
      return defaultValue;
    }
  }

  /**
   * Gets the value of a property as an enum.
   *
   * @param propertyName
   *   Property name
   * @return Enum value
   */
  public <E extends Enum<E>> E getEnumValue(final String propertyName,
                                            final E defaultValue)
  {
    requireNonNull(defaultValue, "No default value provided");
    final String value = getStringValue(propertyName, defaultValue.name());
    return enumValue(value, defaultValue);
  }

  public InclusionRule getInclusionRuleWithDefault(final String includePatternProperty,
                                                   final String excludePatternProperty,
                                                   final InclusionRule inclusionRule)
  {
    requireNonNull(inclusionRule);
    final Optional<InclusionRule> optionalInclusionRule =
      getOptionalInclusionRule(includePatternProperty, excludePatternProperty);
    return optionalInclusionRule.orElse(inclusionRule);
  }

  /**
   * Gets the value of a property as an integer.
   *
   * @param propertyName
   *   Property name
   * @return Integer value
   */
  public int getIntegerValue(final String propertyName, final int defaultValue)
  {
    try
    {
      return Integer.parseInt(getStringValue(propertyName,
                                             String.valueOf(defaultValue)));
    }
    catch (final NumberFormatException e)
    {
      LOGGER.log(Level.FINEST,
                 new StringFormat(
                   "Could not parse integer value for property <%s>",
                   propertyName),
                 e);
      return defaultValue;
    }
  }

  /**
   * Gets the value of a property as an long.
   *
   * @param propertyName
   *   Property name
   * @return Long value
   */
  public long getLongValue(final String propertyName, final long defaultValue)
  {
    try
    {
      return Long.parseLong(getStringValue(propertyName,
                                           String.valueOf(defaultValue)));
    }
    catch (final NumberFormatException e)
    {
      LOGGER.log(Level.FINEST,
                 new StringFormat("Could not parse long value for property <%s>",
                                  propertyName),
                 e);
      return defaultValue;
    }
  }

  public Optional<InclusionRule> getOptionalInclusionRule(final String includePatternProperty,
                                                          final String excludePatternProperty)
  {
    final String includePattern = getStringValue(includePatternProperty, null);
    final String excludePattern = getStringValue(excludePatternProperty, null);
    if (isBlank(includePattern) && isBlank(excludePattern))
    {
      return Optional.empty();
    }
    else
    {
      return Optional.of(new RegularExpressionRule(includePattern,
                                                   excludePattern));
    }
  }

  /**
   * Gets the value of a property as a string.
   *
   * @param propertyName
   *   Property name
   * @param defaultValue
   *   Default value
   * @return String value
   */
  public String getStringValue(final String propertyName,
                               final String defaultValue)
  {
    String value = get(propertyName);
    if (value == null)
    {
      value = defaultValue;
    }
    return value;
  }

  /**
   * Checks if a value is available.
   *
   * @param propertyName
   *   Property name
   * @return True if a value ia available.
   */
  public boolean hasValue(final String propertyName)
  {
    return config.containsKey(propertyName);
  }

  public void putAll(final Properties properties)
  {
    config.putAll(propertiesMap(properties));
  }

  public void setBooleanValue(final String propertyName, final boolean value)
  {
    put(propertyName, Boolean.toString(value));
  }

  public <E extends Enum<E>> void setEnumValue(final String propertyName,
                                               final E value)
  {
    if (value == null)
    {
      remove(propertyName);
    }
    else
    {
      put(propertyName, value.name());
    }
  }

  public void setStringValue(final String propertyName, final String value)
  {
    if (value == null)
    {
      remove(propertyName);
    }
    else
    {
      put(propertyName, value);
    }
  }

  @Override
  public int size()
  {
    return config.size();
  }

  @Override
  public boolean isEmpty()
  {
    return config.isEmpty();
  }

  @Override
  public boolean containsKey(final Object key)
  {
    return config.containsKey(key);
  }

  @Override
  public boolean containsValue(final Object value)
  {
    return config.containsValue(value);
  }

  @Override
  public String get(final Object key)
  {
    return config.get(key);
  }

  @Override
  public String put(final String key, final String value)
  {
    return config.put(key, value);
  }

  @Override
  public String remove(final Object key)
  {
    return config.remove(key);
  }

  @Override
  public void putAll(final Map<? extends String, ? extends String> m)
  {
    if (m == null)
    {
      return;
    }
    config.putAll(m);
  }

  @Override
  public void clear()
  {
    config.clear();
  }

  @Override
  public Set<String> keySet()
  {
    return config.keySet();
  }

  @Override
  public Collection<String> values()
  {
    return config.values();
  }

  @Override
  public Set<java.util.Map.Entry<String, String>> entrySet()
  {
    return config.entrySet();
  }

  /**
   * Convert config to Properties
   *
   * @return Properties
   */
  public Properties toProperties()
  {
    final Properties properties = new Properties();
    for (final Entry<String, String> entry : config.entrySet())
    {
      properties.put(entry.getKey(), entry.getValue());
    }
    return properties;
  }

  @Override
  public String toString()
  {
    return ObjectToString.toString(this);
  }

}

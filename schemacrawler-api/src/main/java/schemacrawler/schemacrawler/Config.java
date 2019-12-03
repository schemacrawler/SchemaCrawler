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
import static sf.util.Utility.enumValue;
import static sf.util.Utility.isBlank;

import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;

import sf.util.ObjectToString;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

/**
 * Configuration properties.
 *
 * @author Sualeh Fatehi
 */
public final class Config
  implements Options, Map<String, String>
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(Config.class.getName());

  /**
   * Copies properties into a map.
   *
   * @param properties Properties to copy
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
   * @param config Config to copy
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
   * @param properties Properties to copy
   */
  public Config(final Properties properties)
  {
    this(propertiesMap(properties));
  }

  @Override
  public void clear()
  {
    config.clear();
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
  public Set<java.util.Map.Entry<String, String>> entrySet()
  {
    return config.entrySet();
  }

  @Override
  public String get(final Object key)
  {
    return config.get(key);
  }

  /**
   * Gets the value of a property as a boolean.
   *
   * @param propertyName Property name
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
   * @param propertyName Property name
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
   * @param propertyName Property name
   * @return Enum value
   */
  public <E extends Enum<E>> E getEnumValue(final String propertyName,
                                            final E defaultValue)
  {
    requireNonNull(defaultValue, "No default value provided");
    final String value = getStringValue(propertyName, defaultValue.name());
    return enumValue(value, defaultValue);
  }

  /**
   * Creates an exclusion rule, which includes everything, and then
   * excludes a pattern. If no pattern is provided, include everything,
   * since there are no excludes specified.
   *
   * @param optionName Option to look up.
   * @return Inclusion rule.
   */
  public InclusionRule getExclusionRule(final String optionName)
  {
    return getInclusionRuleWithDefault(null,
                                       optionName,
                                       () -> new IncludeAll());
  }

  /**
   * Creates an inclusion rule, which includes a pattern has no
   * excludes. If no pattern is provided, exclude everything, since
   * there are no includes specified.
   *
   * @param optionName Option to look up.
   * @return Inclusion rule.
   */
  public InclusionRule getInclusionRule(final String optionName)
  {
    return getInclusionRuleWithDefault(optionName,
                                       null,
                                       () -> new ExcludeAll());
  }

  public InclusionRule getInclusionRuleWithDefault(final String includePatternProperty,
                                                   final String excludePatternProperty,
                                                   final Supplier<InclusionRule> supplier)
  {
    requireNonNull(supplier);
    final Optional<InclusionRule> optionalInclusionRule = getOptionalInclusionRule(
      includePatternProperty,
      excludePatternProperty);
    return optionalInclusionRule.orElse(supplier.get());
  }

  /**
   * Gets the value of a property as an integer.
   *
   * @param propertyName Property name
   * @return Integer value
   */
  public int getIntegerValue(final String propertyName, final int defaultValue)
  {
    try
    {
      return Integer
        .parseInt(getStringValue(propertyName, String.valueOf(defaultValue)));
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
   * @param propertyName Property name
   * @return Long value
   */
  public long getLongValue(final String propertyName, final long defaultValue)
  {
    try
    {
      return Long
        .parseLong(getStringValue(propertyName, String.valueOf(defaultValue)));
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
      return Optional
        .of(new RegularExpressionRule(includePattern, excludePattern));
    }
  }

  /**
   * Gets the value of a property as a string.
   *
   * @param propertyName Property name
   * @param defaultValue Default value
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
   * @param propertyName Property name
   * @return True if a value ia available.
   */
  public boolean hasValue(final String propertyName)
  {
    return config.containsKey(propertyName);
  }

  @Override
  public boolean isEmpty()
  {
    return config.isEmpty();
  }

  @Override
  public Set<String> keySet()
  {
    return config.keySet();
  }

  @Override
  public String put(final String key, final String value)
  {
    return config.put(key, value);
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

  public void putAll(final Properties properties)
  {
    config.putAll(propertiesMap(properties));
  }

  @Override
  public String remove(final Object key)
  {
    return config.remove(key);
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

  @Override
  public Collection<String> values()
  {
    return config.values();
  }

}

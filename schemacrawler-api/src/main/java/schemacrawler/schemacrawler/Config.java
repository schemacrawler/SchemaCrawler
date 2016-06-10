/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.newBufferedReader;
import static java.util.Objects.requireNonNull;
import static sf.util.Utility.enumValue;
import static sf.util.Utility.isBlank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.ObjectToString;
import sf.util.StringFormat;

/**
 * Configuration properties.
 *
 * @author Sualeh Fatehi
 */
public final class Config
  implements Options, Map<String, String>
{

  private static final long serialVersionUID = 8720699738076915453L;

  private static final Logger LOGGER = Logger.getLogger(Config.class.getName());

  /**
   * Loads the SchemaCrawler configuration, and override configuration,
   * from properties files.
   *
   * @param configFilenames
   *        Configuration file name.
   * @return Configuration properties.
   * @throws IOException
   */
  public static Config load(final String... configFilenames)
    throws IOException
  {
    Properties configProperties = new Properties();
    if (configFilenames != null)
    {
      for (final String configFilename: configFilenames)
      {
        if (!isBlank(configFilename))
        {
          final Path configPath = Paths.get(configFilename).normalize()
            .toAbsolutePath();
          configProperties = loadProperties(configProperties, configPath);
        }
      }
    }
    return new Config(configProperties);
  }

  /**
   * Loads the SchemaCrawler configuration, from a properties file
   * stream.
   *
   * @param configStream
   *        Configuration stream.
   * @return Configuration properties.
   */
  public static Config loadResource(final String resource)
  {
    Properties configProperties = new Properties();

    final InputStream stream;
    if (!isBlank(resource))
    {
      stream = Config.class.getResourceAsStream(resource);
    }
    else
    {
      stream = null;
    }

    if (stream != null)
    {
      configProperties = loadProperties(configProperties,
                                        new InputStreamReader(stream));
    }

    return new Config(configProperties);
  }

  /**
   * Loads a properties file.
   *
   * @param properties
   *        Properties object.
   * @param propertiesFile
   *        Properties file.
   * @return Properties
   * @throws IOException
   */
  private static Properties loadProperties(final Properties properties,
                                           final Path propertiesFile)
    throws IOException
  {
    if (propertiesFile == null || !isRegularFile(propertiesFile)
        || !isReadable(propertiesFile))
    {
      LOGGER.log(Level.CONFIG,
                 new StringFormat("Cannot load properties from file, %s",
                                  propertiesFile));
      return properties;
    }

    LOGGER.log(Level.INFO,
               new StringFormat("Loading properties from file, %s",
                                propertiesFile));
    loadProperties(properties,
                   newBufferedReader(propertiesFile, StandardCharsets.UTF_8));
    return properties;
  }

  /**
   * Loads a properties file.
   *
   * @param properties
   *        Properties object.
   * @param reader
   *        Properties data stream.
   * @return Properties
   */
  private static Properties loadProperties(final Properties properties,
                                           final Reader reader)
  {
    if (properties == null || reader == null)
    {
      LOGGER.log(Level.WARNING, "No properties provided");
      return new Properties();
    }

    try (final BufferedReader bufferedReader = new BufferedReader(reader);)
    {
      properties.load(bufferedReader);
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, "Error loading properties", e);
    }
    return properties;
  }

  /**
   * Copies properties into a map.
   *
   * @param properties
   *        Properties to copy
   * @return Map of properties and values
   */
  private static Map<String, String> propertiesMap(final Properties properties)
  {
    final Map<String, String> propertiesMap = new HashMap<>();
    if (properties != null)
    {
      final Set<Map.Entry<Object, Object>> entries = properties.entrySet();
      for (final Map.Entry<Object, Object> entry: entries)
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
   *        Config to copy
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
   *        Properties to copy
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
   * @param propertyName
   *        Property name
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
   * Gets the value of a property as an enum.
   *
   * @param propertyName
   *        Property name
   * @return Enum value
   */
  public <E extends Enum<E>> E getEnumValue(final String propertyName,
                                            final E defaultValue)
  {
    requireNonNull(defaultValue, "No default value provided");
    final String value = getStringValue(propertyName, defaultValue.name());
    return enumValue(value, defaultValue);
  }

  public InclusionRule getExclusionRule(final String optionName)
  {
    final String value = getStringValue(optionName, null);
    final InclusionRule schemaInclusionRule;
    if (!isBlank(value))
    {
      schemaInclusionRule = new RegularExpressionExclusionRule(value);
    }
    else
    {
      schemaInclusionRule = new IncludeAll();
    }
    return schemaInclusionRule;
  }

  public InclusionRule getInclusionRule(final String optionName)
  {
    final String value = getStringValue(optionName, null);
    final InclusionRule schemaInclusionRule;
    if (!isBlank(value))
    {
      schemaInclusionRule = new RegularExpressionInclusionRule(value);
    }
    else
    {
      schemaInclusionRule = new ExcludeAll();
    }
    return schemaInclusionRule;
  }

  public InclusionRule getInclusionRule(final String includePatternProperty,
                                        final String excludePatternProperty)
  {
    final InclusionRule inclusionRule = getInclusionRuleOrNull(includePatternProperty,
                                                               excludePatternProperty);
    if (inclusionRule == null)
    {
      return new IncludeAll();
    }
    else
    {
      return inclusionRule;
    }
  }

  public InclusionRule getInclusionRuleDefaultExclude(final String includePatternProperty,
                                                      final String excludePatternProperty)
  {
    return new RegularExpressionRule(getStringValue(includePatternProperty, ""),
                                     getStringValue(excludePatternProperty,
                                                    ".*"));
  }

  public InclusionRule getInclusionRuleOrNull(final String includePatternProperty,
                                              final String excludePatternProperty)
  {
    final String includePattern = getStringValue(includePatternProperty, null);
    final String excludePattern = getStringValue(excludePatternProperty, null);
    if (isBlank(includePattern) && isBlank(excludePattern))
    {
      return null;
    }
    else
    {
      return new RegularExpressionRule(includePattern, excludePattern);
    }
  }

  /**
   * Gets the value of a property as an integer.
   *
   * @param propertyName
   *        Property name
   * @return Integer value
   */
  public int getIntegerValue(final String propertyName, final int defaultValue)
  {
    return Integer
      .parseInt(getStringValue(propertyName, String.valueOf(defaultValue)));
  }

  /**
   * Gets the value of a property as a string.
   *
   * @param propertyName
   *        Property name
   * @param defaultValue
   *        Default value
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
   *        Property name
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
    config.putAll(m);
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

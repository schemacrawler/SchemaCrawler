/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.schemacrawler;


import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.newBufferedReader;
import static sf.util.Utility.isBlank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.ObjectToString;

/**
 * Configuration properties.
 *
 * @author Sualeh Fatehi
 */
public final class Config
  extends HashMap<String, String>
  implements Options
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
    if (propertiesFile == null || !exists(propertiesFile)
        || !isReadable(propertiesFile) || isDirectory(propertiesFile))
    {
      LOGGER.log(Level.WARNING,
                 "Cannot load properties from file, " + propertiesFile);
      return properties;
    }

    LOGGER.log(Level.INFO, "Loading properties from file, " + propertiesFile);
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

  /**
   * Creates an empty config.
   */
  public Config()
  {
  }

  /**
   * Copies config into a map.
   *
   * @param config
   *        Config to copy
   */
  public Config(final Map<String, String> config)
  {
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
    super(propertiesMap(properties));
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
    final String value = getStringValue(propertyName, defaultValue.name());
    E enumValue;
    if (value == null)
    {
      enumValue = defaultValue;
    }
    else
    {
      try
      {
        enumValue = (E) Enum.valueOf(defaultValue.getClass(), value);
      }
      catch (final Exception e)
      {
        enumValue = defaultValue;
      }
    }
    return enumValue;
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
    return super.containsKey(propertyName);
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
  public String toString()
  {
    return ObjectToString.toString(this);
  }

}

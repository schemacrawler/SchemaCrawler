/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.ObjectToString;
import sf.util.Utility;

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
   * Loads the SchemaCrawler configuration, from a properties file
   * stream.
   * 
   * @param configStream
   *        Configuration stream.
   * @return Configuration properties.
   */
  public static Config load(final InputStream configStream)
  {
    Properties configProperties = new Properties();
    if (configStream != null)
    {
      configProperties = loadProperties(configProperties, configStream);
    }
    return new Config(configProperties);
  }

  /**
   * Loads the SchemaCrawler configuration, and override configuration,
   * from properties files.
   * 
   * @param configFilenames
   *        Configuration file name.
   * @return Configuration properties.
   */
  public static Config load(final String... configFilenames)
  {
    Properties configProperties = new Properties();
    if (configFilenames != null)
    {
      for (final String configFilename: configFilenames)
      {
        if (!Utility.isBlank(configFilename))
        {
          configProperties = loadProperties(configProperties,
                                            new File(configFilename));
        }
      }
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
   */
  private static Properties loadProperties(final Properties properties,
                                           final File propertiesFile)
  {
    try
    {
      final InputStream propertiesStream = new BufferedInputStream(new FileInputStream(propertiesFile));
      loadProperties(properties, propertiesStream);
    }
    catch (final FileNotFoundException e)
    {
      LOGGER.log(Level.WARNING, "Cannot load properties from file, "
                                + propertiesFile.getAbsolutePath());
      LOGGER.log(Level.FINEST, e.getMessage(), e);
    }
    return properties;
  }

  /**
   * Loads a properties file.
   * 
   * @param properties
   *        Properties object.
   * @param propertiesStream
   *        Properties data stream.
   * @return Properties
   */
  private static Properties loadProperties(final Properties properties,
                                           final InputStream propertiesStream)
  {
    try
    {
      properties.load(propertiesStream);
      propertiesStream.close();
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, "Error loading properties", e);
    }
    finally
    {
      try
      {
        if (propertiesStream != null)
        {
          propertiesStream.close();
        }
      }
      catch (final IOException e)
      {
        LOGGER.log(Level.WARNING, "Error closing stream", e);
      }
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
    final Map<String, String> propertiesMap = new HashMap<String, String>();
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
   * Clones a config.
   * 
   * @param config
   *        Config to clone
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
    return Boolean.parseBoolean(getStringValue(propertyName, "false"));
  }

  /**
   * Gets the value of a property as an integer.
   * 
   * @param propertyName
   *        Property name
   * @return Integer value
   */
  public int getIntegerValue(final String propertyName)
  {
    return Integer.parseInt(getStringValue(propertyName, null));
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

  @Override
  public String toString()
  {
    return ObjectToString.toString(this);
  }

}

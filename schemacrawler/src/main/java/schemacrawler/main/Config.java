package schemacrawler.main;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.Utilities;

/**
 * Configuration properties.
 * 
 * @author Sualeh Fatehi
 */
public class Config
  extends HashMap<String, String>
{

  private static final long serialVersionUID = 8720699738076915453L;

  private static final Logger LOGGER = Logger.getLogger(Config.class.getName());

  /**
   * Loads the SchemaCrawler configuration, and override.
   * 
   * @param configfilename
   *        Configuration file name.
   * @param configoverridefilename
   *        Configuration override file name.
   * @return Configuration properties.
   */
  public static Config load(final String configfilename,
                            final String configoverridefilename)
  {
    Properties configProperties = new Properties();
    configProperties = loadProperties(configProperties, configfilename);
    configProperties = loadProperties(configProperties, configoverridefilename);
    return new Config(configProperties);
  }

  /**
   * Loads a properties file.
   * 
   * @param properties
   *        Properties object.
   * @param propertiesFileName
   *        Properties file name.
   * @return Properties
   */
  static Properties loadProperties(final Properties properties,
                                   final String propertiesFileName)
  {
    InputStream propertiesStream = null;
    try
    {
      final File propertiesFile = new File(propertiesFileName);
      if (propertiesFile.exists())
      {
        propertiesStream = new BufferedInputStream(new FileInputStream(propertiesFile));
        properties.load(propertiesStream);
        propertiesStream.close();
      }
      else
      {
        LOGGER.log(Level.CONFIG, "Cannot find properties file "
                                 + propertiesFileName);
      }
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, "Error loading properties file "
                                + propertiesFileName, e);
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
   * Creates an empty config.
   */
  public Config()
  {
    super();
  }

  /**
   * Clones a config.
   * 
   * @param config
   *        Config to clone
   */
  public Config(final Map<String, String> config)
  {
    super(config);
  }

  /**
   * Copies properties into a map.
   * 
   * @param properties
   *        Properties to copy
   */
  public Config(final Properties properties)
  {
    super(Utilities.propertiesMap(properties));
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
  public boolean getIntegerValue(final String propertyName)
  {
    return Boolean.parseBoolean(getStringValue(propertyName, "false"));
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
   * Returns the configuration into properties.
   * 
   * @return Properties
   */
  public Properties toProperties()
  {
    final Properties properties = new Properties();
    for (final Entry<String, String> entry: entrySet())
    {
      properties.setProperty(entry.getKey(), entry.getValue());
    }
    return properties;
  }

}

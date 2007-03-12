/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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
package sf.util;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Utility methods.
 * 
 * @author Sualeh Fatehi
 */
public final class Utilities
{

  /**
   * System specific file separator character.
   */
  public static final char FILE_SEPARATOR = System
    .getProperty("file.separator").charAt(0);

  /**
   * System specific line separator character.
   */
  public static final String NEWLINE = System.getProperty("line.separator");
  private static final Logger LOGGER = Logger.getLogger(Utilities.class
    .getName());

  /**
   * Checks the Java version, and throw an exception if it does not
   * match the version provided as an argument.
   * 
   * @param minVersion
   *        Minimum version supported
   */
  public static void checkJavaVersion(final double minVersion)
  {
    final String jvmVersion = System.getProperty("java.specification.version");
    if (jvmVersion == null
        || Double.parseDouble(jvmVersion.substring(0, 3)) < minVersion)
    {
      throw new IllegalArgumentException("Needs Java " + minVersion
                                         + " or greater");
    }
  }

  /**
   * Interpolate substrings into system property values. Substrings of
   * the form ${<i>propname</i>} are interpolated into the text of the
   * system property whose key matches <i>propname</i>. For example,
   * expandProperties("hello.${user.name}.world") is "hello.foo.world"
   * when called by a user named "foo". Property substrings can be
   * nested. References to nonexistent system properties are
   * interpolated to an empty string.
   * 
   * @param template
   *        Template
   * @return Expanded template
   */
  public static String expandTemplateFromProperties(final String template)
  {
    return expandTemplateFromProperties(template, System.getProperties());
  }

  /**
   * Interpolate substrings into property values. Substrings of the form ${<i>propname</i>}
   * are interpolated into the text of the system property whose key
   * matches <i>propname</i>. For example,
   * expandProperties("hello.${user.name}.world") is "hello.foo.world"
   * when called by a user named "foo". Property substrings can be
   * nested. References to nonexistent system properties are
   * interpolated to an empty string.
   * 
   * @param template
   *        Template
   * @param properties
   *        Properties to substitute in the template
   * @return Expanded template
   */
  public static String expandTemplateFromProperties(final String template,
                                                    final Properties properties)
  {

    if (template == null)
    {
      return null;
    }

    String expandedTemplate = template;
    for (int left; (left = expandedTemplate.indexOf("${")) >= 0;)
    {
      final int inner = expandedTemplate.indexOf("${", left + 2);
      final int right = expandedTemplate.indexOf("}", left + 2);
      if (inner >= 0 && inner < right)
      {
        // Evaluate nested property value
        expandedTemplate = expandedTemplate.substring(0, inner)
                           + expandTemplateFromProperties(expandedTemplate
                             .substring(inner));
      }
      else if (right >= 0)
      {
        // Evaluate this property value
        final String propertyKey = expandedTemplate.substring(left + 2, right);
        Object propertyValue = properties.get(propertyKey);
        if (propertyValue == null)
        {
          propertyValue = "";
        }
        expandedTemplate = expandedTemplate.substring(0, left) + propertyValue
                           + expandedTemplate.substring(right + 1);
      }
      else
      {
        // Unmatched left delimiter - ignore
        break;
      }
    }

    return expandedTemplate;

  }

  /**
   * Gets a list of template variables.
   * 
   * @param template
   *        Template to extract variables from
   * @return Set of variables
   */
  public static Set<String> extractTemplateVariables(final String template)
  {

    if (template == null)
    {
      return new HashSet<String>();
    }

    String shrunkTemplate = template;
    final Set<String> keys = new HashSet<String>();
    for (int left; (left = shrunkTemplate.indexOf("${")) >= 0;)
    {
      final int right = shrunkTemplate.indexOf("}", left + 2);
      if (right >= 0)
      {
        final String propertyKey = shrunkTemplate.substring(left + 2, right);
        keys.add(propertyKey);
        // Destroy key, so we can find teh next one
        shrunkTemplate = shrunkTemplate.substring(0, left) + ""
                         + shrunkTemplate.substring(right + 1);
      }
    }

    return keys;
  }

  /**
   * Checks if the text is null or empty.
   * 
   * @param text
   *        Text to check.
   * @return Whether the string is blank.
   */
  public static boolean isBlank(final String text)
  {
    return text == null || text.trim().length() == 0;
  }

  /**
   * Returns true if the number is an integer within a certain
   * tolerance.
   * 
   * @param number
   *        Number to check
   * @return Whether the double is an integer
   */
  public static boolean isIntegral(final double number)
  {
    return Math.abs(number - (int) number) < 1E-10D;
  }

  /**
   * Returns true if the current operating system is Windows.
   * 
   * @return True is the current operating system is Windows.
   */
  public static boolean isWindowsOS()
  {
    final String osName = System.getProperty("os.name");
    final boolean isWindowsOS = osName == null
                                || osName.toLowerCase().indexOf("windows") != -1;
    return isWindowsOS;
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
  public static Properties loadProperties(final Properties properties,
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
   * Right justifies the string in given field length.
   * 
   * @param string
   *        String to right justify
   * @param len
   *        Length of the field
   * @return Justified string
   */
  public static String padLeft(final String string, final int len)
  {
    final StringBuffer buffer = new StringBuffer();
    if (string != null)
    {
      buffer.append(string);
    }
    while (buffer.length() < len)
    {
      buffer.insert(0, ' ');
    }
    return buffer.toString();
  }

  /**
   * Left justifies the string in given field length.
   * 
   * @param string
   *        String to right justify
   * @param len
   *        Length of the field
   * @return Justified string
   */
  public static String padRight(final String string, final int len)
  {
    final StringBuffer buffer = new StringBuffer();
    if (string != null)
    {
      buffer.append(string);
    }
    while (buffer.length() < len)
    {
      buffer.append(' ');
    }
    return buffer.toString();
  }

  /**
   * Checks if the text is true.
   * 
   * @param text
   *        Text to check.
   * @return Whether the string is true or yes.
   */
  public static boolean parseBoolean(final String text)
  {
    return !isBlank(text) && text.equalsIgnoreCase("YES")
           || Boolean.valueOf(text).booleanValue();
  }

  /**
   * Reads the stream fully, and returns a byte array of data.
   * 
   * @param stream
   *        Stream to read.
   * @return Byte array
   */
  public static byte[] readFully(final InputStream stream)
  {
    final int bufferSize = 2048;
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final BufferedInputStream input = new BufferedInputStream(stream);
    byte[] byteCode = new byte[0];

    try
    {
      int length;
      final byte[] copyBuffer = new byte[bufferSize];

      while (-1 != (length = input.read(copyBuffer)))
      {
        output.write(copyBuffer, 0, length);
      }
      output.flush();
      byteCode = output.toByteArray();
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, "Error reading input stream", e);
    }
    finally
    {
      try
      {
        output.close();
        input.close();
      }
      catch (final IOException e)
      {
        LOGGER.log(Level.WARNING, "Error closing stream", e);
      }
    }

    return byteCode;
  }

  /**
   * Repeats a string.
   * 
   * @param string
   *        String to repeat
   * @param count
   *        Number of times to repeat
   * @return String with repetitions
   */
  public static String repeat(final String string, final int count)
  {

    String repeated = "";

    if (string != null && count >= 1)
    {
      final StringBuffer stringbuffer = new StringBuffer(string.length()
                                                         * count);
      for (int i = 0; i < count; i++)
      {
        stringbuffer.append(string);
      }
      repeated = stringbuffer.toString();
    }

    return repeated;

  }

  /**
   * Sets the application-wide log level.
   * 
   * @param logLevel
   *        Log level to set
   */
  public static void setApplicationLogLevel(final Level logLevel)
  {
    final LogManager logManager = LogManager.getLogManager();
    for (final Enumeration<String> loggerNames = logManager.getLoggerNames(); loggerNames
      .hasMoreElements();)
    {
      final String loggerName = loggerNames.nextElement();
      final Logger logger = logManager.getLogger(loggerName);
      logger.setLevel(null);
    }

    final Logger rootLogger = Logger.getLogger("");
    rootLogger.setLevel(logLevel);

    final Handler[] handlers = rootLogger.getHandlers();
    for (final Handler element: handlers)
    {
      element.setLevel(logLevel);
    }

  }

  /**
   * Writes a string to a file.
   * 
   * @param fileName
   *        Name of the file to write.
   * @param fileContents
   *        Contents of the file.
   * @return The file.
   * @throws IOException
   *         On an exception.
   */
  public static File writeStringToFile(final String fileName,
                                       final String fileContents)
    throws IOException
  {

    final File pomFile = new File(fileName);
    final FileWriter writer = new FileWriter(pomFile);
    writer.write(fileContents);
    writer.flush();
    writer.close();

    return pomFile;
  }

  /**
   * Copies properties into a map.
   * 
   * @param properties
   *        Properties to copy
   * @return Map
   */
  public static Map<String, String> propertiesMap(Properties properties)
  {
    Map<String, String> propertiesMap = new HashMap<String, String>();
    if (properties != null)
    {
      Set<Entry<Object, Object>> entries = properties.entrySet();
      for (Entry<Object, Object> entry: entries)
      {
        propertiesMap.put((String) entry.getKey(), (String) entry.getValue());
      }
    }
    return propertiesMap;
  }

  /**
   * Confound instantiation.
   */
  private Utilities()
  {
    // intentionally left blank
  }

}

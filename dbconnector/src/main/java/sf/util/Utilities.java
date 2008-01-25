/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
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
   * Copies properties into a map.
   * 
   * @param properties
   *        Properties to copy
   * @return Map
   */
  public static Map<String, String> propertiesMap(final Properties properties)
  {
    final Map<String, String> propertiesMap = new HashMap<String, String>();
    if (properties != null)
    {
      final Set<Entry<Object, Object>> entries = properties.entrySet();
      for (final Entry<Object, Object> entry: entries)
      {
        propertiesMap.put((String) entry.getKey(), (String) entry.getValue());
      }
    }
    return propertiesMap;
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
   * Confound instantiation.
   */
  private Utilities()
  {
    // intentionally left blank
  }

}

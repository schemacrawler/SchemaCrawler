/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

  private static final Logger LOGGER = Logger.getLogger(Utilities.class
    .getName());

  /**
   * System specific file separator character.
   */
  public static final char FILE_SEPARATOR = System
    .getProperty("file.separator").charAt(0);
  /**
   * System specific line separator character.
   */
  public static final String NEWLINE = System.getProperty("line.separator");

  /**
   * Confound instantiation.
   */
  private Utilities()
  {
    // intentionally left blank
  }

  /**
   * Left justifies the string in given field length.
   * 
   * @param string
   *          String to right justify
   * @param len
   *          Length of the field
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
   * Right justifies the string in given field length.
   * 
   * @param string
   *          String to right justify
   * @param len
   *          Length of the field
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
   * Repeats a string.
   * 
   * @param string
   *          String to repeat
   * @param count
   *          Number of times to repeat
   * @return String with repetitions
   */
  public static String repeat(final String string, final int count)
  {

    String repeated = "";

    if (count >= 1)
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
   * Reads the stream fully, and returns a byte array of data.
   * 
   * @param stream
   *          Stream to read.
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
        stream.close();
      }
      catch (final IOException e)
      {
        LOGGER.log(Level.WARNING, "Error closing stream", e);
      }
    }

    return byteCode;
  }

  /**
   * Checks the Java version, and throw an exception if it does not
   * match the version provided as an argument.
   * 
   * @param minVersion
   *          Minimum version supported
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
   * Interpolate substrings into property values. Substrings of the form ${<i>propname</i>}
   * are interpolated into the text of the system property whose key
   * matches <i>propname</i>. For example,
   * expandProperties("hello.${user.name}.world") is "hello.foo.world"
   * when called by a user named "foo". Property substrings can be
   * nested. References to nonexistent system properties are
   * interpolated to an empty string.
   * 
   * @param template
   *          Template
   * @param properties
   *          Properties to substitute in the template
   * @return Expanded template
   */
  public static String expandTemplateFromProperties(final String template,
                                                    final Map properties)
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
   * Interpolate substrings into system property values. Substrings of
   * the form ${<i>propname</i>} are interpolated into the text of the
   * system property whose key matches <i>propname</i>. For example,
   * expandProperties("hello.${user.name}.world") is "hello.foo.world"
   * when called by a user named "foo". Property substrings can be
   * nested. References to nonexistent system properties are
   * interpolated to an empty string.
   * 
   * @param template
   *          Template
   * @return Expanded template
   */
  public static String expandTemplateFromProperties(final String template)
  {
    return expandTemplateFromProperties(template, System.getProperties());
  }

  /**
   * Gets a list of template variables.
   * 
   * @param template
   *          Template to extract variables from
   * @return Set of variables
   */
  public static Set extractTemplateVariables(final String template)
  {

    if (template == null)
    {
      return new HashSet();
    }

    String shrunkTemplate = template;
    final Set keys = new HashSet();
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
   * Sets the application-wide log level.
   * 
   * @param logLevel
   *          Log level to set
   */
  public static void setApplicationLogLevel(final Level logLevel)
  {
    final LogManager logManager = LogManager.getLogManager();
    for (final Enumeration loggerNames = logManager.getLoggerNames(); loggerNames
      .hasMoreElements();)
    {
      final String loggerName = (String) loggerNames.nextElement();
      final Logger logger = logManager.getLogger(loggerName);
      logger.setLevel(null);
    }

    final Logger rootLogger = Logger.getLogger("");
    rootLogger.setLevel(logLevel);

    final Handler[] handlers = rootLogger.getHandlers();
    for (int index = 0; index < handlers.length; index++)
    {
      handlers[index].setLevel(logLevel);
    }

  }

  /**
   * Returns true if the number is an integer within a certain
   * tolerance.
   * 
   * @param number
   *          Number to check
   * @return Whether the double is an integer
   */
  public static boolean isIntegral(final double number)
  {
    return Math.abs(number - (int) number) < 1E-10D;
  }

  /**
   * Writes a string to a file.
   * 
   * @param fileName
   *          Name of the file to write.
   * @param fileContents
   *          Contents of the file.
   * @return The file.
   * @throws IOException
   *           On an exception.
   */
  public static File writeStringToFile(String fileName, String fileContents)
    throws IOException
  {

    File pomFile = new File(fileName);
    FileWriter writer = new FileWriter(pomFile);
    writer.write(fileContents);
    writer.flush();
    writer.close();

    return pomFile;
  }

  /**
   * Returns true if the current operating system is Windows.
   * 
   * @return True is the current operating system is Windows.
   */
  public static boolean isWindowsOS()
  {
    String osName = System.getProperty("os.name");
    boolean isWindowsOS = osName == null
                          || osName.toLowerCase().indexOf("windows") != -1;
    return isWindowsOS;
  }

  /**
   * Checks if the text is null or empty.
   * 
   * @param text
   * @return
   */
  public static boolean isBlank(String text)
  {
    return text == null || text.trim().length() == 0;
  }
  
}

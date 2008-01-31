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
import java.util.Map;
import java.util.Properties;
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

  /**
   * Returns the configuration into properties.
   * 
   * @return Properties
   */
  public static Properties toProperties(Map<String, String> map)
  {
    final Properties properties = new Properties();
    for (final Entry<String, String> entry: map.entrySet())
    {
      properties.setProperty(entry.getKey(), entry.getValue());
    }
    return properties;
  }

}

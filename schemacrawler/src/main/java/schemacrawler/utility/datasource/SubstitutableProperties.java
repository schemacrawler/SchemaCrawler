/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.utility.datasource;


import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Properties map that looks like an ordinary java.util.Properties, but
 * substitutes values such as ${propertykey}, where 'propertykey' is
 * another property. Property subsitution is not done on the value of
 * 'propertykey' itself.
 */
final class SubstitutableProperties
  extends Properties
{

  private static final long serialVersionUID = 3258417209599932210L;

  private static final String DELIMITER_START = "${";
  private static final String DELIMITER_END = "}";

  /**
   * Creates a new Properties object with property substitution enabled.
   * 
   * @param properties
   *        The source Properties object
   */
  SubstitutableProperties(final Properties properties)
  {
    // Copy properties over one by one, so that there are no default
    // properties
    if (properties != null)
    {
      final Set<Entry<Object, Object>> entries = properties.entrySet();
      for (final Entry<Object, Object> entry: entries)
      {
        setProperty((String) entry.getKey(), (String) entry.getValue());
      }
    }
  }

  /**
   * Gets a property with the value substituted.
   * 
   * @param key
   *        The property key
   * @return Substituted property
   */
  @Override
  public String getProperty(final String key)
  {

    String value = super.getProperty(key);

    try
    {
      if (value != null)
      {
        return substituteVariables(value);
      }
    }
    catch (final IllegalArgumentException e)
    {
      // log.error( "Bad option value [" + value + "]", e );
      value = null;
    }

    return value;

  }

  private String substituteVariables(final String template)
  {

    final StringBuffer buffer = new StringBuffer();
    int currentPosition = 0;
    int delimiterStartPosition;
    int delimiterEndPosition;

    while (true)
    {
      delimiterStartPosition = template.indexOf(DELIMITER_START,
                                                currentPosition);
      if (delimiterStartPosition == -1)
      {
        if (currentPosition == 0)
        {
          // No substitutions required at all
          return template;
        }
        // No more substitutions
        buffer.append(template.substring(currentPosition, template.length()));
        return buffer.toString();
      }
      else
      {
        buffer.append(template.substring(currentPosition,
                                         delimiterStartPosition));
        delimiterEndPosition = template.indexOf(DELIMITER_END,
                                                delimiterStartPosition);
        if (delimiterEndPosition == -1)
        {
          throw new IllegalArgumentException(template
                                             + " does not have a closing brace");
        }
        delimiterStartPosition = delimiterStartPosition
                                 + DELIMITER_START.length();
        final String key = template.substring(delimiterStartPosition,
                                              delimiterEndPosition);
        final String value = super.getProperty(key);
        if (value != null)
        {
          buffer.append(value);
        }
        // Advance current position
        currentPosition = delimiterEndPosition + DELIMITER_END.length();
      }
    }
  }

}

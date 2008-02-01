/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

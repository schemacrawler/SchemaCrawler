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
package schemacrawler.crawl;


import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import schemacrawler.schema.DatabaseProperty;

class MutableDatabaseProperty
  extends MutableProperty
  implements DatabaseProperty
{

  private static final long serialVersionUID = -7150431683440256142L;

  private static final Set<Entry<String, String>> acronyms;

  static
  {
    final Map<String, String> acronymsMap = new HashMap<String, String>();
    acronymsMap.put("JDBC", "Jdbc");
    acronymsMap.put("ANSI", "Ansi");
    acronymsMap.put("SQL", "Sql");
    acronymsMap.put("URL", "Url");
    acronymsMap.put("TYPE_FORWARD_ONLY", "Type_forward_only");
    acronymsMap.put("TYPE_SCROLL_INSENSITIVE", "Type_scroll_insensitive");
    acronymsMap.put("TYPE_SCROLL_SENSITIVE", "Type_scroll_sensitive");
    acronyms = Collections.unmodifiableSet(acronymsMap.entrySet());
  }

  private transient String description;

  MutableDatabaseProperty(final String name, final Object value)
  {
    super(name, (Serializable) value);
    buildDescription();
  }

  public int compareTo(final DatabaseProperty otherDbProperty)
  {
    if (otherDbProperty == null)
    {
      return -1;
    }
    else
    {
      return getDescription().toLowerCase().compareTo(otherDbProperty
        .getDescription().toLowerCase());
    }
  }

  /**
   * {@inheritDoc}
   */
  public String getDescription()
  {
    buildDescription();
    return description;
  }

  @Override
  public String toString()
  {
    return getDescription() + " = " + getValue();
  }

  private void buildDescription()
  {
    if (description == null)
    {
      final String get = "get";
      description = getName();
      if (description.startsWith(get))
      {
        description = description.substring(get.length());
      }

      for (final Entry<String, String> acronym: acronyms)
      {
        description = description.replaceAll(acronym.getKey(), acronym
          .getValue());
      }

      final int strLen = description.length();
      final StringBuilder buffer = new StringBuilder(strLen);
      for (int i = 0; i < strLen; i++)
      {
        final char ch = description.charAt(i);
        if (Character.isUpperCase(ch) || Character.isTitleCase(ch))
        {
          buffer.append(' ').append(Character.toLowerCase(ch));
        }
        else
        {
          buffer.append(ch);
        }
      }
      description = buffer.toString();

      for (final Entry<String, String> acronym: acronyms)
      {
        description = description.replaceAll(acronym.getValue().toLowerCase(),
                                             acronym.getKey());
        description = description.replaceAll(acronym.getValue(), acronym
          .getKey());
      }

      description = description.trim();
    }
  }

}

/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.crawl;


import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import schemacrawler.schema.DatabaseProperty;
import schemacrawler.schema.Property;

class ImmutableDatabaseProperty
  extends AbstractProperty
  implements DatabaseProperty
{

  private static final long serialVersionUID = -7150431683440256142L;

  private static final Set<Entry<String, String>> acronyms;

  static
  {
    final Map<String, String> acronymsMap = new HashMap<>();
    acronymsMap.put("JDBC", "Jdbc");
    acronymsMap.put("ANSI", "Ansi");
    acronymsMap.put("SQL", "Sql");
    acronymsMap.put("URL", "Url");
    acronymsMap.put("TYPE_FORWARD_ONLY", "Type_forward_only");
    acronymsMap.put("TYPE_SCROLL_INSENSITIVE", "Type_scroll_insensitive");
    acronymsMap.put("TYPE_SCROLL_SENSITIVE", "Type_scroll_sensitive");
    acronyms = Collections.unmodifiableSet(acronymsMap.entrySet());
  }

  ImmutableDatabaseProperty(final String name, final Object value)
  {
    super(name, (Serializable) value);
  }

  @Override
  public int compareTo(final Property otherDbProperty)
  {
    if (otherDbProperty == null)
    {
      return -1;
    }
    else
    {
      return getDescription().toLowerCase()
        .compareTo(otherDbProperty.getDescription().toLowerCase());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription()
  {
    final String get = "get";
    String description = getName();
    if (description.startsWith(get))
    {
      description = description.substring(get.length());
    }

    for (final Entry<String, String> acronym: acronyms)
    {
      description = description.replaceAll(acronym.getKey(),
                                           acronym.getValue());
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
      description = description.replaceAll(acronym.getValue(),
                                           acronym.getKey());
    }

    description = description.trim();
    return description;
  }

  @Override
  public String toString()
  {
    return getDescription() + " = " + getValue();
  }

}

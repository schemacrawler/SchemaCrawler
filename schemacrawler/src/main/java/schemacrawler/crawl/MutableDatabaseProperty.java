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


import schemacrawler.schema.DatabaseProperty;
import sf.util.Utility;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class MutableDatabaseProperty
  implements DatabaseProperty {

  private static final long serialVersionUID = -7150431683440256142L;

  private static final Set<Entry<String, String>> acronyms;

  static {
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

  /**
   * Derives the property name from the method name.
   *
   * @param method Method
   *
   * @return Method name
   */
  private static String createDescription(final String name) {

    final String get = "get";
    String description = name;
    if (description.startsWith(get)) {
      description = description.substring(get.length());
    }

    for (final Entry<String, String> acronym : acronyms) {
      description = description
        .replaceAll(acronym.getKey(), acronym.getValue());
    }

    final int strLen = description.length();
    final StringBuilder buffer = new StringBuilder(strLen);
    for (int i = 0; i < strLen; i++) {
      final char ch = description.charAt(i);
      if (Character.isUpperCase(ch) || Character.isTitleCase(ch)) {
        buffer.append(' ')
          .append(Character.toLowerCase(ch));
      }
      else {
        buffer.append(ch);
      }
    }
    description = buffer.toString();

    for (final Entry<String, String> acronym : acronyms) {
      description = description.replaceAll(acronym.getValue().toLowerCase(),
                                           acronym.getKey());
      description = description
        .replaceAll(acronym.getValue(), acronym.getKey());
    }

    return description.trim();
  }

  private final String name;
  private final String description;
  private final Object value;

  MutableDatabaseProperty(final String name, final Object value) {
    if (Utility.isBlank(name)) {
      throw new IllegalArgumentException("No description provided");
    }
    this.name = name.trim();
    description = createDescription(name);
    this.value = value;
  }

  public int compareTo(final DatabaseProperty otherDbProperty) {
    if (otherDbProperty == null) {
      return -1;
    }
    else {
      return getDescription().toLowerCase()
        .compareTo(otherDbProperty
          .getDescription().toLowerCase());
    }
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof MutableDatabaseProperty)) {
      return false;
    }
    final MutableDatabaseProperty other = (MutableDatabaseProperty) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    }
    else if (!name.equals(other.name)) {
      return false;
    }
    if (value == null) {
      if (other.value != null) {
        return false;
      }
    }
    else if (!value.equals(other.value)) {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.crawl.DatabaseProperty#getDescription()
   */
  public String getDescription() {
    return description;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.crawl.DatabaseProperty#getName()
   */
  public String getName() {
    return name;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.crawl.DatabaseProperty#getValue()
   */
  public Object getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (name == null ? 0 : name.hashCode());
    result = prime * result + (value == null ? 0 : value.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return name + "=" + value;
  }

}

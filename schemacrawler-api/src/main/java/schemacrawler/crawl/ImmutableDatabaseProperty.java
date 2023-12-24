/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static java.util.Objects.compare;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import schemacrawler.schema.DatabaseProperty;
import schemacrawler.schema.Property;

class ImmutableDatabaseProperty extends AbstractProperty implements DatabaseProperty {

  private static final long serialVersionUID = -7150431683440256142L;

  private static final Comparator<Property> comparator =
      nullsLast(comparing(Property::getDescription, CASE_INSENSITIVE_ORDER));
  private static final Set<Entry<Pattern, String>> acronyms;

  static {
    final Map<Pattern, String> acronymsMap = new HashMap<>();
    final List<String> acronymsList =
        asList(
            "JDBC",
            "ANSI",
            "SQL",
            "URL",
            "TYPE_FORWARD_ONLY",
            "TYPE_SCROLL_INSENSITIVE",
            "TYPE_SCROLL_SENSITIVE");
    for (final String acronym : acronymsList) {
      final Pattern pattern = Pattern.compile(acronym, CASE_INSENSITIVE);
      acronymsMap.put(pattern, acronym);
    }
    acronyms = unmodifiableSet(acronymsMap.entrySet());
  }

  private transient String description;

  ImmutableDatabaseProperty(final String name, final Object value) {
    super(name, (Serializable) value);
  }

  @Override
  public int compareTo(final Property otherProperty) {
    return compare(this, otherProperty, comparator);
  }

  /** {@inheritDoc} */
  @Override
  public String getDescription() {
    buildDescription();
    return description;
  }

  @Override
  public String toString() {
    return getDescription() + " = " + getValue();
  }

  /**
   * This function splits DatabaseMetaData method names into words. It is not intended to be a
   * general purpose word splitting algorithm.
   */
  private void buildDescription() {
    if (description != null) {
      return;
    }

    // Remove leading "get"
    final String get = "get";
    description = getName();
    if (description.startsWith(get)) {
      description = description.substring(get.length());
    }

    // Prevent word-splitting of acronyms
    for (final Entry<Pattern, String> acronym : acronyms) {
      final String lowerCaseAcronymAsWord = " " + acronym.getValue().toLowerCase();
      description = acronym.getKey().matcher(description).replaceAll(lowerCaseAcronymAsWord);
    }

    // Split words by title case
    final int strLen = description.length();
    final StringBuilder buffer = new StringBuilder(strLen);
    for (int i = 0; i < strLen; i++) {
      final char ch = description.charAt(i);
      if (Character.isUpperCase(ch)) {
        buffer.append(' ').append(Character.toLowerCase(ch));
      } else {
        buffer.append(ch);
      }
    }
    description = buffer.toString();

    // Replace acronyms back to their upper-case forms
    for (final Entry<Pattern, String> acronym : acronyms) {
      description = acronym.getKey().matcher(description).replaceAll(acronym.getValue());
    }

    description = description.trim();
  }
}

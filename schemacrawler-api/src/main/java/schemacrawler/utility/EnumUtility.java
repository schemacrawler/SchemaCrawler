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

package schemacrawler.utility;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.util.EnumSet;
import java.util.Set;

import schemacrawler.schema.IdentifiedEnum;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class EnumUtility {

  public static <E extends Enum<E>> E enumValue(final String value, final E defaultValue) {
    requireNonNull(defaultValue, "No default value provided");
    E enumValue;
    if (isBlank(value)) {
      enumValue = defaultValue;
    } else {
      try {
        Class<?> enumClass = defaultValue.getClass();
        if (enumClass.getEnclosingClass() != null) {
          enumClass = enumClass.getEnclosingClass();
        }
        enumValue = Enum.valueOf((Class<E>) enumClass, value.trim());
      } catch (final Exception e) {
        enumValue = defaultValue;
      }
    }
    return enumValue;
  }

  public static <E extends Enum<E> & IdentifiedEnum> E enumValueFromId(
      final int value, final E defaultValue) {
    requireNonNull(defaultValue, "No default value provided");
    try {
      final Class<E> enumClass = (Class<E>) defaultValue.getClass();
      for (final E enumValue : EnumSet.allOf(enumClass)) {
        if (enumValue.id() == value) {
          return enumValue;
        }
      }
    } catch (final Exception e) {
      // Ignore
    }
    return defaultValue;
  }

  public static <E extends Enum<E>> Set<E> enumValues(
      final String values, final String splitBy, final E defaultValue) {
    requireNonNull(defaultValue, "No default value provided");

    final EnumSet<E> enumValues = EnumSet.of(defaultValue);

    if (isBlank(values)) {
      return enumValues;
    }

    // Split into multiple event manipulation types
    final String[] valueStrings;
    if (isBlank(splitBy)) {
      valueStrings = new String[] {values};
    } else {
      valueStrings = values.split(splitBy);
    }

    for (String valueString : valueStrings) {
      final E enumValue = enumValue(valueString, defaultValue);
      enumValues.add(enumValue);
    }
    if (enumValues.size() > 1) {
      enumValues.remove(defaultValue);
    }

    return enumValues;
  }

  private EnumUtility() {
    // Prevent instantiation
  }
}

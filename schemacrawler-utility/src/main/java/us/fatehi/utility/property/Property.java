/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.property;

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static java.util.Objects.compare;
import static us.fatehi.utility.Utility.isBlank;

import java.io.Serializable;
import java.util.Comparator;

public interface Property extends Serializable, Comparable<Property> {

  Comparator<Property> comparator =
      nullsLast(comparing(Property::getName, String.CASE_INSENSITIVE_ORDER));

  @Override
  default int compareTo(final Property otherProperty) {
    return compare(this, otherProperty, comparator);
  }

  /**
   * Gets the description of the property.
   *
   * @return Description
   */
  String getDescription();

  /**
   * Gets the name of the property.
   *
   * @return Name
   */
  String getName();

  /**
   * Gets the value of the property.
   *
   * @return Value
   */
  Object getValue();

  default boolean hasDescription() {
    return !isBlank(getDescription());
  }

  default boolean hasValue() {
    return getValue() != null;
  }
}

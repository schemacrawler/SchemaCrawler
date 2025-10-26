/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.property;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

public record PropertyName(String name, String description)
    implements Serializable, Comparable<PropertyName> {

  @Serial private static final long serialVersionUID = 2444083929278551904L;

  private static final Comparator<PropertyName> comparator =
      Comparator.nullsLast(Comparator.comparing(PropertyName::name, String.CASE_INSENSITIVE_ORDER));

  public PropertyName {
    name = requireNotBlank(name, "Property name not provided").trim();
    description = isBlank(description) ? "" : description.trim();
  }

  public PropertyName(final String name) {
    this(name, null);
  }

  @Override
  public int compareTo(final PropertyName other) {
    return compare(this, other, comparator);
  }

  public String getName() {
    return name();
  }

  public String getDescription() {
    return description();
  }

  @Override
  public String toString() {
    return description.isBlank() ? name : name + " - " + description;
  }

  // Utility methods assumed to exist
  private static String requireNotBlank(final String value, final String message) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }

  private static boolean isBlank(final String value) {
    return value == null || value.isBlank();
  }

  private static int compare(
      final PropertyName a, final PropertyName b, final Comparator<PropertyName> comparator) {
    return comparator.compare(a, b);
  }
}

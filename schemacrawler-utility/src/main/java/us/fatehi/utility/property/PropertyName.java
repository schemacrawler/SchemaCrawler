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
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import static java.util.Objects.compare;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

public final class PropertyName implements Serializable, Comparable<PropertyName> {

  private static final long serialVersionUID = 2444083929278551904L;

  private static Comparator<PropertyName> comparator =
      nullsLast(comparing(PropertyName::getName, String.CASE_INSENSITIVE_ORDER));

  private final String name;
  private final String description;

  public PropertyName(final String name) {
    this(name, null);
  }

  public PropertyName(final String name, final String description) {
    this.name = requireNotBlank(name, "Property name not provided").trim();

    if (isBlank(description)) {
      this.description = null;
    } else {
      this.description = description.trim();
    }
  }

  @Override
  public int compareTo(final PropertyName otherProperty) {
    return compare(this, otherProperty, comparator);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || !(obj instanceof PropertyName)) {
      return false;
    }
    final PropertyName other = (PropertyName) obj;
    if (!Objects.equals(name, other.name)) {
      return false;
    }
    return true;
  }

  public String getDescription() {
    return description == null ? "" : description;
  }

  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(name);
    if (description != null) {
      builder.append(" - ").append(description);
    }
    return builder.toString();
  }
}

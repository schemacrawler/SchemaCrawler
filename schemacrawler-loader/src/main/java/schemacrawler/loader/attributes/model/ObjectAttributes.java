/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.loader.attributes.model;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static java.util.Objects.compare;
import static us.fatehi.utility.Utility.isBlank;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public abstract class ObjectAttributes implements Serializable, Comparable<ObjectAttributes> {

  @Serial private static final long serialVersionUID = -6819484903391182146L;

  private static Comparator<ObjectAttributes> comparator =
      nullsLast(comparing(ObjectAttributes::getName, String.CASE_INSENSITIVE_ORDER));

  private final String name;
  private final List<String> remarks;
  private final Map<String, String> attributes;

  public ObjectAttributes(
      final String name, final List<String> remarks, final Map<String, String> attributes) {

    if (isBlank(name)) {
      throw new IllegalArgumentException("No name provided");
    }
    this.name = name;

    if (remarks == null) {
      this.remarks = emptyList();
    } else {
      this.remarks = List.copyOf(remarks);
    }
    if (attributes == null) {
      this.attributes = emptyMap();
    } else {
      this.attributes = Map.copyOf(attributes);
    }
  }

  @Override
  public int compareTo(final ObjectAttributes o) {
    return compare(this, o, comparator);
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public String getName() {
    return name;
  }

  public String getRemarks() {
    return String.join(System.lineSeparator(), remarks);
  }

  public boolean hasAttributes() {
    return !attributes.isEmpty();
  }

  public boolean hasRemarks() {
    if (remarks.isEmpty()) {
      return false;
    }
    return !isBlank(getRemarks());
  }

  @Override
  public String toString() {
    return "<%s>".formatted(getName());
  }
}

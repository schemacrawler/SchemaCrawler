/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schemacrawler;

import static java.util.Objects.requireNonNull;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.values;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import us.fatehi.utility.ObjectToString;

/** Descriptor for level of schema detail to be retrieved when crawling a schema. */
public final class SchemaInfoLevel implements Options {

  private final boolean[] schemaInfoRetrievals;
  private final String tag;

  SchemaInfoLevel(
      final String tag, final Map<SchemaInfoRetrieval, Boolean> schemaInfoRetrievalsMap) {
    requireNonNull(tag, "No tag provided");
    this.tag = tag;

    requireNonNull(schemaInfoRetrievalsMap, "No schema info retrievals provided");
    final SchemaInfoRetrieval[] schemaInfoRetrievalsArray = values();
    schemaInfoRetrievals = new boolean[schemaInfoRetrievalsArray.length];
    for (final SchemaInfoRetrieval schemaInfoRetrieval : schemaInfoRetrievalsArray) {
      final boolean schemaInfoRetrievalValue =
          schemaInfoRetrievalsMap.getOrDefault(schemaInfoRetrieval, false);
      schemaInfoRetrievals[schemaInfoRetrieval.ordinal()] = schemaInfoRetrievalValue;
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
    if (!(obj instanceof SchemaInfoLevel)) {
      return false;
    }
    final SchemaInfoLevel other = (SchemaInfoLevel) obj;
    return Arrays.equals(schemaInfoRetrievals, other.schemaInfoRetrievals)
        && Objects.equals(tag, other.tag);
  }

  public String getTag() {
    return tag;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(schemaInfoRetrievals);
    result = prime * result + Objects.hash(tag);
    return result;
  }

  public boolean is(final SchemaInfoRetrieval schemaInfoRetrieval) {
    if (schemaInfoRetrieval == null) {
      return false;
    }
    return schemaInfoRetrievals[schemaInfoRetrieval.ordinal()];
  }

  @Override
  public String toString() {
    final Map<String, Boolean> values = new HashMap<>();
    for (final SchemaInfoRetrieval schemaInfoRetrieval : values()) {
      values.put(schemaInfoRetrieval.name(), is(schemaInfoRetrieval));
    }
    return ObjectToString.toString(values);
  }
}

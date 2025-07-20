/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

import static us.fatehi.utility.Utility.convertForComparison;

import java.io.Serializable;
import java.util.Arrays;

public final class NamedObjectKey implements Serializable {

  private static final long serialVersionUID = -5008609072012459037L;

  private final String[] key;

  public NamedObjectKey(final String... key) {
    if (key == null || key.length == 0) {
      this.key = new String[0];
    } else {
      this.key = Arrays.copyOf(key, key.length);
    }
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof NamedObjectKey)) {
      return false;
    }
    final NamedObjectKey other = (NamedObjectKey) obj;
    return Arrays.equals(key, other.key);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(key);
  }

  public String slug() {
    if (key.length == 0) {
      return "";
    }
    final String name = key[key.length - 1];
    return convertForComparison(name) + "_" + Integer.toHexString(hashCode());
  }

  @Override
  public String toString() {
    return "{\"key\": \"" + String.join("/", key) + "\"}";
  }

  public NamedObjectKey with(final String name) {
    final int currentLength = key.length;
    final String[] newKey = Arrays.copyOf(key, currentLength + 1);
    newKey[currentLength] = name;
    return new NamedObjectKey(newKey);
  }
}

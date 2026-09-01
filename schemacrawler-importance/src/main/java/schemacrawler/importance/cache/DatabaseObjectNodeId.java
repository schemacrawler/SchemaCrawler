/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.cache;

import java.util.Objects;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.utility.MetaDataUtility.SimpleDatabaseObjectType;

/** Identifies a schema graph vertex by its database key and object type. */
public record DatabaseObjectNodeId(NamedObjectKey key, SimpleDatabaseObjectType type)
    implements Comparable<DatabaseObjectNodeId> {

  public DatabaseObjectNodeId {
    Objects.requireNonNull(key, "No object key provided");
    Objects.requireNonNull(type, "No object type provided");
  }

  @Override
  public int compareTo(final DatabaseObjectNodeId other) {
    if (other == null) {
      return 1;
    }
    final int keyComparison = key.compareTo(other.key);
    return keyComparison != 0 ? keyComparison : type.compareTo(other.type);
  }
}

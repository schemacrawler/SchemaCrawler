/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.util;

import java.util.Objects;
import schemacrawler.importance.model.DatabaseObjectNodeId;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.utility.MetaDataUtility;
import us.fatehi.utility.UtilityMarker;

/** Creates stable graph identifiers for SchemaCrawler database objects. */
@UtilityMarker
public final class NodeIdFactory {

  public static DatabaseObjectNodeId create(final DatabaseObject databaseObject) {
    Objects.requireNonNull(databaseObject, "No database object provided");
    return new DatabaseObjectNodeId(
        databaseObject.key(), MetaDataUtility.getSimpleTypeName(databaseObject));
  }

  private NodeIdFactory() {
    // Prevent instantiation
  }
}

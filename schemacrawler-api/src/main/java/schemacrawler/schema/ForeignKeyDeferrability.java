/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

import static java.sql.DatabaseMetaData.importedKeyInitiallyDeferred;
import static java.sql.DatabaseMetaData.importedKeyInitiallyImmediate;
import static java.sql.DatabaseMetaData.importedKeyNotDeferrable;

import us.fatehi.utility.IdentifiedEnum;

/** An enumeration wrapper around the JDBC deferrability value for foreign keys. */
public enum ForeignKeyDeferrability implements IdentifiedEnum {
  unknown(-1, "unknown"),
  initiallyDeferred(importedKeyInitiallyDeferred, "initially deferred"),
  initiallyImmediate(importedKeyInitiallyImmediate, "initially immediate"),
  keyNotDeferrable(importedKeyNotDeferrable, "not deferrable");

  private final int id;
  private final String text;

  ForeignKeyDeferrability(final int id, final String text) {
    this.id = id;
    this.text = text;
  }

  /** {@inheritDoc} */
  @Override
  public int id() {
    return id;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return text;
  }
}

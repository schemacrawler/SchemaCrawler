/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

import static java.sql.DatabaseMetaData.importedKeyCascade;
import static java.sql.DatabaseMetaData.importedKeyNoAction;
import static java.sql.DatabaseMetaData.importedKeyRestrict;
import static java.sql.DatabaseMetaData.importedKeySetDefault;
import static java.sql.DatabaseMetaData.importedKeySetNull;

/** An enumeration wrapper around foreign key update and delete rules. */
public enum ForeignKeyUpdateRule implements IdentifiedEnum {
  unknown(-1, "unknown"),
  noAction(importedKeyNoAction, "no action"),
  cascade(importedKeyCascade, "cascade"),
  setNull(importedKeySetNull, "set null"),
  setDefault(importedKeySetDefault, "set default"),
  restrict(importedKeyRestrict, "restrict");

  private final int id;
  private final String text;

  ForeignKeyUpdateRule(final int id, final String text) {
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

/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
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

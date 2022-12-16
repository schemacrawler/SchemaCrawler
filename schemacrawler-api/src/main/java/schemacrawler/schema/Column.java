/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.util.Collection;
import java.util.Optional;

/** Represents a column in a database table or routine. */
public interface Column extends BaseColumn<Table> {

  /**
   * Gets the default data value for the column.
   *
   * @return Default data value for the column
   */
  String getDefaultValue();

  /**
   * Gets the list of privileges for the column.
   *
   * @return Privileges for the column
   */
  Collection<Privilege<Column>> getPrivileges();

  /**
   * Referenced column if this column is part of a foreign key, null otherwise.
   *
   * @return Referenced column
   */
  Column getReferencedColumn();

  /**
   * Checks whether there is a default data value for the column.
   *
   * @return Whether there is a default data value
   */
  default boolean hasDefaultValue() {
    return getDefaultValue() != null;
  }

  /**
   * True if this column is auto-incremented.
   *
   * @return If the column is auto-incremented
   */
  boolean isAutoIncremented();

  /**
   * True if this column is a generated column.
   *
   * @return If the column is a generated column
   */
  boolean isGenerated();

  /**
   * True if this column is a hidden column.
   *
   * @return If the column is a hidden column
   */
  boolean isHidden();

  /**
   * True if this column is part of a foreign key.
   *
   * @return If the column is part of a foreign key
   */
  boolean isPartOfForeignKey();

  /**
   * True if this column is part of an index.
   *
   * @return If the column is part of an index
   */
  boolean isPartOfIndex();

  /**
   * True if this column is a part of primary key.
   *
   * @return If the column is a part of primary key
   */
  boolean isPartOfPrimaryKey();

  /**
   * True if this column is part of an unique index.
   *
   * @return If the column is part of an unique index
   */
  boolean isPartOfUniqueIndex();

  /**
   * Gets a privilege by unqualified name.
   *
   * @param name Unqualified name
   * @return Privilege.
   */
  <P extends Privilege<Column>> Optional<P> lookupPrivilege(String name);
}

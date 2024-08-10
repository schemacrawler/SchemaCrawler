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

import java.util.List;

/** Represents a foreign-key mapping to a primary key in another table. */
public interface TableReference
    extends NamedObject,
        AttributedObject,
        DescribedObject,
        TableConstraint,
        Iterable<ColumnReference> {

  /**
   * Gets the list of column pairs.
   *
   * @return Column pairs
   */
  List<ColumnReference> getColumnReferences();

  /**
   * Gets dependent or child table for this reference.
   *
   * @return Dependent table for this reference.
   */
  default Table getDependentTable() {
    return getForeignKeyTable();
  }

  /**
   * Gets the dependent table with an imported foreign key.
   *
   * @return Dependent table.
   */
  Table getForeignKeyTable();

  /**
   * Gets the referenced table.
   *
   * @return Referenced table.
   */
  Table getPrimaryKeyTable();

  /**
   * Gets referenced or parent table for this reference.
   *
   * @return Referenced table for this reference.
   */
  default Table getReferencedTable() {
    return getPrimaryKeyTable();
  }

  /**
   * @deprecated
   * @see #getDependentTable()
   */
  @Deprecated
  default Table getReferencingTable() {
    return getForeignKeyTable();
  }
}

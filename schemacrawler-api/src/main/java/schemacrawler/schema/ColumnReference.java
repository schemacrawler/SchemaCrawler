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

import java.io.Serializable;

/** Represents a single column mapping from a primary key column to a foreign key column. */
public interface ColumnReference extends Serializable, Comparable<ColumnReference> {

  /**
   * Gets the foreign key column.
   *
   * @return Foreign key column
   */
  Column getForeignKeyColumn();

  /**
   * Gets the sequence in the foreign key.
   *
   * @return Foreign key sequence
   */
  int getKeySequence();

  /**
   * Gets the primary key column.
   *
   * @return Primary key column
   */
  Column getPrimaryKeyColumn();
}

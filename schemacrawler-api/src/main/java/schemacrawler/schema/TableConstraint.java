/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

/** Represents a table constraint. */
public interface TableConstraint
    extends DependantObject<Table>, DefinedObject, TypedObject<TableConstraintType> {

  /**
   * Gets the list of columns in ordinal order.
   *
   * @return Columns of the table constraint.
   */
  List<TableConstraintColumn> getConstrainedColumns();
  /**
   * Whether the constraint is deferrable.
   *
   * @return Whether the constraint is deferrable
   */
  boolean isDeferrable();

  /**
   * Whether the constraint is initially deferred.
   *
   * @return Whether the constraint is initially deferred
   */
  boolean isInitiallyDeferred();
}

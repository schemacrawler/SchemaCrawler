/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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

/**
 * Represents a table constraint.
 *
 * @author Sualeh Fatehi
 */
public interface TableConstraint
  extends DependantObject<Table>, TypedObject<TableConstraintType>,
  DefinedObject
{

  /**
   * Gets the list of columns in ordinal order.
   *
   * @return Columns of the table constraint.
   */
  List<TableConstraintColumn> getColumns();

  /**
   * Gets the table constraint type.
   *
   * @return Table constraint type
   */
  TableConstraintType getTableConstraintType();

  @Override
  default TableConstraintType getType()
  {
    return getTableConstraintType();
  }

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

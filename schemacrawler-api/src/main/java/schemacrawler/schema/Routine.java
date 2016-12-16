/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
import java.util.Optional;

public interface Routine
  extends DatabaseObject, TypedObject<RoutineType>, DefinedObject
{

  /**
   * Gets the list of columns in ordinal order.
   *
   * @return Columns of the routine
   */
  List<? extends RoutineColumn<? extends Routine>> getColumns();

  /**
   * Gets the routine type.
   *
   * @return Routine type
   */
  RoutineReturnType getReturnType();

  /**
   * Gets the type of the routine body.
   *
   * @return Routine body type
   */
  RoutineBodyType getRoutineBodyType();

  /**
   * Gets the routine type.
   *
   * @return Routine type.
   */
  RoutineType getRoutineType();

  /**
   * The name which uniquely identifies this routine within its schema.
   *
   * @return Specific name.
   */
  String getSpecificName();

  /**
   * Gets a column by unqualified name.
   *
   * @param name
   *        Unqualified name
   * @return Column.
   */
  Optional<? extends RoutineColumn<? extends Routine>> lookupColumn(String name);

}

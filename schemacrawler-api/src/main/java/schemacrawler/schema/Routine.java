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

import java.util.List;
import java.util.Optional;

public interface Routine extends DatabaseObject, TypedObject<RoutineType>, DefinedObject {

  /**
   * Gets the list of parameters in ordinal order.
   *
   * @return Parameters of the routine
   */
  <C extends RoutineParameter<? extends Routine>> List<C> getParameters();

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
   * Gets a parameter by unqualified name.
   *
   * @param name Unqualified name
   * @return Parameter.
   */
  <C extends RoutineParameter<? extends Routine>> Optional<C> lookupParameter(String name);
}

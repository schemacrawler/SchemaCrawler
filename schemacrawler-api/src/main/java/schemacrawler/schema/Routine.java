/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

import java.util.List;
import java.util.Optional;

public interface Routine
    extends DatabaseObject, TypedObject<RoutineType>, DefinedObject, ReferencingObject {

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

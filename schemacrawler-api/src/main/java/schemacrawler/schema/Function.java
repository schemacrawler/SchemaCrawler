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

/** Represents a database function. */
public interface Function extends Routine {

  /**
   * Gets the list of columns in ordinal order.
   *
   * @return Columns of the procedure
   */
  @Override
  List<FunctionParameter> getParameters();

  /**
   * Gets the procedure type.
   *
   * @return Procedure type
   */
  @Override
  FunctionReturnType getReturnType();

  /**
   * Gets the type of the routine body.
   *
   * @return Routine body type
   */
  @Override
  RoutineBodyType getRoutineBodyType();

  /**
   * Gets a column by name.
   *
   * @param name Name
   * @return Column of the procedure
   */
  @Override
  Optional<? extends FunctionParameter> lookupParameter(String name);
}

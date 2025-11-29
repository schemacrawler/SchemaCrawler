/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

public interface RoutineParameter<R extends Routine> extends BaseColumn<R> {

  /**
   * Gets the routine column type.
   *
   * @return Routine column type.
   */
  ParameterModeType getParameterMode();

  /**
   * Gets the number of decimal digits precision for the column.
   *
   * @return The number of decimal digits precision for the column.
   */
  int getPrecision();
}

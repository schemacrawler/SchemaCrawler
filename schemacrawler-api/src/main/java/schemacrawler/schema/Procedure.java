/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

import java.util.List;
import java.util.Optional;

/** Represents a database procedure. */
public interface Procedure extends Routine {

  /**
   * Gets the list of columns in ordinal order.
   *
   * @return Columns of the procedure
   */
  @Override
  List<ProcedureParameter> getParameters();

  /**
   * Gets the procedure type.
   *
   * @return Procedure type
   */
  @Override
  ProcedureReturnType getReturnType();

  /**
   * Gets a column by unqualified name.
   *
   * @param name Name
   * @return Column of the procedure
   */
  @Override
  Optional<? extends ProcedureParameter> lookupParameter(String name);
}

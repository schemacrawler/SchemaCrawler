/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

import static java.sql.DatabaseMetaData.procedureNoResult;
import static java.sql.DatabaseMetaData.procedureResultUnknown;
import static java.sql.DatabaseMetaData.procedureReturnsResult;

/** An enumeration wrapper around JDBC procedure return types. */
public enum ProcedureReturnType implements RoutineReturnType {
  unknown(procedureResultUnknown, "result unknown"),
  noResult(procedureNoResult, "no result"),
  returnsResult(procedureReturnsResult, "returns result");

  private final int id;
  private final String text;

  ProcedureReturnType(final int id, final String text) {
    this.id = id;
    this.text = text;
  }

  /** {@inheritDoc} */
  @Override
  public int id() {
    return id;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return text;
  }
}

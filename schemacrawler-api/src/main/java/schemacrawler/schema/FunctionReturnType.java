/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schema;

import static java.sql.DatabaseMetaData.functionNoTable;
import static java.sql.DatabaseMetaData.functionResultUnknown;
import static java.sql.DatabaseMetaData.functionReturnsTable;

/** An enumeration wrapper around JDBC function return types. */
public enum FunctionReturnType implements RoutineReturnType {
  unknown(functionResultUnknown, "result unknown"),
  noTable(functionNoTable, "does not return a table"),
  returnsTable(functionReturnsTable, "returns table");

  private final int id;
  private final String text;

  FunctionReturnType(final int id, final String text) {
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

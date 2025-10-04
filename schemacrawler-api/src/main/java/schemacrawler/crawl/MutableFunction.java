/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import schemacrawler.schema.Function;
import schemacrawler.schema.FunctionParameter;
import schemacrawler.schema.FunctionReturnType;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;

/** Represents a database function. Created from metadata returned by a JDBC call. */
final class MutableFunction extends MutableRoutine implements Function {

  @Serial private static final long serialVersionUID = 3906925686089134130L;

  private final NamedObjectList<MutableFunctionParameter> columns = new NamedObjectList<>();
  private FunctionReturnType returnType;

  MutableFunction(final Schema schema, final String name, final String specificName) {
    super(schema, name, specificName);
    // Default values
    returnType = FunctionReturnType.unknown;
  }

  /** {@inheritDoc} */
  @Override
  public List<FunctionParameter> getParameters() {
    return new ArrayList<>(columns.values());
  }

  /** {@inheritDoc} */
  @Override
  public FunctionReturnType getReturnType() {
    return returnType;
  }

  @Override
  public RoutineType getRoutineType() {
    return RoutineType.function;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<MutableFunctionParameter> lookupParameter(final String name) {
    return columns.lookup(this, name);
  }

  void addParameter(final MutableFunctionParameter column) {
    columns.add(column);
  }

  void setReturnType(final FunctionReturnType returnType) {
    this.returnType = requireNonNull(returnType, "Null function return type");
  }
}

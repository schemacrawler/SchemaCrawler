/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.model.implementation;


import schemacrawler.crawl.SchemaCrawler;

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureParameter;
import schemacrawler.schema.ProcedureReturnType;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;

/** Represents a database procedure. Created from metadata returned by a JDBC call. */
public final class MutableProcedure extends MutableRoutine implements Procedure {

  @Serial private static final long serialVersionUID = 3906925686089134130L;
  private final NamedObjectList<MutableProcedureParameter> columns = new NamedObjectList<>();
  private ProcedureReturnType returnType;

  public MutableProcedure(final Schema schema, final String name, final String specificName) {
    super(schema, name, specificName);
    // Default values
    returnType = ProcedureReturnType.unknown;
  }

  /** {@inheritDoc} */
  @Override
  public List<ProcedureParameter> getParameters() {
    return new ArrayList<>(columns.values());
  }

  /** {@inheritDoc} */
  @Override
  public ProcedureReturnType getReturnType() {
    return returnType;
  }

  @Override
  public RoutineType getRoutineType() {
    return RoutineType.procedure;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<MutableProcedureParameter> lookupParameter(final String name) {
    return columns.lookup(this, name);
  }

  public void addParameter(final MutableProcedureParameter column) {
    columns.add(column);
  }

  public void setReturnType(final ProcedureReturnType returnType) {
    this.returnType = requireNonNull(returnType, "Null procedure return type");
  }
}

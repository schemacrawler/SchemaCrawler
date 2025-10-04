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
import java.util.List;
import java.util.Optional;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureParameter;
import schemacrawler.schema.ProcedureReturnType;
import schemacrawler.schema.RoutineType;

final class ProcedurePartial extends RoutinePartial implements Procedure {

  @Serial private static final long serialVersionUID = -1529756351918040452L;

  ProcedurePartial(final Procedure procedure) {
    super(requireNonNull(procedure, "No procedure provided").getSchema(), procedure.getName());
  }

  @Override
  public List<ProcedureParameter> getParameters() {
    throw new NotLoadedException(this);
  }

  @Override
  public ProcedureReturnType getReturnType() {
    throw new NotLoadedException(this);
  }

  @Override
  public RoutineType getRoutineType() {
    return RoutineType.procedure;
  }

  @Override
  public Optional<ProcedureParameter> lookupParameter(final String name) {
    throw new NotLoadedException(this);
  }
}

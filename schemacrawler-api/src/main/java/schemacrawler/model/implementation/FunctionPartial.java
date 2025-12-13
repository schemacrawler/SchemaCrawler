/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.model.implementation;


import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.NotLoadedException;

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.util.List;
import java.util.Optional;
import schemacrawler.schema.Function;
import schemacrawler.schema.FunctionParameter;
import schemacrawler.schema.FunctionReturnType;
import schemacrawler.schema.RoutineType;

public final class FunctionPartial extends RoutinePartial implements Function {

  @Serial private static final long serialVersionUID = -1529756351918040452L;

  public FunctionPartial(final Function function) {
    super(requireNonNull(function, "No function provided").getSchema(), function.getName());
  }

  @Override
  public List<FunctionParameter> getParameters() {
    throw new NotLoadedException(this);
  }

  @Override
  public FunctionReturnType getReturnType() {
    throw new NotLoadedException(this);
  }

  @Override
  public RoutineType getRoutineType() {
    return RoutineType.function;
  }

  @Override
  public Optional<FunctionParameter> lookupParameter(final String name) {
    throw new NotLoadedException(this);
  }
}

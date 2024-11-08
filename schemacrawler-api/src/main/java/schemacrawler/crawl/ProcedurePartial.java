/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureParameter;
import schemacrawler.schema.ProcedureReturnType;
import schemacrawler.schema.RoutineType;

final class ProcedurePartial extends RoutinePartial implements Procedure {

  private static final long serialVersionUID = -1529756351918040452L;

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

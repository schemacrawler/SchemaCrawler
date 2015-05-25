/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schema.ProcedureReturnType;

final class ProcedurePartial
  extends RoutinePartial
  implements Procedure
{

  private static final long serialVersionUID = -1529756351918040452L;

  ProcedurePartial(final Procedure procedure)
  {
    super(requireNonNull(procedure, "No procedure provided").getSchema(),
          procedure.getName());
  }

  @Override
  public List<ProcedureColumn> getColumns()
  {
    throw new NotLoadedException();
  }

  @Override
  public ProcedureReturnType getReturnType()
  {
    throw new NotLoadedException();
  }

  @Override
  public Optional<ProcedureColumn> lookupColumn(final String name)
  {
    throw new NotLoadedException();
  }

}

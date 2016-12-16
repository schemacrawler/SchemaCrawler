/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schema.ProcedureReturnType;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;

/**
 * Represents a database procedure. Created from metadata returned by a
 * JDBC call.
 *
 * @author Sualeh Fatehi
 */
final class MutableProcedure
  extends MutableRoutine
  implements Procedure
{

  private static final long serialVersionUID = 3906925686089134130L;

  private ProcedureReturnType returnType;
  private final NamedObjectList<MutableProcedureColumn> columns = new NamedObjectList<>();

  MutableProcedure(final Schema schema, final String name)
  {
    super(schema, name);
    // Default values
    returnType = ProcedureReturnType.unknown;
  }

  /**
   * {@inheritDoc}
   *
   * @see Procedure#getColumns()
   */
  @Override
  public List<ProcedureColumn> getColumns()
  {
    return new ArrayList<>(columns.values());
  }

  /**
   * {@inheritDoc}
   *
   * @see Procedure#getReturnType()
   */
  @Override
  public ProcedureReturnType getReturnType()
  {
    return returnType;
  }

  @Override
  public RoutineType getRoutineType()
  {
    return RoutineType.procedure;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Procedure#lookupColumn(java.lang.String)
   */
  @Override
  public Optional<MutableProcedureColumn> lookupColumn(final String name)
  {
    return columns.lookup(this, name);
  }

  void addColumn(final MutableProcedureColumn column)
  {
    columns.add(column);
  }

  void setReturnType(final ProcedureReturnType returnType)
  {
    this.returnType = requireNonNull(returnType, "Null procedure return type");
  }

}

/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import schemacrawler.schema.Function;
import schemacrawler.schema.FunctionParameter;
import schemacrawler.schema.FunctionReturnType;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;

/** Represents a database function. Created from metadata returned by a JDBC call. */
final class MutableFunction extends MutableRoutine implements Function {

  private static final long serialVersionUID = 3906925686089134130L;

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

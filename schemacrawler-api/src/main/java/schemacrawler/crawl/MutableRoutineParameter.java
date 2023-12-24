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

import schemacrawler.schema.ParameterModeType;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineParameter;

/** Represents a parameter in a database routine. Created from metadata returned by a JDBC call. */
abstract class MutableRoutineParameter<R extends Routine> extends AbstractColumn<R>
    implements RoutineParameter<R> {

  private static final long serialVersionUID = 3546361725629772857L;

  private ParameterModeType parameterMode;

  /**
   * Effective Java - Item 17 - Minimize Mutability - Package-private constructors make a class
   * effectively final
   *
   * @param parent Parent of this object
   * @param name Name of the named object
   */
  MutableRoutineParameter(final DatabaseObjectReference<R> parent, final String name) {
    super(parent, name);
  }

  /** {@inheritDoc} */
  @Override
  public ParameterModeType getParameterMode() {
    return parameterMode;
  }

  /** {@inheritDoc} */
  @Override
  public final int getPrecision() {
    return getDecimalDigits();
  }

  void setParameterMode(final ParameterModeType parameterMode) {
    this.parameterMode = parameterMode;
  }

  final void setPrecision(final int precision) {
    setDecimalDigits(precision);
  }
}

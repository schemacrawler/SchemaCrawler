/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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

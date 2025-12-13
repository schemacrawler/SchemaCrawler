/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.model.implementation;


import schemacrawler.crawl.SchemaCrawler;

import java.io.Serial;
import schemacrawler.schema.Function;
import schemacrawler.schema.FunctionParameter;

/** Represents a column in a database function. Created from metadata returned by a JDBC call. */
public final class MutableFunctionParameter extends MutableRoutineParameter<Function>
    implements FunctionParameter {

  @Serial private static final long serialVersionUID = 3546361725629772857L;

  public MutableFunctionParameter(final Function parent, final String name) {
    super(new FunctionPointer(parent), name);
  }
}

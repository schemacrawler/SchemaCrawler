/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import schemacrawler.schema.Function;
import schemacrawler.schema.FunctionParameter;

/** Represents a column in a database function. Created from metadata returned by a JDBC call. */
final class MutableFunctionParameter extends MutableRoutineParameter<Function>
    implements FunctionParameter {

  private static final long serialVersionUID = 3546361725629772857L;

  MutableFunctionParameter(final Function parent, final String name) {
    super(new FunctionPointer(parent), name);
  }
}

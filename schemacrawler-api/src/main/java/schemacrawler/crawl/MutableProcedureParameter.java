/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureParameter;

/** Represents a column in a database procedure. Created from metadata returned by a JDBC call. */
final class MutableProcedureParameter extends MutableRoutineParameter<Procedure>
    implements ProcedureParameter {

  private static final long serialVersionUID = 3546361725629772857L;

  MutableProcedureParameter(final Procedure parent, final String name) {
    super(new ProcedurePointer(parent), name);
  }
}

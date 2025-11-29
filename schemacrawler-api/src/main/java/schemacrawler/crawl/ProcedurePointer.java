/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import schemacrawler.schema.Procedure;

class ProcedurePointer extends DatabaseObjectReference<Procedure> {

  @Serial private static final long serialVersionUID = 5422838457822334919L;

  ProcedurePointer(final Procedure procedure) {
    super(requireNonNull(procedure, "No procedure provided"), new ProcedurePartial(procedure));
  }
}

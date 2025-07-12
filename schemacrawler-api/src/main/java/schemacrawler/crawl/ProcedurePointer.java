/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Procedure;

class ProcedurePointer extends DatabaseObjectReference<Procedure> {

  private static final long serialVersionUID = 5422838457822334919L;

  ProcedurePointer(final Procedure procedure) {
    super(requireNonNull(procedure, "No procedure provided"), new ProcedurePartial(procedure));
  }
}

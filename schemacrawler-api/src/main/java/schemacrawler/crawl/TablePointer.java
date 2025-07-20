/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Table;

final class TablePointer extends DatabaseObjectReference<Table> {

  private static final long serialVersionUID = 8940800217960888019L;

  TablePointer(final Table table) {
    super(requireNonNull(table, "No table provided"), new TablePartial(table));
  }
}

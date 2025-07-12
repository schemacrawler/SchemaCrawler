/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Column;

final class ColumnPointer extends DatabaseObjectReference<Column> {

  private static final long serialVersionUID = 122669483681884924L;

  ColumnPointer(final Column column) {
    super(requireNonNull(column, "No column provided"), new ColumnPartial(column));
  }
}

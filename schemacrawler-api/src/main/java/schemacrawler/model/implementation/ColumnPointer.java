/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.model.implementation;


import schemacrawler.crawl.SchemaCrawler;

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import schemacrawler.schema.Column;

public final class ColumnPointer extends DatabaseObjectReference<Column> {

  @Serial private static final long serialVersionUID = 122669483681884924L;

  public ColumnPointer(final Column column) {
    super(requireNonNull(column, "No column provided"), new ColumnPartial(column));
  }
}

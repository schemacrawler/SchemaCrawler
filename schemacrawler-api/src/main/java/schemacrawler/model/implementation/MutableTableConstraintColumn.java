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
import schemacrawler.schema.Column;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintColumn;

public final class MutableTableConstraintColumn extends MutableKeyColumn implements TableConstraintColumn {

  @Serial private static final long serialVersionUID = -6923211341742623556L;

  private final TableConstraint tableConstraint;

  public MutableTableConstraintColumn(final TableConstraint tableConstraint, final Column column) {
    super(column);
    this.tableConstraint = tableConstraint;
  }

  /** {@inheritDoc} */
  @Override
  public TableConstraint getTableConstraint() {
    return tableConstraint;
  }

  /** {@inheritDoc} */
  @Override
  public int getTableConstraintOrdinalPosition() {
    return getKeyOrdinalPosition();
  }
}

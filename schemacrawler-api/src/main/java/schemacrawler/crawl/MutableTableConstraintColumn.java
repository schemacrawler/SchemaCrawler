/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import schemacrawler.schema.Column;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintColumn;

final class MutableTableConstraintColumn extends MutableKeyColumn implements TableConstraintColumn {

  private static final long serialVersionUID = -6923211341742623556L;

  private final TableConstraint tableConstraint;

  MutableTableConstraintColumn(final TableConstraint tableConstraint, final Column column) {
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

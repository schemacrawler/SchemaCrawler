/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import schemacrawler.schema.Column;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.IndexColumnSortSequence;

final class MutableIndexColumn extends MutableKeyColumn implements IndexColumn {

  private static final long serialVersionUID = -6923211341742623556L;

  private final Index index;
  private IndexColumnSortSequence sortSequence;

  MutableIndexColumn(final Index index, final Column column) {
    super(column);
    this.index = index;
  }

  /** {@inheritDoc} */
  @Override
  public Index getIndex() {
    return index;
  }

  /** {@inheritDoc} */
  @Override
  public int getIndexOrdinalPosition() {
    return getKeyOrdinalPosition();
  }

  /** {@inheritDoc} */
  @Override
  public IndexColumnSortSequence getSortSequence() {
    return sortSequence;
  }

  void setSortSequence(final IndexColumnSortSequence sortSequence) {
    this.sortSequence = sortSequence;
  }
}

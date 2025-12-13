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
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.IndexColumnSortSequence;

public final class MutableIndexColumn extends MutableKeyColumn implements IndexColumn {

  @Serial private static final long serialVersionUID = -6923211341742623556L;

  private final Index index;
  private IndexColumnSortSequence sortSequence;

  public MutableIndexColumn(final Index index, final Column column) {
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

  public void setSortSequence(final IndexColumnSortSequence sortSequence) {
    this.sortSequence = sortSequence;
  }
}

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

public interface IndexColumn extends Column {

  /**
   * Gets the index this column belongs to.
   *
   * @return Index
   */
  Index getIndex();

  /**
   * Ordinal position of the column, in the index.
   *
   * @return Ordinal position
   */
  int getIndexOrdinalPosition();

  /**
   * Gets the sort sequence.
   *
   * @return Sort sequence
   */
  IndexColumnSortSequence getSortSequence();
}

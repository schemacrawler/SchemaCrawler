/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

import java.util.List;
import java.util.Optional;

/** Represents an index on a database table. */
public interface Index
    extends DependantObject<Table>, TypedObject<IndexType>, DefinedObject, Iterable<IndexColumn> {

  /**
   * Gets the cardinality. When the index type is statistic, then this is the number of rows in the
   * table; otherwise, it is the number of unique values in the index.
   *
   * @return Cardinality
   */
  long getCardinality();

  /**
   * Gets the list of columns in ordinal order.
   *
   * @return Columns of the index.
   */
  List<IndexColumn> getColumns();

  /**
   * Gets the index type.
   *
   * @return Index type.
   */
  IndexType getIndexType();

  /**
   * Gets the pages. When the index type is statistic, then this is the number of pages used for the
   * table, otherwise it is the number of pages used for the current index.
   *
   * @return Pages
   */
  long getPages();

  /**
   * If the index is unique.
   *
   * @return If the index is unique
   */
  boolean isUnique();

  /**
   * Gets a column by unqualified name.
   *
   * @param name Unqualified name
   * @return Column.
   */
  <C extends IndexColumn> Optional<C> lookupColumn(String name);
}

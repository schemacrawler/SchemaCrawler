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

/** Represents a result set, a result of a query. */
public interface ResultsColumns extends NamedObject, Iterable<ResultsColumn> {

  /**
   * Gets the list of columns in ordinal order.
   *
   * @return Columns of the table.
   */
  List<ResultsColumn> getColumns();

  /**
   * Gets a comma-separated list of columns.
   *
   * @return Comma-separated list of columns
   */
  String getColumnsListAsString();

  /**
   * Gets a column by name.
   *
   * @param name Name
   * @return Column.
   */
  <C extends ResultsColumn> Optional<C> lookupColumn(String name);
}

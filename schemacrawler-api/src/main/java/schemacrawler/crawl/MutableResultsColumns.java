/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import schemacrawler.schema.ResultsColumn;
import schemacrawler.schema.ResultsColumns;

/** Represents a result set, a result of a query. */
final class MutableResultsColumns extends AbstractNamedObject implements ResultsColumns {

  @Serial private static final long serialVersionUID = 5204766782914559188L;

  private final NamedObjectList<MutableResultsColumn> columns = new NamedObjectList<>();

  MutableResultsColumns(final String name) {
    super(name);
  }

  /** {@inheritDoc} */
  @Override
  public List<ResultsColumn> getColumns() {
    return new ArrayList<>(columns.values());
  }

  /** {@inheritDoc} */
  @Override
  public String getColumnsListAsString() {
    String columnsList = "";
    final List<ResultsColumn> columns = getColumns();
    if (columns != null && !columns.isEmpty()) {
      final StringBuilder buffer = new StringBuilder(1024);
      for (int i = 0; i < columns.size(); i++) {
        if (i > 0) {
          buffer.append(", ");
        }
        final ResultsColumn column = columns.get(i);
        buffer.append(column.getFullName());
      }
      columnsList = buffer.toString();
    }
    return columnsList;
  }

  @Override
  public Iterator<ResultsColumn> iterator() {
    return getColumns().iterator();
  }

  /** {@inheritDoc} */
  @Override
  public Optional<MutableResultsColumn> lookupColumn(final String name) {
    // Look up by name, full name or label
    if (name == null) {
      return Optional.empty();
    }
    for (final MutableResultsColumn column : columns) {
      if (name.equalsIgnoreCase(column.getLabel())
          || name.equalsIgnoreCase(column.getFullName())
          || name.equalsIgnoreCase(column.getName())) {
        return Optional.of(column);
      }
    }
    return Optional.empty();
  }

  void addColumn(final MutableResultsColumn column) {
    columns.add(column);
  }
}

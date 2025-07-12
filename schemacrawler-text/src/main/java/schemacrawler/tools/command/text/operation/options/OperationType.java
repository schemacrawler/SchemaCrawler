/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.text.operation.options;

import schemacrawler.schemacrawler.Query;

/** Database operations. */
public enum OperationType implements Operation {

  /** Count operation */
  count("Row Count", "Show counts of rows in the tables", "SELECT COUNT(*) FROM ${table}"),
  /** Dump operation */
  dump(
      "Dump",
      "Show data from all rows in the tables",
      "SELECT ${columns} FROM ${table} ORDER BY ${orderbycolumns}"),
  /**
   * Quick dump operation, where columns do not need to be retrieved (minimum infolevel), but the
   * order of rows may not be preserved from run to run.
   */
  quickdump(
      "Dump",
      "Show data from all rows in the tables, "
          + "but row order is not guaranteed - "
          + "this can be used with a minimum info-level for speed",
      "SELECT * FROM ${table}"),
  ;

  private final String description;
  private final String queryString;
  private final String title;

  OperationType(final String title, final String description, final String queryString) {
    this.title = title;
    this.description = description;
    this.queryString = queryString;
  }

  @Override
  public String getDescription() {
    return description;
  }

  /**
   * Query.
   *
   * @return Query
   */
  @Override
  public Query getQuery() {
    return new Query(name(), queryString);
  }

  /**
   * Operation title.
   *
   * @return Operation title
   */
  @Override
  public String getTitle() {
    return title;
  }
}

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.operation.options;

import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;

/** Database operations. */
public enum OperationType implements Operation {

  /** Count operation */
  count("Row Count", "Show counts of rows in the tables", "SELECT COUNT(*) FROM ${table}"),
  /** Dump operation */
  dump(
      "Dump",
      "Show data from all rows in the tables",
      "SELECT ${columns} FROM ${table} ORDER BY ${basiccolumns}"),
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
  /**
   * Quick dump operation, where columns do not need to be retrieved (minimum infolevel), but the
   * order of rows may not be preserved from run to run.
   */
  tablesample(
      "Table sample",
      "Show sample data from tables, " + "but the samples are not the same from run to run",
      "SELECT ${basiccolumns} FROM ${table}",
      InformationSchemaKey.TABLESAMPLE),
  ;

  private final String description;
  private final String queryString;
  private final String title;
  private final InformationSchemaKey viewKey;

  OperationType(final String title, final String description, final String queryString) {
    this(title, description, queryString, null);
  }

  OperationType(
      final String title,
      final String description,
      final String queryString,
      final InformationSchemaKey viewKey) {
    this.title = title;
    this.description = description;
    this.queryString = queryString;
    this.viewKey = viewKey; // Can be null
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getName() {
    return name();
  }

  /**
   * Query.
   *
   * @return Query
   */
  @Override
  public Query getQuery(final InformationSchemaViews views) {
    if (viewKey == null || views == null || !views.hasQuery(viewKey)) {
      return new Query(name(), queryString);
    }
    final String overriddenQueryString = views.getQuery(viewKey).getQuery();
    return new Query(name(), overriddenQueryString);
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

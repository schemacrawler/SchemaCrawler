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
  count("Row Count", "Show counts of rows in the tables", "SELECT COUNT(*) AS COUNT FROM ${table}"),
  dump(
      "Dump",
      "Show data from all rows in the tables",
      "SELECT ${columns} FROM ${table} ORDER BY ${basiccolumns}"),
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

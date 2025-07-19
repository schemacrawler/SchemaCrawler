/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

import static us.fatehi.utility.TemplatingUtility.extractTemplateVariables;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.io.Serializable;
import java.util.Set;

/** A SQL query. May be parameterized with ant-like variable references. */
public final class Query implements Serializable {

  private static final long serialVersionUID = 2820769346069413473L;

  private final String name;
  private final String query;

  /**
   * Definition of a query, including a name, and parameterized or regular SQL.
   *
   * @param name Query name.
   * @param query Query SQL.
   */
  public Query(final String name, final String query) {
    this.name = requireNotBlank(name, "No query name provided");
    this.query = requireNotBlank(query, "No query SQL provided");
  }

  /**
   * Gets the query name.
   *
   * @return Query name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the query SQL.
   *
   * @return Query SQL
   */
  public String getQuery() {
    return query;
  }

  /**
   * Determines if this query has substitutable parameters, and whether it should be run once for
   * each table.
   *
   * @return If the query is to be run over each table
   */
  public boolean isQueryOver() {
    final Set<String> keys = extractTemplateVariables(query);
    return keys.contains("table");
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return String.format("-- \"%s\"%n%s", name, query);
  }
}

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

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/** A SQL query. May be parameterized with ant-like variable references. */
public record Query(String name, String query) implements Serializable {

  @Serial private static final long serialVersionUID = 2820769346069413473L;

  public Query {
    name = requireNotBlank(name, "No query name provided");
    query = requireNotBlank(query, "No query SQL provided");
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

  @Override
  public String toString() {
    return "-- \"%s\"%n%s".formatted(name, query);
  }
}

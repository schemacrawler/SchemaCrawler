/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.operation.options;

import static java.util.Objects.requireNonNull;
import schemacrawler.schemacrawler.Query;

public class QueryOperation implements Operation {

  public final Query query;

  public QueryOperation(Query query) {
    this.query = requireNonNull(query, "No query provided");
  }

  @Override
  public String getDescription() {
    return "User defined query";
  }

  @Override
  public String getName() {
    return "query";
  }

  @Override
  public Query getQuery() {
    return query;
  }

  /**
   * Operation title.
   *
   * @return Operation title
   */
  @Override
  public String getTitle() {
    return "Query";
  }
}

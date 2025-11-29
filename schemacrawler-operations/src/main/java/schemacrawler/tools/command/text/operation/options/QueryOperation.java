/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.operation.options;

import static java.util.Objects.requireNonNull;

import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;

public record QueryOperation(Query query) implements Operation {

  public QueryOperation {
    query = requireNonNull(query, "No query provided");
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
  public Query getQuery(final InformationSchemaViews views) {
    // Query is not overridden from information schema views
    return query;
  }

  @Override
  public String getTitle() {
    return "Query";
  }
}

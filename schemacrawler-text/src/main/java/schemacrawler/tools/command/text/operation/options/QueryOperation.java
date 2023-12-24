/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
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

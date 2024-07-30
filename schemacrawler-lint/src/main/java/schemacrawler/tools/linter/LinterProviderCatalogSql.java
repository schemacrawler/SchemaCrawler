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

package schemacrawler.tools.linter;

import java.sql.Connection;
import java.sql.SQLException;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.QueryUtility;
import schemacrawler.schemacrawler.exceptions.DatabaseAccessException;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.Linter;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.property.PropertyName;

public class LinterProviderCatalogSql extends BaseLinterProvider {

  private static final long serialVersionUID = 7775205295917734672L;

  public LinterProviderCatalogSql() {
    super(LinterCatalogSql.class.getName());
  }

  @Override
  public Linter newLinter() {
    return new LinterCatalogSql(getPropertyName());
  }
}

class LinterCatalogSql extends BaseLinter {

  private String message;
  private String sql;

  LinterCatalogSql(final PropertyName propertyName) {
    super(propertyName);
  }

  @Override
  public String getSummary() {
    if (isBlank(message)) {
      // Linter is not configured
      return "SQL statement based catalog linter";
    }
    return message;
  }

  @Override
  protected void configure(final Config config) {
    requireNonNull(config, "No configuration provided");

    message = config.getStringValue("message", "");
    requireNotBlank(message, "No message provided");

    sql = config.getStringValue("sql", "");
    requireNotBlank(sql, "No SQL provided");
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    // No-op, since the actual linting is done in the start method
  }

  @Override
  protected void start(final Connection connection) {
    super.start(connection);

    if (isBlank(sql)) {
      return;
    }

    requireNonNull(connection, "No connection provided");

    try {
      final Query query = new Query(message, sql);
      final Object queryResult = QueryUtility.executeForScalar(query, connection);
      if (queryResult != null) {
        addCatalogLint(getSummary() + " " + queryResult, true);
      }
    } catch (final SQLException e) {
      throw new DatabaseAccessException(
          String.format("Could not execute SQL for catalog lints%n%s", sql), e);
    }
  }
}

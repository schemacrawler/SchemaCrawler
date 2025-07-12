/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.linter;

import static schemacrawler.schemacrawler.QueryUtility.executeForScalar;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.IdentifiersBuilder;
import schemacrawler.schemacrawler.Query;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.Linter;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.StringFormat;

public class LinterProviderTableSql extends BaseLinterProvider {

  private static final long serialVersionUID = -7901644028908017034L;

  public LinterProviderTableSql() {
    super(LinterTableSql.class.getName());
  }

  @Override
  public Linter newLinter(final LintCollector lintCollector) {
    return new LinterTableSql(getPropertyName(), lintCollector);
  }
}

class LinterTableSql extends BaseLinter {

  private static final Logger LOGGER = Logger.getLogger(LinterTableSql.class.getName());

  private String message;
  private String sql;

  LinterTableSql(final PropertyName propertyName, final LintCollector lintCollector) {
    super(propertyName, lintCollector);
  }

  @Override
  public String getSummary() {
    if (isBlank(message)) {
      // Linter is not configured
      return "SQL statement based table linter";
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
  public boolean usesConnection() {
    return true;
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    if (isBlank(sql)) {
      return;
    }

    requireNonNull(table, "No table provided");
    requireNonNull(connection, "No connection provided");

    final Query query = new Query(message, sql);
    try {
      final Identifiers identifiers =
          IdentifiersBuilder.builder().fromConnection(connection).toOptions();
      final Object queryResult = executeForScalar(query, connection, table, identifiers);
      if (queryResult != null) {
        addTableLint(table, getSummary() + " " + queryResult);
      }
    } catch (final SQLException e) {
      LOGGER.log(
          Level.WARNING,
          e,
          new StringFormat("Could not execute SQL for table lints, for table", table));
    }
  }
}

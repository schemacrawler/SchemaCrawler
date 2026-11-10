/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.postgresql;

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.SQLRuntimeException;
import us.fatehi.utility.string.StringFormat;

public final class PostgreSQLConnectionInitializer implements Consumer<Connection> {

  private static final Logger LOGGER =
      Logger.getLogger(PostgreSQLConnectionInitializer.class.getName());

  @Override
  public void accept(final Connection connection) {
    requireNonNull(connection, "No database connection provided");

    LOGGER.log(Level.FINE, new StringFormat("Initializing PostgreSQL connection <%s>", connection));

    try {
      // Reset PostgreSQL `search_path`
      connection.setSchema(null);
    } catch (final SQLException e) {
      throw new SQLRuntimeException("Could not reset PostgreSQL `search_path`", e);
    }

    LOGGER.log(Level.FINE, new StringFormat("Initialized PostgreSQL connection <%s>", connection));
  }
}

/*
 * ======================================================================== SchemaCrawler
 * http://www.schemacrawler.com Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>. All
 * rights reserved. ------------------------------------------------------------------------
 *
 * SchemaCrawler is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * SchemaCrawler and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0, GNU General Public License v3 or GNU Lesser General Public License v3.
 *
 * You may elect to redistribute this code under any of these licenses.
 *
 * The Eclipse Public License is available at: http://www.eclipse.org/legal/epl-v10.html
 *
 * The GNU General Public License v3 and the GNU Lesser General Public License v3 are available at:
 * http://www.gnu.org/licenses/
 *
 * ========================================================================
 */
package us.fatehi.utility.database;

import static java.util.Objects.requireNonNull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class UtilityLogger {

  private final Logger logger;


  public UtilityLogger(final Logger logger) {
    this.logger = requireNonNull(logger, "No logger provided");
  }

  public void logSQLWarnings(final ResultSet resultSet) {
    if (resultSet == null) {
      return;
    }
    if (!logger.isLoggable(Level.INFO)) {
      return;
    }

    try {
      logSQLWarnings(resultSet.getWarnings());
      resultSet.clearWarnings();
    } catch (final SQLException e) {
      // NOTE: Do not show exception while logging warnings
      logger.log(Level.WARNING, "Could not log SQL warnings for result set");
    }
  }

  public void logSQLWarnings(final Statement statement) {
    if (statement == null) {
      return;
    }
    if (!logger.isLoggable(Level.INFO)) {
      return;
    }

    try {
      logSQLWarnings(statement.getWarnings());
      statement.clearWarnings();
    } catch (final SQLException e) {
      // NOTE: Do not show exception while logging warnings
      logger.log(Level.WARNING, "Could not log SQL warnings for statement");
    }
  }

  private void logSQLWarnings(final SQLWarning sqlWarning) {
    SQLWarning currentSqlWarning = sqlWarning;
    while (currentSqlWarning != null) {
      final String message =
          String.format("%s%nError code: %d, SQL state: %s", currentSqlWarning.getMessage(),
              currentSqlWarning.getErrorCode(), currentSqlWarning.getSQLState());
      logger.log(Level.INFO, message, currentSqlWarning);
      currentSqlWarning = currentSqlWarning.getNextWarning();
    }
  }
}

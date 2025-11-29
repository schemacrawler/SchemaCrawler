/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.oracle;

import static us.fatehi.utility.database.DatabaseUtility.createStatement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import us.fatehi.utility.string.StringFormat;

class OracleInformationSchemaViewsBuilder
    implements BiConsumer<InformationSchemaViewsBuilder, Connection> {

  private static final Logger LOGGER = Logger.getLogger(OracleDatabaseConnector.class.getName());

  private static String getCatalogScope(final Connection connection) {
    final String sql = "SELECT TABLE_NAME FROM DBA_TABLES WHERE ROWNUM = 1";
    String catalogScope;
    try (final Statement statement = createStatement(connection); ) {
      statement.execute(sql);
      catalogScope = "DBA";
    } catch (final SQLException e) {
      LOGGER.log(Level.FINE, "Could not access DBA tables", e);
      catalogScope = "ALL";
    }

    LOGGER.log(
        Level.INFO,
        new StringFormat("Using Oracle data dictionary catalog scope <%s>", catalogScope));
    return catalogScope;
  }

  @Override
  public void accept(
      final InformationSchemaViewsBuilder informationSchemaViewsBuilder,
      final Connection connection) {
    if (informationSchemaViewsBuilder == null) {
      LOGGER.log(Level.FINE, "No information schema views builder provided");
      return;
    }

    informationSchemaViewsBuilder.fromResourceFolder("/oracle.information_schema");

    // Check level of access
    final String catalogScope = getCatalogScope(connection);
    informationSchemaViewsBuilder.substituteAll("catalogscope", catalogScope);
  }
}

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.sqlserver;

import java.sql.Connection;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.database.SqlScript;
import us.fatehi.utility.string.StringFormat;

public final class SqlServerConnectionInitializer implements Consumer<Connection> {

  private static final Logger LOGGER =
      Logger.getLogger(SqlServerConnectionInitializer.class.getName());

  @Override
  public void accept(final Connection connection) {
    LOGGER.log(Level.FINE, new StringFormat("Initializing SQL Server connection <%s>", connection));
    SqlScript.executeScriptFromResource("/initialize-all-routines.sql", "@", connection);
    SqlScript.executeScriptFromResource("/initialize-all-routine-references.sql", "@", connection);
    SqlScript.executeScriptFromResource("/initialize-all-triggers.sql", "@", connection);
    SqlScript.executeScriptFromResource("/initialize-all-views.sql", "@", connection);
    SqlScript.executeScriptFromResource("/initialize-all-view-table-usage.sql", "@", connection);
    SqlScript.executeScriptFromResource("/initialize-all-table-constraints.sql", "@", connection);
    SqlScript.executeScriptFromResource("/initialize-all-check-constraints.sql", "@", connection);
    SqlScript.executeScriptFromResource(
        "/initialize-all-constraint-column-usage.sql", "@", connection);
    SqlScript.executeScriptFromResource("/initialize-all-sequences.sql", "@", connection);
    SqlScript.executeScriptFromResource("/initialize-all-synonyms.sql", "@", connection);
    SqlScript.executeScriptFromResource(
        "/initialize-all-additional-column-attributes.sql", "@", connection);
    SqlScript.executeScriptFromResource(
        "/initialize-all-additional-table-attributes.sql", "@", connection);
    SqlScript.executeScriptFromResource("/initialize-all-database-users.sql", "@", connection);
    LOGGER.log(Level.FINE, new StringFormat("Initialized SQL Server connection <%s>", connection));
  }
}

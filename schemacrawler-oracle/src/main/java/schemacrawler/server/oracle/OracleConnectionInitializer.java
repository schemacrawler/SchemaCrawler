/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.server.oracle;

import java.sql.Connection;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.database.SqlScript;
import us.fatehi.utility.string.StringFormat;

public final class OracleConnectionInitializer implements Consumer<Connection> {

  private static final Logger LOGGER =
      Logger.getLogger(OracleConnectionInitializer.class.getName());

  @Override
  public void accept(final Connection connection) {
    LOGGER.log(Level.FINE, new StringFormat("Initializing Oracle connection <%s>", connection));
    SqlScript.executeScriptFromResource("/schemacrawler-oracle.before.sql", connection);
    LOGGER.log(Level.FINE, new StringFormat("Initialized connection <%s>", connection));
  }
}

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.databaseconnector;

import schemacrawler.schemacrawler.DatabaseServerType;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;

public final class UnknownDatabaseConnector extends DatabaseConnector {

  public static final DatabaseConnector UNKNOWN = new UnknownDatabaseConnector();

  /** Constructor for unknown databases. Bypass the null-checks of the main constructor */
  private UnknownDatabaseConnector() {
    super(
        DatabaseServerType.UNKNOWN,
        url -> false,
        (informationSchemaViewsBuilder, connection) -> {},
        (schemaRetrievalOptionsBuilder, connection) -> {},
        limitOptionsBuilder -> {},
        () -> DatabaseConnectionSourceBuilder.builder(""));
  }
}

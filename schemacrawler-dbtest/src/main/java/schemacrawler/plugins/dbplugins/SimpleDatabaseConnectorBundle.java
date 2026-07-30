/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins;

import java.util.Collection;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorBundle;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptionsBuilder;
import us.fatehi.utility.datasource.DatabaseServerType;

/** ServiceLoader bridge for YAML database connectors. */
public final class SimpleDatabaseConnectorBundle extends DatabaseConnector
    implements DatabaseConnectorBundle {

  public SimpleDatabaseConnectorBundle() {
    super(DatabaseConnectorOptionsBuilder.builder(DatabaseServerType.UNKNOWN).build());
  }

  @Override
  public Collection<DatabaseConnector> getDatabaseConnectors() {
    final MultiDatabaseConnectorRegistry simpleDatabaseconnectorRegistry =
        MultiDatabaseConnectorRegistry.getInstance();
    return simpleDatabaseconnectorRegistry.getDatabaseConnectors();
  }
}

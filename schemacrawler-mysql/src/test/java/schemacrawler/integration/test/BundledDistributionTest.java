/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import java.sql.Connection;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

public class BundledDistributionTest {

  @Test
  public void testInformationSchema_mysql() throws Exception {

    final Connection connection = null;
    final DatabaseConnectorRegistry registry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    final DatabaseConnector databaseSystemIdentifier =
        registry.findDatabaseConnectorFromDatabaseSystemIdentifier("mysql");
    assertThat(
        databaseSystemIdentifier
            .getSchemaRetrievalOptionsBuilder(connection)
            .toOptions()
            .getInformationSchemaViews()
            .size(),
        is(15));
  }

  @Test
  public void testPlugin_mysql() throws Exception {
    final DatabaseConnectorRegistry registry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    assertThat(registry.hasDatabaseSystemIdentifier("mysql"), is(true));
  }
}

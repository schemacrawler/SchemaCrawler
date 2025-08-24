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
  public void testInformationSchema_sqlserver() throws Exception {

    final Connection connection = null;
    final DatabaseConnectorRegistry registry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    final DatabaseConnector databaseSystemIdentifier =
        registry.findDatabaseConnectorFromDatabaseSystemIdentifier("sqlserver");
    assertThat(
        databaseSystemIdentifier
            .getSchemaRetrievalOptionsBuilder(connection)
            .toOptions()
            .getInformationSchemaViews()
            .size(),
        is(19));
  }

  @Test
  public void testPlugin_sqlserver() throws Exception {
    final DatabaseConnectorRegistry registry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    assertThat(registry.hasDatabaseSystemIdentifier("sqlserver"), is(true));
  }
}

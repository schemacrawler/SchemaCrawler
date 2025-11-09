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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

public class OracleDistributionTest {

  private DatabaseConnector dbConnector;

  @BeforeEach
  public void setup() {
    final DatabaseConnectorRegistry registry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    dbConnector = registry.findDatabaseConnectorFromDatabaseSystemIdentifier("oracle");
  }

  @Test
  public void testIdentifierQuoteString() {

    final Connection connection = null;
    assertThat(
        dbConnector
            .getSchemaRetrievalOptionsBuilder(connection)
            .toOptions()
            .getIdentifierQuoteString(),
        is(""));
  }
}

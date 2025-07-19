/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.datasource;

import java.sql.Connection;

public class DatabaseConnectionSourceUtility {

  public static DatabaseConnectionSource newTestDatabaseConnectionSource(
      final Connection connection) {
    return new TestDatabaseConnectionSource(connection);
  }
}

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.sql.Driver;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.database.DatabaseUtility;

public class DatabaseDriverTest {

  @Test
  public void availableJDBCDrivers() throws Exception {
    final Collection<Driver> availableJDBCDrivers = DatabaseUtility.getAvailableJdbcDrivers();
    final int numJDBCDrivers = availableJDBCDrivers.size();
    assertThat(
        "Number of of avilable JDBC drivers is not correct - found " + numJDBCDrivers,
        numJDBCDrivers,
        is(19));
  }
}

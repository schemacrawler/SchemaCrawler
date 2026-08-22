/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static us.fatehi.test.utility.DataSourceTestUtility.JDBC_DRIVER_COUNT;

import java.util.Collection;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.database.JdbcDriverRegistry;
import us.fatehi.utility.property.PropertyName;

public class DatabaseDriverTest {

  @Test
  public void availableJDBCDrivers() throws Exception {
    final Collection<PropertyName> availableJDBCDrivers = JdbcDriverRegistry.availableJDBCDrivers();
    assertThat(
        "Number of of avilable JDBC drivers is not correct",
        availableJDBCDrivers,
        hasSize(JDBC_DRIVER_COUNT + 8));
  }
}

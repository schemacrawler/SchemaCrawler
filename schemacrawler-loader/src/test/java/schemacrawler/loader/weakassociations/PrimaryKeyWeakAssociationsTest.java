/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.loader.weakassociations;

import static schemacrawler.test.utility.ProposedWeakAssociationsTestUtility.weakAssociations;

import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@DisableLogging
@ResolveTestContext
public class PrimaryKeyWeakAssociationsTest {

  @Test
  @WithTestDatabase(script = "/pk_test_1.sql")
  public void weakAssociations1(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    weakAssociations(testContext, dataSource, false);
    weakAssociations(testContext, dataSource, true);
  }

  @Test
  @WithTestDatabase(script = "/pk_test_2.sql")
  public void weakAssociations2(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    weakAssociations(testContext, dataSource, false);
    weakAssociations(testContext, dataSource, true);
  }

  @Test
  @WithTestDatabase(script = "/pk_test_3.sql")
  public void weakAssociations3(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    weakAssociations(testContext, dataSource, false);
    weakAssociations(testContext, dataSource, true);
  }
}

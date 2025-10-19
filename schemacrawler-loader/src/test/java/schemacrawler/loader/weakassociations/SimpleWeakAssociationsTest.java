/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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
public class SimpleWeakAssociationsTest {

  @Test
  @WithTestDatabase(script = "/simple_weak_association_with_ids.sql")
  public void simpleWeakAssociationWithIds(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    weakAssociations(testContext, dataSource, false);
    weakAssociations(testContext, dataSource, true);
  }

  @Test
  @WithTestDatabase(script = "/simple_weak_association_with_plurals.sql")
  public void simpleWeakAssociationWithPlurals(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    weakAssociations(testContext, dataSource, false);
    weakAssociations(testContext, dataSource, true);
  }
}

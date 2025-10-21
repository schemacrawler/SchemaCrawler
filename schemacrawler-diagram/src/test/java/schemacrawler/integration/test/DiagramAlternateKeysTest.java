/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import schemacrawler.test.AbstractAlternateKeysTest;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.options.OutputFormat;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
public class DiagramAlternateKeysTest extends AbstractAlternateKeysTest {

  @DisplayName("Alternate keys loaded from catalog attributes file")
  @ParameterizedTest(name = "with output to {0}")
  @EnumSource(
      value = DiagramOutputFormat.class,
      names = {"scdot", "htmlx"})
  public void alternateKeys_01(
      final OutputFormat outputFormat,
      final TestContext testContext,
      final DatabaseConnectionSource dataSource)
      throws Exception {
    assertAlternateKeys(testContext, dataSource, outputFormat);
  }
}

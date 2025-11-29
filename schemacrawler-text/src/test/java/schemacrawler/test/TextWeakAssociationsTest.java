/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.options.OutputFormat;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public class TextWeakAssociationsTest extends AbstractWeakAssociationsTest {

  @Override
  @DisplayName("Inferred weak associations")
  @ParameterizedTest(name = "with output to {0}")
  @EnumSource(
      value = TextOutputFormat.class,
      names = {"text", "html"})
  public void weakAssociations_00(
      final OutputFormat outputFormat,
      final TestContext testContext,
      final DatabaseConnectionSource dataSource)
      throws Exception {
    super.weakAssociations_00(outputFormat, testContext, dataSource);
  }

  @Override
  @DisplayName("Weak associations loaded from catalog attributes file")
  @ParameterizedTest(name = "with output to {0}")
  @EnumSource(
      value = TextOutputFormat.class,
      names = {"text", "html"})
  public void weakAssociations_01(
      final OutputFormat outputFormat,
      final TestContext testContext,
      final DatabaseConnectionSource dataSource)
      throws Exception {
    super.weakAssociations_01(outputFormat, testContext, dataSource);
  }

  @Override
  @DisplayName("Weak associations loaded with remarks")
  @ParameterizedTest(name = "with output to {0}")
  @EnumSource(
      value = TextOutputFormat.class,
      names = {"text", "html"})
  public void weakAssociations_02(
      final OutputFormat outputFormat,
      final TestContext testContext,
      final DatabaseConnectionSource dataSource)
      throws Exception {
    super.weakAssociations_02(outputFormat, testContext, dataSource);
  }

  @Override
  @DisplayName("Weak associations loaded with remarks, but hiding remarks")
  @ParameterizedTest(name = "with output to {0}")
  @EnumSource(
      value = TextOutputFormat.class,
      names = {"text", "html"})
  public void weakAssociations_03(
      final OutputFormat outputFormat,
      final TestContext testContext,
      final DatabaseConnectionSource dataSource)
      throws Exception {
    super.weakAssociations_03(outputFormat, testContext, dataSource);
  }
}

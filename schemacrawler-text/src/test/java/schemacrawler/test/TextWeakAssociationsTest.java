/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import schemacrawler.test.utility.TestContext;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.options.OutputFormat;
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

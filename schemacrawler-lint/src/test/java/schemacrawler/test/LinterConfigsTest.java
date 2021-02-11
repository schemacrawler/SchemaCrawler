/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.tools.lint.config.LinterConfigUtility.readLinterConfigs;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerRuntimeException;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.command.lint.options.LintOptionsBuilder;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.config.LinterConfig;
import schemacrawler.tools.lint.config.LinterConfigs;

public class LinterConfigsTest {

  @Test
  @DisplayName("No YAML linter config file")
  public void testParseBadYaml0() throws SchemaCrawlerException {
    final LintOptions lintOptions = LintOptionsBuilder.builder().toOptions();

    final LinterConfigs linterConfigs = readLinterConfigs(lintOptions);

    assertThat(linterConfigs.size(), is(0));
  }

  @Test
  @DisplayName("Invalid YAML linter config file")
  public void testParseBadYaml1() throws SchemaCrawlerException, IOException {
    assertThrows(
        SchemaCrawlerRuntimeException.class,
        () -> {
          final LintOptions lintOptions =
              LintOptionsBuilder.builder()
                  .withLinterConfigs("/schemacrawler-linter-configs-bad-1.yaml")
                  .toOptions();

          final LinterConfigs linterConfigs = readLinterConfigs(lintOptions);
        });
  }

  @Test
  @DisplayName("Valid but incorrect YAML linter config file")
  public void testParseBadYaml2() throws SchemaCrawlerException, IOException {
    assertThrows(
        SchemaCrawlerRuntimeException.class,
        () -> {
          final LintOptions lintOptions =
              LintOptionsBuilder.builder()
                  .withLinterConfigs("/schemacrawler-linter-configs-bad-1.yaml")
                  .toOptions();

          final LinterConfigs linterConfigs = readLinterConfigs(lintOptions);
        });
  }

  @Test
  @DisplayName("Valid YAML linter config file")
  public void testParseGoodLinterConfigs() throws SchemaCrawlerException, IOException {

    final LintOptions lintOptions =
        LintOptionsBuilder.builder()
            .withLinterConfigs("/schemacrawler-linter-configs-1.yaml")
            .toOptions();

    final LinterConfigs linterConfigs = readLinterConfigs(lintOptions);

    assertThat(linterConfigs.size(), is(3));
    for (final LinterConfig linterConfig : linterConfigs) {
      if (linterConfig.getLinterId().equals("linter.Linter1")) {
        assertThat(linterConfig.getSeverity(), equalTo(LintSeverity.medium));
        assertThat(linterConfig.isRunLinter(), is(true));
        assertThat(
            linterConfig.getTableInclusionRule(), is(new RegularExpressionExclusionRule("SOME.*")));
        assertThat(
            linterConfig.getColumnInclusionRule(),
            is(new RegularExpressionInclusionRule("SOME.*")));
      }

      if (linterConfig.getLinterId().equals("linter.Linter2")) {
        assertThat(linterConfig.getSeverity(), nullValue());
        assertThat(linterConfig.isRunLinter(), is(false));
        assertThat(
            linterConfig.getConfig().getStringValue("exclude", "<unknown>"), is("<unknown>"));
      }

      if (linterConfig.getLinterId().equals("linter.Linter3")) {
        assertThat(linterConfig.getSeverity(), equalTo(LintSeverity.high));
        assertThat(linterConfig.isRunLinter(), is(true));
      }
    }
  }
}

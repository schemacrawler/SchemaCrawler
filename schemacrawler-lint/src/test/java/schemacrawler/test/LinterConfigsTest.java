/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.TestUtility.readerForResource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.LinterConfig;
import schemacrawler.tools.lint.LinterConfigs;
import schemacrawler.tools.options.Config;

public class LinterConfigsTest {

  @Test
  public void testParseBadLinterConfigs1() throws SchemaCrawlerException, IOException {
    final Reader reader = readerForResource("bad-schemacrawler-linter-configs-a.xml", UTF_8);
    final LinterConfigs linterConfigs = new LinterConfigs(new Config());
    linterConfigs.parse(reader);
    assertThat(linterConfigs.size(), is(3));

    for (final LinterConfig linterConfig : linterConfigs) {
      if (linterConfig.getLinterId().equals("linter.Linter1")) {
        assertThat(linterConfig.getConfig().getStringValue("exclude", null), nullValue());
      }

      if (linterConfig.getLinterId().equals("linter.Linter2")) {
        assertThat(linterConfig.getConfig().getStringValue("exclude", null), is(".*"));
      }

      if (linterConfig.getLinterId().equals("linter.Linter3")) {
        assertThat(linterConfig.getSeverity(), equalTo(LintSeverity.medium));
      }
    }
  }

  @Test
  public void testParseBadXml0() throws SchemaCrawlerException {
    assertThrows(
        NullPointerException.class,
        () -> {
          final LinterConfigs linterConfigs = new LinterConfigs(new Config());
          linterConfigs.parse(null);
        });
  }

  @Test
  public void testParseBadXml1() throws SchemaCrawlerException, IOException {
    assertThrows(
        SchemaCrawlerException.class,
        () -> {
          final Reader reader = new StringReader("some random string that is not XML");
          final LinterConfigs linterConfigs = new LinterConfigs(new Config());
          linterConfigs.parse(reader);
        });
  }

  @Test
  public void testParseBadXml2() throws SchemaCrawlerException, IOException {
    final Reader reader = readerForResource("bad-schemacrawler-linter-configs-2.xml", UTF_8);
    final LinterConfigs linterConfigs = new LinterConfigs(new Config());
    linterConfigs.parse(reader);
  }

  @Test
  public void testParseGoodLinterConfigs() throws SchemaCrawlerException, IOException {
    final Reader reader = readerForResource("schemacrawler-linter-configs-1.xml", UTF_8);
    final LinterConfigs linterConfigs = new LinterConfigs(new Config());
    linterConfigs.parse(reader);

    assertThat(linterConfigs.size(), is(3));
    for (final LinterConfig linterConfig : linterConfigs) {
      if (linterConfig.getLinterId().equals("linter.Linter1")) {
        assertThat(linterConfig.getSeverity(), equalTo(LintSeverity.medium));
        assertThat(linterConfig.isRunLinter(), is(true));
      }

      if (linterConfig.getLinterId().equals("linter.Linter2")) {
        assertThat(linterConfig.getSeverity(), nullValue());
        assertThat(linterConfig.isRunLinter(), is(false));
        assertThat(linterConfig.getConfig().getStringValue("exclude", null), is(".*"));
      }

      if (linterConfig.getLinterId().equals("linter.Linter3")) {
        assertThat(linterConfig.getSeverity(), equalTo(LintSeverity.high));
        assertThat(linterConfig.isRunLinter(), is(true));
      }
    }
  }
}

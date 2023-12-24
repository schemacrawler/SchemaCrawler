/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.test.schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForColumnInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForRoutineInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForRoutineParameterInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSequenceInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSynonymInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForTableInclusion;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.commandline.utility.SchemaCrawlerOptionsConfig;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.PropertiesUtility;
import us.fatehi.utility.ioresource.ClasspathInputResource;

public class SchemaCrawlerOptionsConfigTest {

  @Test
  public void limitOptionsInclusionRules() {
    final LimitOptionsBuilder builder = LimitOptionsBuilder.builder();
    final Config config = new Config(loadConfig("/limit.config.properties"));

    SchemaCrawlerOptionsConfig.fromConfig(builder, config);

    final LimitOptions limitOptions = builder.toOptions();

    assertThat(
        limitOptions.get(ruleForTableInclusion).toString(), endsWith("{+/.*.SOME_TAB/ -//}"));
    assertThat(
        limitOptions.get(ruleForColumnInclusion).toString(), endsWith("{+/.*/ -/.*.SOME_COL/}"));
    assertThat(
        limitOptions.get(ruleForRoutineInclusion).toString(),
        endsWith("{+/.*/ -/.*.SOME_ROUTINE/}"));
    assertThat(
        limitOptions.get(ruleForRoutineParameterInclusion).toString(),
        endsWith("{+/.*.OTHER_ROUTINE/ -//}"));
    assertThat(
        limitOptions.get(ruleForSynonymInclusion).toString(), endsWith("{+/.*.A_SYNONYM/ -//}"));
    assertThat(
        limitOptions.get(ruleForSequenceInclusion).toString(), endsWith("{+/.*/ -/EXC_SYN/}"));
  }

  @Test
  public void limitOptionsRoutineTypes() {
    final LimitOptionsBuilder builder = LimitOptionsBuilder.builder();
    final Config config = new Config(loadConfig("/limit.config.properties"));

    SchemaCrawlerOptionsConfig.fromConfig(builder, config);

    final LimitOptions limitOptions = builder.toOptions();

    assertThat(limitOptions.getRoutineTypes().toString(), is("[]"));
  }

  @Test
  public void limitOptionsTableTypes() {
    final LimitOptionsBuilder builder = LimitOptionsBuilder.builder();
    final Config config = new Config(loadConfig("/limit.config.properties"));

    SchemaCrawlerOptionsConfig.fromConfig(builder, config);

    final LimitOptions limitOptions = builder.toOptions();

    assertThat(limitOptions.getTableTypes().toString(), is("[other table]"));
  }

  @Test
  public void nullBuilders() {
    final LimitOptionsBuilder limitOptionsBuilder =
        SchemaCrawlerOptionsConfig.fromConfig((LimitOptionsBuilder) null, new Config());
    assertThat(limitOptionsBuilder, is(not(nullValue())));

    final GrepOptionsBuilder grepOptionsBuilder =
        SchemaCrawlerOptionsConfig.fromConfig((GrepOptionsBuilder) null, new Config());
    assertThat(grepOptionsBuilder, is(not(nullValue())));

    SchemaCrawlerOptionsConfig.fromConfig((LoadOptionsBuilder) null, new Config());
    assertThat(grepOptionsBuilder, is(not(nullValue())));
  }

  private Map<String, String> loadConfig(final String configResource) {
    try {
      final Properties properties =
          TestUtility.loadProperties(new ClasspathInputResource(configResource));
      return PropertiesUtility.propertiesMap(properties);
    } catch (final IOException e) {
      fail("Could not load " + configResource, e);
      return null;
    }
  }
}

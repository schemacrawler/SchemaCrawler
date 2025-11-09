/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForColumnInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForRoutineInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForRoutineParameterInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSequenceInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSynonymInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForTableInclusion;

import java.util.Properties;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.tools.commandline.utility.SchemaCrawlerOptionsConfig;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import us.fatehi.test.utility.TestUtility;

public class SchemaCrawlerOptionsConfigTest {

  @Test
  public void limitOptionsInclusionRules() {
    final LimitOptionsBuilder builder = LimitOptionsBuilder.builder();
    final Config config = loadConfig("/limit.config.properties");

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
    final Config config = loadConfig("/limit.config.properties");

    SchemaCrawlerOptionsConfig.fromConfig(builder, config);

    final LimitOptions limitOptions = builder.toOptions();

    assertThat(limitOptions.routineTypes().toString(), is("[]"));
  }

  @Test
  public void limitOptionsTableTypes() {
    final LimitOptionsBuilder builder = LimitOptionsBuilder.builder();
    final Config config = loadConfig("/limit.config.properties");

    SchemaCrawlerOptionsConfig.fromConfig(builder, config);

    final LimitOptions limitOptions = builder.toOptions();

    assertThat(limitOptions.tableTypes().toString(), is("[other table]"));
  }

  @Test
  public void nullBuilders() {
    final LimitOptionsBuilder limitOptionsBuilder =
        SchemaCrawlerOptionsConfig.fromConfig(
            (LimitOptionsBuilder) null, ConfigUtility.newConfig());
    assertThat(limitOptionsBuilder, is(not(nullValue())));

    final GrepOptionsBuilder grepOptionsBuilder =
        SchemaCrawlerOptionsConfig.fromConfig((GrepOptionsBuilder) null, ConfigUtility.newConfig());
    assertThat(grepOptionsBuilder, is(not(nullValue())));

    SchemaCrawlerOptionsConfig.fromConfig((LoadOptionsBuilder) null, ConfigUtility.newConfig());
    assertThat(grepOptionsBuilder, is(not(nullValue())));
  }

  private Config loadConfig(final String configResource) {
    final Properties properties = TestUtility.loadPropertiesFromClasspath(configResource);
    return ConfigUtility.fromProperties(properties);
  }
}

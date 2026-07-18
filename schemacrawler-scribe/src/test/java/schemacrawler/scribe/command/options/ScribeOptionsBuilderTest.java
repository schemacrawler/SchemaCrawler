/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.command.options;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;

public class ScribeOptionsBuilderTest {

  @Test
  public void defaults() {
    final ScribeOptions options = ScribeOptionsBuilder.builder().toOptions();
    assertThat(options.getTitle(), is(""));
    assertThat(options.isIncludeLint(), is(false));
    assertThat(options.getLocale(), is(Locale.getDefault()));
    assertThat(options.isExpandedOutput(), is(false));
  }

  @Test
  public void withLocale() {
    final ScribeOptions options =
        ScribeOptionsBuilder.builder().withLocale(Locale.FRENCH).toOptions();
    assertThat(options.getLocale(), is(Locale.FRENCH));
  }

  @Test
  public void withTitle() {
    final ScribeOptions options = ScribeOptionsBuilder.builder().withTitle("My Report").toOptions();
    assertThat(options.getTitle(), is("My Report"));
  }

  @Test
  public void withExpandedOutput() {
    final ScribeOptions options =
        ScribeOptionsBuilder.builder().withExpandedOutput(true).toOptions();
    assertThat(options.isExpandedOutput(), is(true));
  }

  @Test
  public void fromConfigReadsEachPrefixedConfigFileKey() {
    final Config config = ConfigUtility.newConfig();
    config.put("schemacrawler.scribe.generate-diagrams", false);
    config.put("schemacrawler.scribe.include-lint", true);
    config.put("schemacrawler.scribe.language", "fr");
    config.put("schemacrawler.scribe.expanded-output", true);

    final ScribeOptions options = ScribeOptionsBuilder.builder().fromConfig(config).toOptions();

    assertThat(options.isIncludeLint(), is(true));
    assertThat(options.getLocale(), is(Locale.FRENCH));
    assertThat(options.isExpandedOutput(), is(true));
  }

  @Test
  public void fromConfigReadsEachBareCliOptionKey() {
    // Real CLI parsing (CommandLineUtility.matchedOptionValues()) stores matched option values
    // under the bare option name, not the schemacrawler.scribe.* prefix - fromConfig() must honor
    // this, or every Scribe boolean/string CLI option would be silently ignored.
    final Config config = ConfigUtility.newConfig();
    config.put("generate-diagrams", false);
    config.put("include-lint", true);
    config.put("language", "fr");
    config.put("expanded-output", true);

    final ScribeOptions options = ScribeOptionsBuilder.builder().fromConfig(config).toOptions();

    assertThat(options.isIncludeLint(), is(true));
    assertThat(options.getLocale(), is(Locale.FRENCH));
    assertThat(options.isExpandedOutput(), is(true));
  }

  @Test
  public void fromConfigHonorsBareCliOptionKeyOverPrefixedConfigFileKey() {
    final Config config = ConfigUtility.newConfig();
    config.put("schemacrawler.scribe.expanded-output", false);
    config.put("expanded-output", true);

    final ScribeOptions options = ScribeOptionsBuilder.builder().fromConfig(config).toOptions();

    assertThat(options.isExpandedOutput(), is(true));
  }

  @Test
  public void fromConfigWithAbsentLocaleUsesDefault() {
    final Config config = ConfigUtility.newConfig();
    final ScribeOptions options = ScribeOptionsBuilder.builder().fromConfig(config).toOptions();
    assertThat(options.getLocale(), is(Locale.getDefault()));
  }
}

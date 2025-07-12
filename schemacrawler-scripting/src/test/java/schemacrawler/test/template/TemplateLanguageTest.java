/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.test.template;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import schemacrawler.tools.command.template.options.TemplateLanguageOptionsBuilder;
import schemacrawler.tools.command.template.options.TemplateLanguageType;
import schemacrawler.tools.options.Config;

public class TemplateLanguageTest {

  @Test
  public void templateLanguageByName() throws Exception {
    final TemplateLanguageOptionsBuilder builder = TemplateLanguageOptionsBuilder.builder();
    for (final TemplateLanguageType templateLanguageType : TemplateLanguageType.values()) {
      final Config config = new Config();
      config.put("templating-language", templateLanguageType.name());
      config.put("template", "script.file");
      builder.fromConfig(config);

      final String language = builder.toOptions().getLanguage();
      assertThat(TemplateLanguageType.valueOf(language), is(templateLanguageType));
    }
  }

  @Test
  public void templateLanguageForBadValue() {
    final TemplateLanguageOptionsBuilder builder = TemplateLanguageOptionsBuilder.builder();
    final Config config = new Config();
    config.put("templating-language", "bad-value");
    config.put("template", "script.file");
    builder.fromConfig(config);

    final String language = builder.toOptions().getLanguage();
    assertThrows(IllegalArgumentException.class, () -> TemplateLanguageType.valueOf(language));
  }

  @Test
  public void templateLanguageForBlank() {
    final TemplateLanguageOptionsBuilder builder = TemplateLanguageOptionsBuilder.builder();
    final Config config = new Config();
    config.put("templating-language", "");
    config.put("template", "script.file");
    builder.fromConfig(config);

    final String language = builder.toOptions().getLanguage();
    assertThrows(IllegalArgumentException.class, () -> TemplateLanguageType.valueOf(language));
  }

  @Test
  public void templateLanguageForNull() {
    final TemplateLanguageOptionsBuilder builder = TemplateLanguageOptionsBuilder.builder();
    final Config config = new Config();
    config.put("templating-language", null);
    config.put("template", "script.file");
    builder.fromConfig(config);

    final String language = builder.toOptions().getLanguage();
    assertThrows(IllegalArgumentException.class, () -> TemplateLanguageType.valueOf(language));
  }
}

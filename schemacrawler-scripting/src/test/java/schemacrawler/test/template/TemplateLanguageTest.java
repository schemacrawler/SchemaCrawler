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

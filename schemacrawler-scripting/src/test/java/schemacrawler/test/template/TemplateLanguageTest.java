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

package schemacrawler.test.template;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.integration.template.TemplateLanguage;
import schemacrawler.tools.integration.template.TemplateLanguageType;

public class TemplateLanguageTest
{

  @Test
  public void templateLanguageByName()
    throws Exception
  {
    final TemplateLanguage templateLanguage = new TemplateLanguage();
    for (final TemplateLanguageType templateLanguageType : TemplateLanguageType.values())
    {
      final Config config = new Config();
      config.put("templating-language", templateLanguageType.name());
      templateLanguage.addConfig(config);

      assertThat(templateLanguage.getTemplateLanguageType(),
                 is(templateLanguageType));
    }
  }

  @Test
  public void templateLanguageForNull()
  {
    final TemplateLanguage templateLanguage = new TemplateLanguage();
    final Config config = new Config();
    config.put("templating-language", null);
    templateLanguage.addConfig(config);

    assertThat(templateLanguage.getTemplateLanguageType(),
               is(TemplateLanguageType.unknown));
  }

  @Test
  public void templateLanguageForBlank()
  {
    final TemplateLanguage templateLanguage = new TemplateLanguage();
    final Config config = new Config();
    config.put("templating-language", "");
    templateLanguage.addConfig(config);

    assertThat(templateLanguage.getTemplateLanguageType(),
               is(TemplateLanguageType.unknown));
  }

  @Test
  public void templateLanguageForBadValue()
  {
    final TemplateLanguage templateLanguage = new TemplateLanguage();
    final Config config = new Config();
    config.put("templating-language", "bad-value");
    templateLanguage.addConfig(config);

    assertThat(templateLanguage.getTemplateLanguageType(),
               is(TemplateLanguageType.unknown));
  }

}

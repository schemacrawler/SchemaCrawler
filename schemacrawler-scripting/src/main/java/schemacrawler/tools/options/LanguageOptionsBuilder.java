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

package schemacrawler.tools.options;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.IOUtility.getFileExtension;
import static us.fatehi.utility.Utility.isBlank;

import schemacrawler.schemacrawler.OptionsBuilder;

public abstract class LanguageOptionsBuilder<O extends LanguageOptions>
    implements OptionsBuilder<LanguageOptionsBuilder<O>, O>,
        ConfigOptionsBuilder<LanguageOptionsBuilder<O>, O> {

  private final String defaultLanguage;
  private final String languageKey;
  private final String resourceKey;
  private String language;
  private String script;

  protected LanguageOptionsBuilder(
      final String languageKey, final String resourceKey, final String defaultLanguage) {
    this.languageKey = requireNonNull(languageKey, "No language key provided");
    this.resourceKey = requireNonNull(resourceKey, "No resource key provided");
    this.defaultLanguage = requireNonNull(defaultLanguage, "No default language provided");
  }

  @Override
  public LanguageOptionsBuilder<O> fromConfig(final Config config) {
    script = getScript(config);
    // Language may be inferred from script extension, so set it afterwards
    language = getLanguage(config);
    return this;
  }

  @Override
  public LanguageOptionsBuilder<O> fromOptions(final LanguageOptions options) {
    if (options != null) {
      language = options.getLanguage();
      script = options.getScript();
    }
    return this;
  }

  public String getLanguage() {
    return language;
  }

  public String getScript() {
    return script;
  }

  public void setLanguage(final String language) {
    this.language = language;
  }

  public void setScript(final String script) {
    this.script = script;
  }

  @Override
  public Config toConfig() {
    throw new UnsupportedOperationException();
  }

  private final String getLanguage(final Config config) {
    // Check if language is specified
    final String language = config.getStringValue(languageKey, null);
    if (!isBlank(language)) {
      return language;
    }

    // Use the script file extension if the language is not specified
    final String fileExtension = getFileExtension(script);
    if (!isBlank(fileExtension)) {
      return fileExtension;
    }

    return defaultLanguage;
  }

  private String getScript(final Config config) {
    return config.getStringValue(resourceKey, null);
  }
}

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

package schemacrawler.tools.command.script.options;

import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.LanguageOptions;
import schemacrawler.tools.options.LanguageOptionsBuilder;

public final class ScriptLanguageOptionsBuilder extends LanguageOptionsBuilder<ScriptOptions> {

  public static ScriptLanguageOptionsBuilder builder() {
    return new ScriptLanguageOptionsBuilder();
  }

  private Config config;

  private ScriptLanguageOptionsBuilder() {
    super("script-language", "script", "javascript");
    config = new Config();
  }

  @Override
  public ScriptLanguageOptionsBuilder fromConfig(final Config config) {
    super.fromConfig(config);
    this.config = new Config(config);
    return this;
  }

  @Override
  public ScriptLanguageOptionsBuilder fromOptions(final LanguageOptions options) {
    super.fromOptions(options);
    if (options != null) {
      config = ((ScriptOptions) options).getConfig();
    }
    return this;
  }

  @Override
  public ScriptOptions toOptions() {
    return new ScriptOptions(getLanguage(), getScript(), config);
  }
}

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

import static us.fatehi.utility.Utility.requireNotBlank;
import static us.fatehi.utility.ioresource.InputResourceUtility.createInputResource;

import java.util.Optional;

import schemacrawler.tools.executable.CommandOptions;
import us.fatehi.utility.ioresource.InputResource;

public abstract class LanguageOptions implements CommandOptions {

  private final String language;
  private final String script;

  public LanguageOptions(final String language, final String script) {
    this.language = requireNotBlank(language, "No language provided");
    this.script = requireNotBlank(script, "No script provided");
  }

  public String getLanguage() {
    return language;
  }

  public final Optional<InputResource> getResource() {
    return createInputResource(script);
  }

  public String getScript() {
    return script;
  }
}

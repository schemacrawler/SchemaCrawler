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
package schemacrawler.tools.integration.script;

import java.util.HashMap;
import java.util.Map;

import schemacrawler.tools.integration.LanguageOptions;

public class ScriptOptions extends LanguageOptions {

  private final Map<String, Object> config;

  public ScriptOptions(
      final String language, final String script, final Map<String, Object> config) {
    super(language, script);
    if (config == null) {
      this.config = new HashMap<>();
    } else {
      this.config = config;
    }
  }

  public Map<String, Object> getConfig() {
    return new HashMap<>(config);
  }
}

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

package schemacrawler.tools.command.script;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

abstract class AbstractScriptExecutor implements ScriptExecutor {

  protected final String scriptingLanguage;
  protected Reader reader;
  protected Writer writer;
  protected Map<String, Object> context;

  protected AbstractScriptExecutor(final String scriptingLanguage) {
    this.scriptingLanguage = requireNotBlank(scriptingLanguage, "No scripting language provided");
  }

  @Override
  public void initialize(
      final Map<String, Object> context, final Reader reader, final Writer writer) {

    this.reader = requireNonNull(reader, "No script input resource provided");
    this.writer = requireNonNull(writer, "No output writer provided");

    if (context == null) {
      this.context = Collections.emptyMap();
    } else {
      this.context = new HashMap<>(context);
    }
  }
}

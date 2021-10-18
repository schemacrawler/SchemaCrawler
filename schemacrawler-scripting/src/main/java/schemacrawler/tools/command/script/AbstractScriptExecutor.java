/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import us.fatehi.utility.ioresource.InputResource;

abstract class AbstractScriptExecutor implements ScriptExecutor {

  private static final Logger LOGGER = Logger.getLogger(AbstractScriptExecutor.class.getName());

  protected final Charset inputCharset;
  protected final InputResource scriptResource;
  protected final Writer writer;
  protected final String scriptingLanguage;
  protected Map<String, Object> context;

  protected AbstractScriptExecutor(
      final String scriptingLanguage,
      final Charset inputCharset,
      final InputResource scriptResource,
      final Writer writer) {
    this.scriptingLanguage = requireNotBlank(scriptingLanguage, "No scripting language provided");
    this.inputCharset = requireNonNull(inputCharset, "No input encoding provided");
    this.scriptResource = requireNonNull(scriptResource, "No script input resource provided");
    this.writer = requireNonNull(writer, "No output writer provided");
    context = Collections.emptyMap();
  }

  @Override
  public void setContext(final Map<String, Object> context) {
    if (context == null) {
      this.context = Collections.emptyMap();
    }
    this.context = new HashMap<>(context);
  }
}
